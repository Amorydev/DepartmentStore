package com.amory.departmentstore.activity.user

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.activity.MainActivity
import com.amory.departmentstore.activity.admin.AdminActivity
import com.amory.departmentstore.activity.user.resetPassword.VerifyEmailActivity
import com.amory.departmentstore.databinding.ActivityDangNhapBinding
import com.amory.departmentstore.model.LoginModel
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import io.paperdb.Paper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangNhapBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var customProgressDialog: Dialog

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangNhapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        RetrofitClient.init(this)
        init()
        setUpObserver()
        onCLickListener()
    }

    private fun onCLickListener() {
        binding.txtDangkingay.setOnClickListener { startRegisterActivity() }
        binding.btnDangnhap.setOnClickListener { login() }
        binding.txtQuenmatkhau.setOnClickListener { forgetPassword() }
    }

    private fun setUpObserver() {
        viewModel.loginResult.observe(this) { loginResult ->
            binding.prgbar.visibility = View.GONE
            if (loginResult.message == "Login successfully") {
                saveTokenInSharedPreferences(loginResult)
                Log.d("Login", "Đăng nhập thành công")
                viewModel.getUserInfo()
            } else {
                binding.prgbar.visibility = View.GONE
                Toast.makeText(applicationContext, "Đăng nhập không thành công", Toast.LENGTH_SHORT)
                    .show()
                binding.txtSaimatkhau.visibility = View.VISIBLE
            }
        }
        viewModel.infoUser.observe(this) { infoUser ->
            Utils.user_current = infoUser
            handleLogicOnSuccess(infoUser)
        }
    }

    private fun saveTokenInSharedPreferences(loginResult: LoginModel?) {
        val editor = sharedPreferences.edit()
        editor.putString("token", loginResult!!.access_token)
        editor.putString("refreshToken", loginResult.refresh_token)
        editor.apply()
    }


    private fun handleLogicOnSuccess(infoUser: User?) {
        if (infoUser!!.active) {
            val intent = if (infoUser.role.id == 1) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this@LoginActivity, AdminActivity::class.java)
            }
            if (infoUser.role.id == 1) {
                if (binding.checkBoxNhodangnhap.isChecked) {
                    Paper.book().write("isLogin", true)
                    Paper.book().write("email", infoUser.email)
                    Paper.book().write("password", infoUser.password)
                    Paper.book().write("checked", true)
                }
                Paper.book().write("user", infoUser)
                val userId = infoUser.id
                viewModel.saveTokenFCMUser(userId)
            } else {
                val adminId = infoUser.id
                viewModel.saveTokenFCMAdmin(adminId)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        } else {
            showBlockUserDialog()
        }
    }

    private fun showBlockUserDialog() {
        customProgressDialog = Dialog(this@LoginActivity)
        val view = LayoutInflater.from(this@LoginActivity)
            .inflate(R.layout.alert_dialog_blocked, null)
        customProgressDialog.setContentView(view)
        customProgressDialog.setCancelable(true)
        customProgressDialog.show()
    }

    private fun startRegisterActivity() {
            val intent = Intent(this, DangKiActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
    }

    private fun init() {
        binding.prgbar.visibility = View.INVISIBLE
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

    }

    private fun login() {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passET.text.toString().trim()
            if (validateInput(email, password)) {
                binding.prgbar.visibility = View.VISIBLE
                viewModel.login(email, password)
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



    private fun forgetPassword() {
            val intent = Intent(this, VerifyEmailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
    }
}