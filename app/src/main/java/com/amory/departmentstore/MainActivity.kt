package com.amory.departmentstore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.amory.departmentstore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SlideDiscount()
    }

    private fun SlideDiscount() {
        val imageView1 = ImageView(this)
        imageView1.setImageResource(R.drawable.discount_1)
        imageView1.scaleType = ImageView.ScaleType.FIT_XY

        val imageView2 = ImageView(this)
        imageView2.setImageResource(R.drawable.discount_2)
        imageView2.scaleType = ImageView.ScaleType.FIT_XY
        val imageView3 = ImageView(this)
        imageView3.setImageResource(R.drawable.discount_2)
        imageView3.scaleType = ImageView.ScaleType.FIT_XY
        val imageView4 = ImageView(this)
        imageView4.setImageResource(R.drawable.discount_2)
        imageView4.scaleType = ImageView.ScaleType.FIT_XY

        binding.viewFlipper.addView(imageView1)
        binding.viewFlipper.addView(imageView2)
        binding.viewFlipper.addView(imageView3)
        binding.viewFlipper.addView(imageView4)

        val slide_in = AnimationUtils.loadAnimation(applicationContext,R.anim.slide_in_right)
        val slide_out = AnimationUtils.loadAnimation(applicationContext,R.anim.slide_out_right)
        /*Chuyển đổi ảnh*/
        binding.viewFlipper.flipInterval = 4000
        binding.viewFlipper.isAutoStart = true
        binding.viewFlipper.inAnimation = slide_out
        binding.viewFlipper.inAnimation = slide_in


    }
}