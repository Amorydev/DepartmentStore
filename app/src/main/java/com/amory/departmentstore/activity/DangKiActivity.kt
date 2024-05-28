package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityDangKiBinding
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.RegisterModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DangKiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangKiBinding
    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangKiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFirebaseAuth = FirebaseAuth.getInstance()
        initViews()
        onClickBack()
    }

    private fun onClickBack() {
        binding.btnBackdangki.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun initViews() {
        binding.txtBatbuocnhappass.visibility = View.INVISIBLE
        binding.txtBatbuocnhapemail.visibility = View.INVISIBLE
        binding.txtPasskhongkhop.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnDangki.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val specialChar = Regex("[^A-Za-z0-9 ]").containsMatchIn(password)
        val lowerCase = Regex("[a-z]").containsMatchIn(password)
        val digit = Regex("[0-9]").containsMatchIn(password)
        return specialChar && lowerCase && digit
    }

    private fun validateAndRegister() {
        binding.progressBar.visibility = View.VISIBLE

        val firstName = binding.edtFirstname.text?.trim().toString()
        val lastName = binding.edtLastname.text?.trim().toString()
        val email = binding.emailEt.text?.trim().toString()
        val password = binding.passET.text?.trim().toString()
        val repassword = binding.repassET.text?.trim().toString()

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

        if (password != repassword) {
            binding.txtPasskhongkhop.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            isValid = false
        } else {
            binding.txtPasskhongkhop.visibility = View.INVISIBLE
        }

        if (isValid) {
            registerUser(firstName, lastName, email, password)
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
        val call = service.dangkitaikhoan(firstName, lastName, email, password, 1)

        call.enqueue(object : Callback<RegisterModel> {
            override fun onResponse(call: Call<RegisterModel>, response: Response<RegisterModel>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (response.body()?.status.equals(Constant.SUCCESS)) {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@DangKiActivity,
                                "Đăng kí thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@DangKiActivity, DangNhapActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (response.body()?.status.equals(Constant.EMAIL_DUPLICATED)) {
                            binding.txtBatbuocnhapemail.text = "Email đã tồn tại"
                            binding.txtBatbuocnhapemail.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                        }else{
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@DangKiActivity,
                                "Đăng kí thất bại",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }

            override fun onFailure(call: Call<RegisterModel>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                /*Log.d("Error Register", t.message.toString())*/
                Toast.makeText(this@DangKiActivity, "Đăng kí không thành công", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}
