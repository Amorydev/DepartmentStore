package com.amory.departmentstore.activity.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.activity.MainActivity
import com.amory.departmentstore.activity.admin.AdminActivity
import com.amory.departmentstore.activity.user.resetPassword.VerifyEmailActivity
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
        onClickQuenMatKhau()
    }

    private fun onCLickDangKi() {
        binding.txtDangkingay.setOnClickListener {
            val intent = Intent(this, DangKiActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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
        binding.txtSaimatkhau.visibility = View.INVISIBLE
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = this.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)
        /*isChiTietDatHang = intent.getBooleanExtra("fromActivity",false)
        idsanpham = intent.getIntExtra("fromActivity_id",0)
        tensanpham = intent.getStringExtra("fromActivity_name").toString()
        giasanpham = intent.getFloatExtra("fromActivity_price",0f)
        hinhanhsanpham = intent.getStringExtra("fromActivity_imageUrl").toString()
        motasanpham = intent.getStringExtra("fromActivity_description").toString()*/

    }

    private fun onClickDangNhap() {
        binding.prgbar.visibility = View.INVISIBLE
        binding.btnDangnhap.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passET.text.toString().trim()
            if (validateInput(email, password)) {
                binding.prgbar.visibility = View.VISIBLE
                dangNhap(email, password)
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
                    if (response.body()?.message.equals("Login successfully")) {
                        val callUser = service.getUser()
                        callUser.enqueue(object : Callback<User> {
                            override fun onResponse(
                                call: Call<User>,
                                response: Response<User>
                            ) {
                                if (response.isSuccessful) {
                                    val userRole = response.body()?.role?.id
                                    /*Toast.makeText(this@DangNhapActivity,userRole.toString(),Toast.LENGTH_SHORT).show()*/
                                    if (response.body()?.active == true) {
                                        val intent = if (userRole == 1) {
                                            Intent(this@DangNhapActivity, MainActivity::class.java)
                                        } else {
                                            Intent(this@DangNhapActivity, AdminActivity::class.java)
                                        }

                                        if (userRole == 1) {
                                            if (binding.checkBoxNhodangnhap.isChecked) {
                                                Paper.book().write("isLogin", true)
                                                Paper.book().write("email", email)
                                                Paper.book().write("password", password)
                                                Paper.book().write("checked", true)
                                            }
                                            Paper.book().write("user", response.body()!!)
                                            val userId = response.body()?.id
                                            saveTokenFCMUser(userId)
                                        } else {
                                            val adminId = response.body()?.id
                                            saveTokenFCMAdmin(adminId)
                                        }
                                        Utils.user_current = response.body()!!
                                        Toast.makeText(
                                            applicationContext,
                                            "Đăng nhập thành công",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        /*if (isChiTietDatHang){
                                        intent.putExtra("name",tensanpham)
                                        intent.putExtra("price",giasanpham)
                                        intent.putExtra("hinhanhsanpham",hinhanhsanpham)
                                        intent.putExtra("idsanpham",idsanpham)
                                        intent.putExtra("motasanpham",motasanpham)
                                    }*/
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<User>, t: Throwable) {
                                t.printStackTrace()
                                /*  Log.d("LoiCallMe", t.message.toString())*/
                            }
                        })
                    }else {
                        Toast.makeText(
                            applicationContext,
                            "Tài khoản đã bị khóa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                binding.prgbar.visibility = View.GONE
                Toast.makeText(applicationContext, "Đăng nhập không thành công", Toast.LENGTH_SHORT)
                    .show()
                /* Log.d("dangnhap", t.message.toString())*/
                binding.txtSaimatkhau.visibility = View.VISIBLE
            }
        })
    }

    private fun saveTokenFCMUser(userId: Int?) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (!TextUtils.isEmpty(token)) {
                    sendToFirebase(token, userId!!)
                }
            }
    }

    private fun saveTokenFCMAdmin(adminId: Int?) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (!TextUtils.isEmpty(token)) {
                    sendToFirebase(token, adminId!!)
                }
            }
    }

    private fun sendToFirebase(token: String, userId: Int) {
        val db = FirebaseFirestore.getInstance()
        val tokensCollection = db.collection("tokens")

        tokensCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val useToken = hashMapOf(
                        "token" to token,
                        "userId" to userId
                    )
                    tokensCollection
                        .add(useToken)
                        .addOnSuccessListener {
                            /* Log.d("Token", "Token added successfully") */
                        }
                        .addOnFailureListener {
                            /* Log.d("Error token", it.message.toString()) */
                        }
                } else {
                    for (document in querySnapshot.documents) {
                        document.reference
                            .update("token", token)
                            .addOnSuccessListener {
                                /* Log.d("Token", "Token updated successfully") */
                            }
                            .addOnFailureListener {
                                /* Log.d("Error token", it.message.toString()) */
                            }
                    }
                }
            }
            .addOnFailureListener {
                /* Log.d("Error token", it.message.toString()) */
            }
    }

    private fun onClickQuenMatKhau() {
        binding.txtQuenmatkhau.setOnClickListener {
            val intent = Intent(this, VerifyEmailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}