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
import com.amory.departmentstore.model.LoginModel
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        RetrofitClient.init(this)
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
        sharedPreferences = this.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)

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
                Toast.makeText(this, "Vui lòng nhập đúng định dạng email.", Toast.LENGTH_SHORT)
                    .show()
                false
            }

            else -> true
        }
    }

    private fun dangNhap(email: String, password: String) {
        val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
        val call = service.dangnhaptaikhoan(email, password)
        call.enqueue(object : Callback<LoginModel> {
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                binding.prgbar.visibility = View.GONE
                if (response.isSuccessful) {
                    /* Toast.makeText(this@DangNhapActivity,Utils.user_current.toString(),Toast.LENGTH_SHORT).show()*/
                    val editor = sharedPreferences.edit()
                    editor.putString("token", response.body()?.access_token)
                    editor.putString("refreshToken", response.body()?.refresh_token)
                    editor.apply()
                    val callUser = service.getUser()
                    callUser.enqueue(object : Callback<User> {
                        override fun onResponse(
                            call: Call<User>,
                            response: Response<User>
                        ) {
                            if (response.isSuccessful) {
                                val userRole = response.body()?.role?.id
                                Toast.makeText(this@DangNhapActivity,userRole.toString(),Toast.LENGTH_SHORT).show()
                                val intent = if (userRole == 1) {
                                    Intent(this@DangNhapActivity, MainActivity::class.java)
                                } else {
                                    Intent(this@DangNhapActivity, AdminActivity::class.java)
                                }

                                if (userRole == 1) {
                                    if (binding.checkBoxNhodangnhap.isChecked) {
                                        Paper.book().write("isLogin", true)
                                        Paper.book().write("user", response.body()!!)
                                        Paper.book().write("email", email)
                                        Paper.book().write("password", password)
                                        Paper.book().write("checked", true)
                                    }
                                } else {
                                    val adminId = response.body()?.id
                                    saveTokenAdmin(adminId)
                                }
                                Utils.user_current = response.body()!!
                                Toast.makeText(
                                    applicationContext,
                                    "Đăng nhập thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            t.printStackTrace()
                            Log.d("LoiCallMe", t.message.toString())
                        }
                    })
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Đăng nhập không thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                binding.prgbar.visibility = View.GONE
                Toast.makeText(applicationContext, "Đăng nhập không thành công", Toast.LENGTH_SHORT)
                    .show()
                Log.d("dangnhap", t.message.toString())
            }
        })
    }

    private fun saveTokenAdmin(adminId: Int?) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (!TextUtils.isEmpty(token)) {
                    sendToFirebase(token, adminId!!)
                }
            }
    }

    private fun sendToFirebase(token: String, userId: Int) {
        val db = FirebaseFirestore.getInstance()
        val useToken = hashMapOf(
            "token" to token,
            "userId" to userId
        )
        db.collection("tokens")
            .add(useToken)
            .addOnSuccessListener {
                Log.d("Token", "Thanh cong")
            }
            .addOnFailureListener {
                Log.d("Error token", it.message.toString())
            }
    }

    override fun onResume() {
        super.onResume()
        val emailPaper = Paper.book().read<String>("email")
        val passwordPaper = Paper.book().read<String>("password")
        if (emailPaper!!.isNotEmpty() && passwordPaper!!.isNotEmpty()) {
            binding.emailEt.setText(emailPaper)
            binding.passET.setText(passwordPaper)
        }
    }
}
