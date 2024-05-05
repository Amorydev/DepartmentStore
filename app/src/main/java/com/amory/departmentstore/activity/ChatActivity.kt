package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvChatAdapter
import com.amory.departmentstore.adapter.Utils
import com.amory.departmentstore.databinding.ActivityChatBinding
import com.amory.departmentstore.model.ChatMessage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var list: ArrayList<ChatMessage>
    private lateinit var adapter: RvChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        onClickChat()
        getChat()
        insertUser()
        onCLickBack()
    }

    private fun onCLickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun insertUser() {
        val name = Utils.user_current?.first_name + Utils.user_current?.last_name
        val user:HashMap<String,Any> = hashMapOf()
        user["id"] = Utils.user_current?.id.toString()
        user["username"] = name
        db.collection("user").document(Utils.user_current?.id.toString()).set(user)
    }

    private fun onClickChat() {
        binding.btnSend.setOnClickListener {
            sendToFirebase()
        }
    }

    private fun sendToFirebase() {
        val txt_chat = binding.txtChat.text.toString().trim()
        if (txt_chat.isNotEmpty()) {
            val message: HashMap<String, Any> = hashMapOf()
            message[Utils.GUI_ID] = Utils.user_current?.id.toString()
            message[Utils.NHAN_ID] = Utils.ID_NHAN
            message[Utils.MESS] = txt_chat
            message[Utils.DATE_TIME] = Timestamp.now()
            db.collection(Utils.PATH)
                .add(message)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, " ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error", e)
                }
        }
        binding.txtChat.setText("")
        /*Toast.makeText(applicationContext, list.toString(), Toast.LENGTH_SHORT).show()*/
    }

    private fun getChat() {
        db.collection(Utils.PATH)
            .whereEqualTo(Utils.GUI_ID, Utils.user_current?.id.toString())
            .whereEqualTo(Utils.NHAN_ID, Utils.ID_NHAN)
            .addSnapshotListener(eventListener)
        db.collection(Utils.PATH)
            .whereEqualTo(Utils.GUI_ID, Utils.ID_NHAN)
            .whereEqualTo(Utils.NHAN_ID, Utils.user_current?.id.toString())
            .addSnapshotListener(eventListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            return@EventListener
        }
        if (value != null) {
            val count = list.size
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val chatMessage = ChatMessage(
                        documentChange.document.get(Utils.GUI_ID).toString(),
                        documentChange.document.get(Utils.NHAN_ID).toString(),
                        documentChange.document.get(Utils.MESS).toString(),
                        documentChange.document.getTimestamp(Utils.DATE_TIME),
                        documentChange.document.getDate(Utils.DATE_TIME)!!
                    )
                    list.add(chatMessage)
                }
            }
            list.sortWith(Comparator { obj1, obj2 -> obj1.dateObj?.compareTo(obj2.dateObj)!! })
            if (count == 0) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyItemRangeInserted(list.size, list.size)
                binding.rvChat.smoothScrollToPosition(list.size - 1)
            }
        } else {
            Log.d("list", "data: null")
        }
    }

    private fun initViews() {
        list = ArrayList()
        db = FirebaseFirestore.getInstance()
        binding.rvChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvChat.setHasFixedSize(true)
        adapter = RvChatAdapter(list, Utils.user_current?.id.toString())
        binding.rvChat.adapter = adapter
    }
}