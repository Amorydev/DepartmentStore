package com.amory.departmentstore.activity.admin

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvAdminChatUser
import com.amory.departmentstore.databinding.ActivityAdminChatUserBinding
import com.amory.departmentstore.model.Role
import com.amory.departmentstore.model.User
import com.google.firebase.firestore.FirebaseFirestore

class AdminChatUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminChatUserBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var listUser: ArrayList<User>
    private lateinit var adapter: RvAdminChatUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminChatUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        getUser()
        onClickBacK()
    }

    private fun onClickBacK() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getUser() {
        db.collection("user")
            .get()
            .addOnSuccessListener {
                for (documents in it.documentChanges) {
                    val user = User(
                        documents.document.getString("id")!!.toInt(),
                        documents.document.getString("username")!!,
                        " ",
                        "",
                        "",
                        true,
                        Role(1,"user")
                    )
                    listUser.add(user)
                }
                /*Toast.makeText(this, listUser.toString(), Toast.LENGTH_SHORT).show()*/
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e("amory", it.message.toString())
            }
    }

    private fun initViews() {
        listUser = ArrayList()
        db = FirebaseFirestore.getInstance()
        binding.rvUserChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvUserChat.setHasFixedSize(true)
        adapter = RvAdminChatUser(listUser)
        binding.rvUserChat.adapter = adapter
    }
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}