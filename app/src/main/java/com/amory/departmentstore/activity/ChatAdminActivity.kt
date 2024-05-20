package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvChatAdapter
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityChatAdminBinding
import com.amory.departmentstore.model.ChatMessage
import com.amory.departmentstore.model.Constant
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ChatAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatAdminBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var list: ArrayList<ChatMessage>
    private lateinit var adapter: RvChatAdapter
    private lateinit var ID_NHAN: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ID_NHAN = intent.getIntExtra("id", 0).toString()
        if (ID_NHAN.isNotEmpty()){
            initViews()
            onClickChat()
            getChat()
            onCLickBack()
        }
    }

    private fun onCLickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun onClickChat() {
        binding.btnSend.setOnClickListener {
            sendToFirebase()
        }
    }

    private fun sendToFirebase() {
        val txt_chat = binding.txtChat.text.toString().trim()
        if (txt_chat.isNotEmpty() && ID_NHAN.isNotEmpty()) {
            val message: HashMap<String, Any> = hashMapOf()
            message[Constant.GUI_ID] = Utils.user_current?.id.toString()
            message[Constant.NHAN_ID] = ID_NHAN
            message[Constant.MESS] = txt_chat
            message[Constant.DATE_TIME] = Timestamp.now()
            db.collection(Constant.PATH)
                .add(message)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, " ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error", e)
                }
        }
        binding.txtChat.setText("")
/*
        Toast.makeText(applicationContext, list.toString(), Toast.LENGTH_SHORT).show()
*/
    }

    private fun getChat() {
        db.collection(Constant.PATH)
            .whereEqualTo(Constant.GUI_ID, Utils.user_current?.id.toString())
            .whereEqualTo(Constant.NHAN_ID, ID_NHAN)
            .addSnapshotListener(eventListener)
        db.collection(Constant.PATH)
            .whereEqualTo(Constant.GUI_ID, ID_NHAN)
            .whereEqualTo(Constant.NHAN_ID, Utils.user_current?.id.toString())
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
                        documentChange.document.get(Constant.GUI_ID).toString(),
                        documentChange.document.get(Constant.NHAN_ID).toString(),
                        documentChange.document.get(Constant.MESS).toString(),
                        documentChange.document.getTimestamp(Constant.DATE_TIME),
                        documentChange.document.getDate(Constant.DATE_TIME)!!
                    )
                    list.add(chatMessage)
                }
            }
            list.sortWith(Comparator { obj1, obj2 -> obj1.dateObj?.compareTo(obj2.dateObj)!! })
            if (count == 0) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyItemRangeInserted(list.size, list.size)
                binding.rvChatAdmin.smoothScrollToPosition(list.size - 1)
            }
        } else {
            Log.d("list", "data: null")
        }
    }

    private fun initViews() {
        list = ArrayList()
        db = FirebaseFirestore.getInstance()
        val txt_user = intent.getStringExtra("username")
        binding.txtUsername.text = txt_user
        binding.rvChatAdmin.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvChatAdmin.setHasFixedSize(true)
        adapter = RvChatAdapter(list, Utils.user_current?.id.toString())
        binding.rvChatAdmin.adapter = adapter
    }
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}