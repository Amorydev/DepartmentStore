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
            val intent = Intent(this,DangKiActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun onClickChecked(email:String,password:String) {
        binding.checkBoxNhodangnhap.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                Paper.book().write("email",email)
                Paper.book().write("password",password)
            }
        }
    }

    private fun init(){
        Paper.init(this)
        val emailPaper = Paper.book().read<String>("email")
        val passwordPaper = Paper.book().read<String>("password")
        if (emailPaper != null && passwordPaper != null){
            binding.emailEt.setText(emailPaper)
            binding.passET.setText(passwordPaper)
        }
    }

    private fun onClickDangNhap() {
        binding.prgbar.visibility = View.INVISIBLE
        binding.btnDangnhap.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passET.text.toString().trim()
            onClickChecked(email,password)
            binding.prgbar.visibility = View.VISIBLE
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
                Toast.makeText(
                    applicationContext,
                    "Vui lòng nhập đầy đủ email và password",
                    Toast.LENGTH_SHORT
                ).show()
                binding.prgbar.visibility = View.INVISIBLE
            }else {

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
                                    binding.prgbar.visibility = View.GONE
                                    Utils.user = response.body()?.result?.get(0)!!
                                    val intent =
                                        Intent(this@DangNhapActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
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
    override fun onResume() {
        super.onResume()
        if (::user.isInitialized && Utils.user.email != null && Utils.user.password != null){
            binding.emailEt.setText(Utils.user.email)
            binding.passET.setText(Utils.user.password)
        }
    }
}