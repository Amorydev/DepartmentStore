package com.amory.departmentstore.activity.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.activity.MainActivity
import com.amory.departmentstore.databinding.ActivityChangePasswordBinding
import com.amory.departmentstore.model.UpdatePasswordModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.txtValidatenewpass.visibility = View.INVISIBLE
        onClickBack()
        onClicXacNhan()
    }

    private fun onClicXacNhan() {
        binding.btnXacNhan.setOnClickListener {
            val password = binding.passET?.text?.trim().toString()
            val newPassword = binding.newpassET.text?.trim().toString()
            val reNewPassword = binding.reNewPassET.text?.trim().toString()
            if (isValidPassword(newPassword) && isValidPassword(reNewPassword)){
                if (newPassword == reNewPassword){
                    val passwordRequestBody =  RequestBody.create("text/plain".toMediaTypeOrNull(), password)
                    val newPasswordRequestBody =  RequestBody.create("text/plain".toMediaTypeOrNull(), newPassword)
                    val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
                    val call = service.changePassword(passwordRequestBody,newPasswordRequestBody)
                    call.enqueue(object : Callback<UpdatePasswordModel>{
                        override fun onResponse(
                            call: Call<UpdatePasswordModel>,
                            response: Response<UpdatePasswordModel>
                        ) {
                            if (response.isSuccessful){
                                val intent = Intent(this@ChangePasswordActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }else{
                                Log.d("Error", response.body().toString())
                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    "Không thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<UpdatePasswordModel>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(this@ChangePasswordActivity, "Sửa thất bại", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("ChangePassword", t.message.toString())
                        }
                    })
                }else{
                    binding.txtValidatenewpass.text = "Mật khẩu phải trùng khớp"
                    binding.txtValidatenewpass.visibility = View.VISIBLE
                }
            }else{
                binding.txtValidatenewpass.visibility = View.VISIBLE
            }
        }
    }
    private fun isValidPassword(password: String): Boolean {
        val specialChar = Regex("[^A-Za-z0-9 ]").containsMatchIn(password)
        val lowerCase = Regex("[a-z]").containsMatchIn(password)
        val digit = Regex("[0-9]").containsMatchIn(password)
        return specialChar && lowerCase && digit
    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}