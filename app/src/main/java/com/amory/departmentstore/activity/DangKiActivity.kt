package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityDangKiBinding
import com.amory.departmentstore.model.DangKiModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class DangKiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangKiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDangKiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnDangki.setOnClickListener {
            kiemTraDuLieu()
        }
    }

    private fun kiemTraDuLieu() {
        binding.progressBar.visibility = View.VISIBLE
        val first_name = binding.edtFirstname.text.toString()
        val last_name = binding.edtLastname.text.toString()
        val email = binding.emailEt.text.toString()
        val password = binding.passET.text.toString()
        val repassword = binding.repassET.text.toString()
        val mobile = binding.mobileET.text.toString()

        if (first_name.isEmpty() && last_name.isEmpty() && email.isEmpty() && password.isEmpty() && repassword.isEmpty() && mobile.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ dữ liệu", Toast.LENGTH_SHORT).show()
        } else {
            Handler().postDelayed({
                if (password == repassword) {
                    val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
                    val call = service.dangkitaikhoan(first_name, last_name, email, password, mobile)
                    call.enqueue(object : Callback<DangKiModel> {
                        override fun onResponse(
                            call: Call<DangKiModel>,
                            response: Response<DangKiModel>
                        ) {
                            if (response.isSuccessful) {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    applicationContext,
                                   "Đăng kí thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<DangKiModel>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                } else {
                    Toast.makeText(this, "Password không trùng khớp", Toast.LENGTH_SHORT).show()
                }
            },2000)

        }
        /*kiem tra dinh dang email*/
        if (!email.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }) {
            Toast.makeText(this, "Vui lòng nhập đúng định dạng email", Toast.LENGTH_SHORT).show()
        }

    }

}