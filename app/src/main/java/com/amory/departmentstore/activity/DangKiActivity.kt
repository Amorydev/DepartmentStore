package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityDangKiBinding
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DangKiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangKiBinding
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangKiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dangki()
    }

    private fun dangki() {
        binding.txtBatbuocnhappass.visibility = View.INVISIBLE
        binding.txtBatbuocnhaprepass.visibility = View.INVISIBLE
        binding.txtBatbuocnhapemail.visibility = View.INVISIBLE
        binding.txtBatbuocnhapmobile.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnDangki.setOnClickListener {
            kiemTraDuLieu()
        }
    }

    fun isValidPassword(password: String): Boolean {
        val kitudacbiet = Regex("[^A-Za-z0-9 ]").containsMatchIn(password)
        val kituthuong = Regex("[a-z]").containsMatchIn(password)
        val kituso = Regex("[0-9]").containsMatchIn(password)

        return kitudacbiet && kituthuong && kituso
    }

    private fun kiemTraDuLieu() {
        binding.progressBar.visibility = View.VISIBLE
        val first_name = binding.edtFirstname.text?.trim().toString()
        val last_name = binding.edtLastname.text?.trim().toString()
        val email = binding.emailEt.text?.trim().toString()
        val password = binding.passET.text?.trim().toString()
        val repassword = binding.repassET.text?.trim().toString()
        val mobile = binding.mobileET.text?.trim().toString()

        if (first_name.isEmpty() && last_name.isEmpty()
            && email.isEmpty() && password.isEmpty()
            && repassword.isEmpty() && mobile.isEmpty()
            && !isValidPassword(password)
            &&!email.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } && email.isEmpty()) {
            binding.txtBatbuocnhapmobile.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(this, "Vui lòng nhập đủ dữ liệu", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.INVISIBLE
        } else {
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this@DangKiActivity
                ) { p0 ->
                    if (p0.isSuccessful) {
                        val user: FirebaseUser? = firebaseAuth.currentUser
                        if (user != null) {
                            postData(
                                first_name,
                                last_name,
                                email,
                                password,
                                repassword,
                                mobile,
                                user.uid
                            )
                        }
                    } else {
                        Toast.makeText(applicationContext, "Email đã tồn tại", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

        }
        /*kiem tra dinh dang email*/
        if (!email.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } && email.isEmpty()) {
            binding.txtBatbuocnhapemail.visibility = View.VISIBLE
            /*
                        Toast.makeText(this, "Vui lòng nhập đúng định dạng email", Toast.LENGTH_SHORT).show()
            */
        }
        if (!isValidPassword(password)) {
            binding.txtBatbuocnhappass.visibility = View.VISIBLE
        }

    }

    private fun postData(
        first_name: String,
        last_name: String,
        email: String,
        password: String,
        repassword: String,
        mobile: String,
        uid: String
    ) {
        val token = ""
        Handler().postDelayed({
            if (password == repassword) {
                val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
                val call =
                    service.dangkitaikhoan(first_name, last_name, email, password, mobile,uid,token)
                call.enqueue(object : Callback<UserModel> {
                    override fun onResponse(
                        call: Call<UserModel>,
                        response: Response<UserModel>
                    ) {
                        if (response.isSuccessful) {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                "Đăng kí thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                            Handler().postDelayed({
                                val intent =
                                    Intent(this@DangKiActivity, DangNhapActivity::class.java)
                                startActivity(intent)
                            }, 500)
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            "Đăng kí không thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                        t.printStackTrace()
                    }
                })
            } else {
                binding.txtBatbuocnhaprepass.visibility = View.VISIBLE
                Toast.makeText(this, "Password không trùng khớp", Toast.LENGTH_SHORT).show()
            }
        }, 1000)
    }
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

}