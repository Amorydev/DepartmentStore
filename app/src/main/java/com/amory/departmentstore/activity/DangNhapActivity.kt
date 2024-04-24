package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.adapter.Utils
import com.amory.departmentstore.databinding.ActivityDangNhapBinding
import com.amory.departmentstore.model.User
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DangNhapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDangNhapBinding
    private lateinit var user: User
    private var isLogin = false

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
        if (emailPaper != null && passwordPaper != null) {
            binding.emailEt.setText(emailPaper)
            binding.passET.setText(passwordPaper)
        }
    }

    private fun onClickDangNhap() {
        binding.prgbar.visibility = View.INVISIBLE
        binding.btnDangnhap.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passET.text.toString().trim()

            binding.prgbar.visibility = View.VISIBLE
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && !Patterns.EMAIL_ADDRESS.matcher(
                    email
                ).matches()
            ) {
                Toast.makeText(
                    applicationContext,
                    "Vui lòng nhập đầy đủ email và password",
                    Toast.LENGTH_SHORT
                ).show()
                binding.prgbar.visibility = View.INVISIBLE
            } else {
                Utils.user_current?.email = email
                Utils.user_current?.password = password
                val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
                val call = service.dangnhaptaikhoan(email, password)
                call.enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
                            Handler().postDelayed({
                                if (response.body()?.result?.isNotEmpty()!!) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Đăng nhập thành công",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isLogin = true
                                    Paper.book().write("isLogin", isLogin)
                                    binding.prgbar.visibility = View.GONE
                                    Utils.user_current = response.body()?.result?.get(0)!!


                                    if (Utils.user_current!!.email != "admin") {
                                        Paper.book().write("user", Utils.user_current!!)
                                        Paper.book().write("email", email)
                                        Paper.book().write("password", password)
                                        dangNhap()
                                    } else {
                                        val intent =
                                            Intent(this@DangNhapActivity, AdminActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    binding.prgbar.visibility = View.GONE
                                    Toast.makeText(
                                        applicationContext,
                                        "Đăng nhập không thành công",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, 1000)

                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
        }

    }

    private fun dangNhap() {
        val intent = Intent(this@DangNhapActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (::user.isInitialized && Utils.user_current?.email?.isEmpty()!! && Utils.user_current?.password?.isEmpty()!!) {
            binding.emailEt.setText(Utils.user_current?.email)
            binding.passET.setText(Utils.user_current?.password)
        }
    }
}