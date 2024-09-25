package com.amory.departmentstore.activity.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.amory.departmentstore.databinding.ActivityDangKiBinding
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.RegisterModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.viewModel.RegisterViewModel
import com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty.back
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangKiBinding
    private lateinit var mFirebaseAuth: FirebaseAuth
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangKiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFirebaseAuth = FirebaseAuth.getInstance()
        initViews()
        setUpObserver()
        onClickListener()
    }

    private fun initViews() {
        binding.txtBatbuocnhappass.visibility = View.INVISIBLE
        binding.txtBatbuocnhapemail.visibility = View.INVISIBLE
        binding.txtPasskhongkhop.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun setUpObserver() {
        viewModel.registerResult.observe(this) { registerResult ->
            checkStatusRegister(registerResult)
        }
    }


    private fun onClickListener() {
        binding.btnBackdangki.setOnClickListener { back() }
        binding.btnDangki.setOnClickListener { registerAccount() }
    }

    private fun back() {
        onBackPressedDispatcher.onBackPressed()
        finish()
    }


    private fun registerAccount() {
        val firstName = binding.edtFirstname.text?.trim().toString()
        val lastName = binding.edtLastname.text?.trim().toString()
        val email = binding.emailEt.text?.trim().toString()
        val password = binding.passET.text?.trim().toString()
        val rePassword = binding.repassET.text?.trim().toString()
        if (validateAndRegister(firstName, lastName, email, password, rePassword)) {
            viewModel.register(firstName, lastName, email, password)
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun checkStatusRegister(registerResult: RegisterModel?) {
        if (registerResult!!.status == Constant.SUCCESS) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                this@RegisterActivity,
                "Đăng kí thành công",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else if (registerResult.status == Constant.EMAIL_DUPLICATED) {
            binding.txtBatbuocnhapemail.text = "Email đã tồn tại"
            binding.txtBatbuocnhapemail.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                this@RegisterActivity,
                "Đăng kí thất bại",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val specialChar = Regex("[^A-Za-z0-9 ]").containsMatchIn(password)
        val lowerCase = Regex("[a-z]").containsMatchIn(password)
        val digit = Regex("[0-9]").containsMatchIn(password)
        return specialChar && lowerCase && digit
    }

    private fun validateAndRegister(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        rePassword: String
    ): Boolean {
        binding.progressBar.visibility = View.VISIBLE

        var isValid = true

        if (firstName.isEmpty()) {
            binding.edtFirstname.error = "Vui lòng nhập firstName"
            isValid = false
        }

        if (lastName.isEmpty()) {
            binding.edtLastname.error = "Vui lòng nhập lastName"
            isValid = false
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtBatbuocnhapemail.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtBatbuocnhapemail.visibility = View.INVISIBLE
        }

        if (!isValidPassword(password)) {
            binding.txtBatbuocnhappass.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.txtBatbuocnhappass.visibility = View.INVISIBLE
        }

        if (password != rePassword) {
            binding.txtPasskhongkhop.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            isValid = false
        } else {
            binding.txtPasskhongkhop.visibility = View.INVISIBLE
        }
        return isValid
    }
}
