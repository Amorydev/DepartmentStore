package com.amory.departmentstore.activity

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityAdminThemLoaiSanPhamBinding
import com.amory.departmentstore.model.LoaiSanPham
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AdminThemLoaiSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminThemLoaiSanPhamBinding
    private var loaiSanPham: LoaiSanPham? = null
    private var mediaPath: String? = null
    private var flags = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminThemLoaiSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPath = ""
        onClickBack()
        onClickThemHinhAnh()
        onClickAddLoaiSanPham()

        if (intent != null && intent.hasExtra("sua")) {
            loaiSanPham = intent.getSerializableExtra("sua") as? LoaiSanPham
        }
        if (loaiSanPham != null) {
            flags = false
            binding.txt.text = "Sửa loại sản phẩm"
            binding.edtTensanpham.setText(loaiSanPham!!.name)
            Glide.with(this).load(loaiSanPham!!.imageUrl).into(binding.imvHinhanh)
        } else {
            flags = true
        }
    }

    private fun onClickAddLoaiSanPham() {
        binding.imbAdd.setOnClickListener {
            val name = binding.edtTensanpham.text.toString().trim()
            var imagePath = mediaPath
            if (mediaPath?.isEmpty() == true){
                imagePath = loaiSanPham?.imageUrl.toString()
            }
            val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)

            if (flags) {
                val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
                val imagePart: MultipartBody.Part? = if (!imagePath.isNullOrEmpty()) {
                    val file = File(imagePath)
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    MultipartBody.Part.createFormData("fileImage", file.name, requestFile)
                } else {
                    null
                }
                if (imagePart != null) {
                    val call = service.themLoaiSanPhamMoi(nameRequestBody, imagePart)
                    call.enqueue(object : Callback<LoaiSanPhamModel> {
                        override fun onResponse(
                            call: Call<LoaiSanPhamModel>,
                            response: Response<LoaiSanPhamModel>
                        ) {
                            Log.d("Response", response.toString())
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@AdminThemLoaiSanPhamActivity,
                                    AdminQLLoaiSanPhamActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                            t.printStackTrace()
                            Log.d("loi", t.message.toString())
                            Toast.makeText(
                                applicationContext,
                                "Không thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }else{
                val id = loaiSanPham?.id
                val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
                val imagePart: MultipartBody.Part? = if (!imagePath.isNullOrEmpty()) {
                    val file = File(imagePath)
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    MultipartBody.Part.createFormData("fileImage", file.name, requestFile)
                } else {
                    null
                }
                if (imagePart != null) {
                    val call = service.suaLoaiSanPham(id,nameRequestBody,imagePart)
                    call.enqueue(object : Callback<LoaiSanPhamModel>{
                        override fun onResponse(
                            call: Call<LoaiSanPhamModel>,
                            response: Response<LoaiSanPhamModel>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@AdminThemLoaiSanPhamActivity,
                                    "Sửa thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@AdminThemLoaiSanPhamActivity,
                                    AdminQLLoaiSanPhamActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                            t.printStackTrace()
                            Log.d("loisua", t.message.toString())
                            Toast.makeText(
                                applicationContext,
                                "Không thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }
        }
    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun onClickThemHinhAnh() {
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
        return if (cursor != null) {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val result = cursor.getString(index)
            cursor.close()
            result
        } else {
            uri.path
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && data.data != null) {
            val fileUri = data.data!!
            mediaPath = getPath(fileUri)
            Glide.with(this).load(fileUri).into(binding.imvHinhanh)
            binding.imbThemhinhanh.visibility = View.INVISIBLE
            Log.d("Log", mediaPath ?: "No Path Found")
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}
