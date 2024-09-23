package com.amory.departmentstore.activity.admin

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityAdminAddKhuyenMaiBinding
import com.amory.departmentstore.model.Promotion
import com.amory.departmentstore.model.PromotionModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallBanners
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AdminAddKhuyenMaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAddKhuyenMaiBinding
    private var mediaPath = ""
    var flags = false
    var listPromotion: Promotion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddKhuyenMaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickAdd()
        onCLickThemHinhAnh()
        onClickBack()
        if (intent != null && intent.hasExtra("khuyenmai")) {
            listPromotion = intent.getSerializableExtra("khuyenmai") as? Promotion
        }
        if (listPromotion != null) {
            binding.txt.text = "Sửa khuyến mãi"
            binding.edtKhuyenmai.setText(listPromotion?.name)
            binding.edtThongtin.setText(listPromotion?.description)
            Glide.with(binding.root).load(listPromotion?.imageUrl).centerCrop()
                .into(binding.imvHinhanh)
        } else {
            flags = true
        }
    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun onClickAdd() {
        binding.imbAdd.setOnClickListener {
            val khuyenmai = binding.edtKhuyenmai.text?.trim().toString()
            val thongtin = binding.edtThongtin.text?.trim().toString()
            val imagePath =
                mediaPath.takeIf { it.isNotEmpty() } ?: listPromotion?.imageUrl.toString()
            if (flags) {
                val nameRequestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), khuyenmai)
                val descRequestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), thongtin)

                val imagePart: MultipartBody.Part? = if (imagePath.isNotEmpty()) {
                    val file = File(imagePath)
                    val requestFile =
                        RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    MultipartBody.Part.createFormData(
                        "fileImage",
                        file.name,
                        requestFile
                    )
                } else {
                    null
                }
                if (imagePart?.equals("") == true) {
                    val service = RetrofitClient.retrofitInstance.create(APICallBanners::class.java)
                    val call = service.themKhuyenMaiMoi(nameRequestBody, imagePart, descRequestBody)
                    call.enqueue(object : Callback<PromotionModel> {
                        override fun onResponse(
                            call: Call<PromotionModel>,
                            response: Response<PromotionModel>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@AdminAddKhuyenMaiActivity,
                                    "Thêm khuyến mãi thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@AdminAddKhuyenMaiActivity,
                                    AdminKhuyeMaiActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<PromotionModel>, t: Throwable) {
                            t.printStackTrace()
                            Log.d("loi banner", t.message.toString())
                        }
                    })
                }
                if (imagePart?.equals("") != true) {
                    val service = RetrofitClient.retrofitInstance.create(APICallBanners::class.java)
                    val call = service.themKhuyenMaiMoi(nameRequestBody, descRequestBody)
                    call.enqueue(object : Callback<PromotionModel> {
                        override fun onResponse(
                            call: Call<PromotionModel>,
                            response: Response<PromotionModel>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@AdminAddKhuyenMaiActivity,
                                    "Thêm khuyến mãi thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@AdminAddKhuyenMaiActivity,
                                    AdminKhuyeMaiActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<PromotionModel>, t: Throwable) {
                            t.printStackTrace()
                            Log.d("loi banner", t.message.toString())
                        }
                    })
                }
            } else {
                val nameRequestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), khuyenmai)
                val descRequestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), thongtin)

                val imagePart: MultipartBody.Part? = if (imagePath.isNotEmpty()) {
                    val file = File(imagePath)
                    val requestFile =
                        RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    MultipartBody.Part.createFormData(
                        "fileImage",
                        file.name,
                        requestFile
                    )
                } else {
                    null
                }
                if (imagePart!=null) {
                    val service = RetrofitClient.retrofitInstance.create(APICallBanners::class.java)
                    val call = service.suaKhuyenMai(
                        listPromotion?.id,
                        nameRequestBody,
                        imagePart,
                        descRequestBody
                    )
                    call.enqueue(object : Callback<PromotionModel> {
                        override fun onResponse(
                            call: Call<PromotionModel>,
                            response: Response<PromotionModel>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@AdminAddKhuyenMaiActivity,
                                    "Cập nhật thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@AdminAddKhuyenMaiActivity,
                                    AdminKhuyeMaiActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<PromotionModel>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                }
                if (imagePart?.equals("") != true) {
                    val service = RetrofitClient.retrofitInstance.create(APICallBanners::class.java)
                    val call = service.suaKhuyenMai(
                        listPromotion?.id,
                        nameRequestBody,
                        descRequestBody
                    )
                    call.enqueue(object : Callback<PromotionModel> {
                        override fun onResponse(
                            call: Call<PromotionModel>,
                            response: Response<PromotionModel>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@AdminAddKhuyenMaiActivity,
                                    "Cập nhật thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@AdminAddKhuyenMaiActivity,
                                    AdminKhuyeMaiActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<PromotionModel>, t: Throwable) {
                            t.printStackTrace()

                        }
                    })
                }
            }

        }
    }

    private fun onCLickThemHinhAnh() {
        binding.imbThemhinhanh.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }
    }

    private fun getPath(uri: Uri): String? {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                it.getString(index)
            } else {
                ""
            }
        } ?: uri.path
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && data.data != null) {
            val fileUri = data.data!!
            mediaPath = getPath(fileUri).toString()
            Glide.with(this).load(fileUri).into(binding.imvHinhanh)
            binding.imbThemhinhanh.visibility = View.INVISIBLE
            Log.d("Log", mediaPath ?: "No Path Found")
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }


}