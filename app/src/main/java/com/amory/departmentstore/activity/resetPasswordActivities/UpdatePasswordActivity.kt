package com.amory.departmentstore.activity.resetPasswordActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.amory.departmentstore.R
import com.amory.departmentstore.activity.DangNhapActivity
import com.amory.departmentstore.databinding.ActivityUpdatePasswordBinding
import com.amory.departmentstore.model.UpdatePasswordModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdatePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickBack()
        ChangePassword()
    }

    private fun ChangePassword() {
        binding.btnNext.setOnClickListener {
            val newPassword = binding.passET.text?.trim().toString()
            val newPassRequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), newPassword)
            val email = intent.getStringExtra("email").toString()
            if (isValidPassword(newPassword)) {
                val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
                val call = service.updatePassword(email, newPassRequestBody)
                call.enqueue(object : Callback<UpdatePasswordModel> {
                    override fun onResponse(
                        call: Call<UpdatePasswordModel>,
                        response: Response<UpdatePasswordModel>
                    ) {
                        if (response.isSuccessful) {
                            val intent =
                                Intent(this@UpdatePasswordActivity, DangNhapActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d("Error", response.body().toString())
                            Toast.makeText(
                                this@UpdatePasswordActivity,
                                "Không thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdatePasswordModel>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(
                            this@UpdatePasswordActivity,
                            "Đổi password thất bại",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("newPassword", t.message.toString())
                    }
                })
            } else {
                Toast.makeText(
                    this@UpdatePasswordActivity,
                    "Vui lòng nhập mật khẩu đúng định dạng",
                    Toast.LENGTH_SHORT
                ).show()
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