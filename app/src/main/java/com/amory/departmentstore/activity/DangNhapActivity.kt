package com.amory.departmentstore.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityDangNhapBinding
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DangNhapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangNhapBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangNhapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        onCLickDangKi()
        onClickDangNhap()
    }

    private fun onCLickDangKi() {
        binding.txtDangkingay.setOnClickListener {
            val intent = Intent(this, DangKiActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun init() {
        Paper.init(this)
        val emailPaper = Paper.book().read<String>("email")
        val passwordPaper = Paper.book().read<String>("password")
        val checked = Paper.book().read<Boolean>("checked")
        if (checked == true) {
            binding.checkBoxNhodangnhap.isChecked = true
        }
        if (emailPaper != null && passwordPaper != null) {
            binding.emailEt.setText(emailPaper)
            binding.passET.setText(passwordPaper)
        }
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = this.getSharedPreferences("SAVE_TOKEN",Context.MODE_PRIVATE)
        RetrofitClient.init(this)
    }

    private fun onClickDangNhap() {
        binding.prgbar.visibility = View.INVISIBLE
        binding.btnDangnhap.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passET.text.toString().trim()

            if (validateInput(email, password)) {
                binding.prgbar.visibility = View.VISIBLE
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.currentUser?.let {
                                dangNhap(email, password)
                            }
                        } else {
                            binding.prgbar.visibility = View.INVISIBLE
                            Toast.makeText(
                                applicationContext,
                                "Authentication thất bại.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        binding.prgbar.visibility = View.INVISIBLE
                        Log.d("Error Login", it.message.toString())
                        Toast.makeText(
                            applicationContext,
                            "Error: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this, "Vui lòng nhập email.", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Vui lòng nhập password.", Toast.LENGTH_SHORT).show()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Vui lòng nhập đúng định dạng email.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun dangNhap(email: String, password: String) {
        val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
        val call = service.dangnhaptaikhoan(email, password)
        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                binding.prgbar.visibility = View.GONE
                if (response.isSuccessful) {
                    val editor = sharedPreferences.edit()
                    editor.putString("token",response.body()?.access_token)

                    val userRole = response.body()?.user?.role?.id
                    val intent = if (userRole == 1) {
                        Intent(this@DangNhapActivity, MainActivity::class.java)
                    } else {
                        Intent(this@DangNhapActivity, AdminActivity::class.java)
                    }
                    if (userRole == 1) {
                        if (binding.checkBoxNhodangnhap.isChecked) {
                            Paper.book().write("isLogin", true)
                            Paper.book().write("user", Utils.user_current!!)
                            Paper.book().write("email", email)
                            Paper.book().write("password", password)
                            Paper.book().write("checked", true)
                        }
                        saveTokenUser()
                    }else{
                        val adminId = response.body()?.user?.id!!.toInt()
                        editor.putInt("adminId", adminId)
                        saveTokenAdmin()
                    }
                    editor.apply()
                    Utils.user_current = response.body()?.user
                    Toast.makeText(applicationContext, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(applicationContext, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                binding.prgbar.visibility = View.GONE
                Toast.makeText(applicationContext, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show()
                Log.d("dangnhap", t.message.toString())
            }
        })
    }

    private fun saveTokenAdmin() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                if (!TextUtils.isEmpty(it)){
                    val editor = sharedPreferences.edit()
                    editor.putString("FMTokenAdmin",it.toString())
                    editor.apply()
                }
            }
    }

    private fun saveTokenUser() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                if (!TextUtils.isEmpty(it)){
                    val editor = sharedPreferences.edit()
                    editor.putString("FMTokenUser",it.toString())
                    editor.apply()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        Utils.user_current?.let {
            if (it.email.isNotEmpty() && it.password.isNotEmpty()) {
                binding.emailEt.setText(it.email)
                binding.passET.setText(it.password)
            }
        }
    }
}
