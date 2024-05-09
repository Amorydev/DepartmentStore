package com.amory.departmentstore.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityLienHeBinding

class LienHeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLienHeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLienHeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClick()
        onClickBack()
    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun onClick() {
        binding.imvFb.setOnClickListener {
            openWebPage("https://facebook.com/amory.dev/")
        }
        binding.txtFb.setOnClickListener {
            openWebPage("https://facebook.com/amory.dev/")
        }
        binding.imvInsta.setOnClickListener {
            openWebPage("https://facebook.com/amory.dev/")
        }
        binding.txtInsta.setOnClickListener {
            openWebPage("https://acebook.com/amory.dev/")
        }
        binding.imvGithub.setOnClickListener {
            openWebPage("https://github.com/Amorydev")
        }
        binding.txtGit.setOnClickListener {
            openWebPage("https://github.com/Amorydev")
        }
        binding.imvEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("amory.dev@gmail.com"))
            startActivity(Intent.createChooser(intent, "Send email"))
        }
        binding.imvEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("amory.dev@gmail.com"))
            startActivity(Intent.createChooser(intent, "Send email"))
        }
    }
    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}