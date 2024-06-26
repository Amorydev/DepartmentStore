package com.amory.departmentstore.activity.user.resetPassword

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityVerifyOtpactivityBinding
import com.amory.departmentstore.model.UpdatePasswordModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyOtpactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickback()
        startCountdownTimer()
        xuLyET()
        verifyOTP()
    }

    private fun verifyOTP() {
        var otp: Int = 0
        val email = intent.getStringExtra("email").toString()
        Log.d("email",email)
        binding.txtEmail.text = email
        binding.btnNext.setOnClickListener {
            val otpEditText1 = binding.otpEditText1.text?.trim().toString()
            val otpEditText2 = binding.otpEditText2.text?.trim().toString()
            val otpEditText3 = binding.otpEditText3.text?.trim().toString()
            val otpEditText4 = binding.otpEditText4.text?.trim().toString()
            val otpEditText5 = binding.otpEditText5.text?.trim().toString()
            val otpEditText6 = binding.otpEditText6.text?.trim().toString()

            otp =
                (otpEditText1 + otpEditText2 + otpEditText3 + otpEditText4 + otpEditText5 + otpEditText6).toInt()


            val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
            val call = service.verifyOtp(otp, email)
            call.enqueue(object : Callback<UpdatePasswordModel> {
                override fun onResponse(
                    call: Call<UpdatePasswordModel>,
                    response: Response<UpdatePasswordModel>
                ) {
                    if (response.isSuccessful) {

                        val intent =
                            Intent(this@VerifyOTPActivity, UpdatePasswordActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@VerifyOTPActivity,
                            "Không thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<UpdatePasswordModel>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(
                        this@VerifyOTPActivity,
                        "OTP không hợp lệ!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("OTP", t.message.toString())
                }
            })
        }
    }
    private fun xuLyET() {
        val otpEditText1 = binding.otpEditText1
        val otpEditText2 = binding.otpEditText2
        val otpEditText3 = binding.otpEditText3
        val otpEditText4 = binding.otpEditText4
        val otpEditText5 = binding.otpEditText5
        val otpEditText6 = binding.otpEditText6
        val otpEditTexts = arrayOf(
            otpEditText1,
            otpEditText2,
            otpEditText3,
            otpEditText4,
            otpEditText5,
            otpEditText6
        )

        for (i in otpEditTexts.indices) {
            otpEditTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < otpEditTexts.size - 1) {
                        otpEditTexts[i + 1].requestFocus()
                    } else if (s?.length == 0 && i > 0) {
                        otpEditTexts[i - 1].requestFocus()
                    }
                    updateButtonColor()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun updateButtonColor() {
        val otp = binding.otpEditText1.text.toString() +
                binding.otpEditText2.text.toString() +
                binding.otpEditText3.text.toString() +
                binding.otpEditText4.text.toString() +
                binding.otpEditText5.text.toString() +
                binding.otpEditText6.text.toString()

        if (isValidOtp(otp)) {
            binding.btnNext.setBackgroundColor(ContextCompat.getColor(this, R.color.main))
        } else {
            binding.btnNext.setBackgroundColor(ContextCompat.getColor(this, R.color.color_shimmer))
        }
    }

    private fun isValidOtp(otp: String): Boolean {
        val otpPattern = Regex("^[0-9]{6}$")
        return otpPattern.matches(otp)
    }



    private fun onClickback() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun startCountdownTimer() {
        object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.txtTime.text =
                    "Mã xác thực của bạn có hiệu lực trong vòng ${millisUntilFinished / 1000}s"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.txtTime.text = "Mã xác thực đã hết hiệu lực."
            }
        }.start()
    }
}
