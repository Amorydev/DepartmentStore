package com.amory.departmentstore.activity.resetPasswordActivities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityVetifyEmailBinding
import com.amory.departmentstore.model.UpdatePasswordModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyEmailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityVetifyEmailBinding
    private lateinit var customProgressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVetifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onCLickBack()
        sendOTPEmail()
    }

    private fun sendOTPEmail() {
        binding.txtEmailsai.visibility = View.INVISIBLE
        binding.btnNext.setOnClickListener {
            val email = binding.emailEt.text?.trim().toString()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showCustomProgressBar()
                val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
                val call = service.sendOtpToEmail(email)
                call.enqueue(object : Callback<UpdatePasswordModel> {
                    override fun onResponse(
                        call: Call<UpdatePasswordModel>,
                        response: Response<UpdatePasswordModel>
                    ) {
                        if (response.isSuccessful) {
                            customProgressDialog.dismiss()
                            val intent =
                                Intent(this@VerifyEmailActivity, VerifyOTPActivity::class.java)
                            intent.putExtra("email", email)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            customProgressDialog.dismiss()
                            Log.d("Error", response.body().toString())
                            Toast.makeText(
                                this@VerifyEmailActivity,
                                "Không thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdatePasswordModel>, t: Throwable) {
                        customProgressDialog.dismiss()
                        t.printStackTrace()
                        binding.txtEmailsai.visibility = View.VISIBLE
                    }
                })
            }else{
                Toast.makeText(this@VerifyEmailActivity, "Vui lòng nhạp đúng định dạng email", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    @SuppressLint("InflateParams")
    private fun showCustomProgressBar() {
        customProgressDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_progressbar, null)
        customProgressDialog.setContentView(view)
        customProgressDialog.setCancelable(false)
        customProgressDialog.show()
    }

    private fun onCLickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}