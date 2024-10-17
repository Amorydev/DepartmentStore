package com.amory.departmentstore.activity.user

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvChatAdapter
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityChatBinding
import com.amory.departmentstore.model.ChatMessage
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.NotificationReponse
import com.amory.departmentstore.model.SendNotification
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APINotification.APIPushNotification
import com.amory.departmentstore.retrofit.APINotification.RetrofitNotification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var list: ArrayList<ChatMessage>
    private lateinit var adapter: RvChatAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ID_NHAN: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        onClickListener()
        getChat()
        insertUser()
    }

    private fun onClickListener() {
        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnSend.setOnClickListener { sendToFirebase() }
    }

    private fun initViews() {
        list = ArrayList()
        db = FirebaseFirestore.getInstance()
        binding.rvChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvChat.setHasFixedSize(true)
        val account = Paper.book().read<User>("user")
        adapter = if (account != null) {
            RvChatAdapter(list, account.id.toString())
        } else {
            RvChatAdapter(list, Utils.user_current?.id.toString())
        }
        binding.rvChat.adapter = adapter
        sharedPreferences = this.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)
        ID_NHAN = sharedPreferences.getInt("adminId", 5).toString()
    }

    private fun insertUser() {
        val name: String
        val user: HashMap<String, Any> = hashMapOf()
        val account = Paper.book().read<User>("user")
        if (account != null) {
            name = account.firstName + " " + account.lastName
            user["id"] = account.id.toString()
            user["username"] = name
            db.collection("user").document(account.id.toString()).set(user)
        } else {
            name = Utils.user_current?.firstName + " " + Utils.user_current?.lastName
            user["id"] = Utils.user_current?.id.toString()
            user["username"] = name
            db.collection("user").document(Utils.user_current?.id.toString()).set(user)
        }

    }

    private fun sendToFirebase() {
        val txt_chat = binding.txtChat.text.toString().trim()

        if (txt_chat.isNotEmpty()) {
            val message: HashMap<String, Any> = hashMapOf()
            val account = Paper.book().read<User>("user")
            val guiId = account?.id?.toString() ?: Utils.user_current?.id.toString()
            message[Constant.GUI_ID] = guiId
            message[Constant.NHAN_ID] = ID_NHAN
            message[Constant.MESS] = txt_chat
            message[Constant.DATE_TIME] = Timestamp.now()
            db.collection(Constant.PATH)
                .add(message)
                .addOnSuccessListener { documentReference ->
                    /*Log.d(TAG, " ${documentReference.id}")*/
                    val fullName = if (account == null) {
                        Utils.user_current?.firstName + " " + Utils.user_current?.lastName
                    } else {
                        account.firstName + " " + account.lastName
                    }
                    pushNotification(fullName, binding.txtChat.text.toString())
                }
                .addOnFailureListener { e ->
                    /*Log.w(TAG, "Error", e)*/
                }
        }
        binding.txtChat.setText("")
        /*Toast.makeText(applicationContext, list.toString(), Toast.LENGTH_SHORT).show()*/
    }

    private fun pushNotification(fullName: String, chat: String) {
        getTokenFCM(5) { token ->
            if (token == null) {
                Log.e("pushNotification", "Failed to retrieve FCM token")
                return@getTokenFCM
            }
            Log.d("tokenFCM", token)

            val data: MutableMap<String, String> = HashMap()
            data["body"] = chat
            data["title"] = "Bạn có tin nhắn mới từ $fullName"
            val sendNoti = SendNotification(token, data)
            Log.d("FCMMessage", sendNoti.toString())

            val service =
                RetrofitNotification.retrofitInstance.create(APIPushNotification::class.java)
            val call = service.sendNotification(sendNoti)
            call.enqueue(object : Callback<NotificationReponse> {
                override fun onResponse(
                    call: Call<NotificationReponse>,
                    response: Response<NotificationReponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("FCM Response", "Notification sent successfully")
                    } else {
                        Log.e(
                            "FCM Response",
                            "Failed to send notification. Response code: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<NotificationReponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("FCM Failure", "Error sending notification", t)
                }
            })
        }
    }

    private fun getTokenFCM(userId: Int, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val tokensCollection = db.collection("tokens")

        tokensCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    callback(null)
                } else {
                    val document = querySnapshot.documents[0]
                    val token = document.getString("token")
                    callback(token)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                callback(null)
            }
    }

    private fun getChat() {
        val account = Paper.book().read<User>("user")
        val user_id: String = account?.id?.toString() ?: Utils.user_current?.id.toString()
        /*Toast.makeText(applicationContext,user_id,Toast.LENGTH_SHORT).show()*/
        db.collection(Constant.PATH)
            .whereEqualTo(Constant.GUI_ID, user_id)
            .whereEqualTo(Constant.NHAN_ID, ID_NHAN)
            .addSnapshotListener(eventListener)
        db.collection(Constant.PATH)
            .whereEqualTo(Constant.GUI_ID, ID_NHAN)
            .whereEqualTo(Constant.NHAN_ID, user_id)
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
            list.sortWith(Comparator { obj1, obj2 -> obj1.dateObj.compareTo(obj2.dateObj) })
            if (count == 0) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyItemRangeInserted(list.size, list.size)
                binding.rvChat.smoothScrollToPosition(list.size - 1)
            }
        }
    }
}