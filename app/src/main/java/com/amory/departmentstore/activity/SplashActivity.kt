package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.amory.departmentstore.R
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.AuthInterceptor
import io.paperdb.Paper

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Paper.init(this)
        Handler().postDelayed({
            if (Paper.book().read<User>("user") == null){
                val intent = Intent(this, DangNhapActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        },1500)
    }
}