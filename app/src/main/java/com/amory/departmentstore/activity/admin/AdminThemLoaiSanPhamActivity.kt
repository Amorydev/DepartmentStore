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
import com.amory.departmentstore.databinding.ActivityAdminThemLoaiSanPhamBinding
import com.amory.departmentstore.model.Category
import com.amory.departmentstore.model.CategoryModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallCategories
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

class AdminThemLoaiSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminThemLoaiSanPhamBinding
    private var category: Category? = null
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
            category = intent.getSerializableExtra("sua") as? Category
        }
        if (category != null) {
            flags = true
            binding.txt.text = "Sửa loại sản phẩm"
            binding.edtTensanpham.setText(category!!.name)
            Glide.with(this).load(category!!.imageUrl).into(binding.imvHinhanh)
        } else {
            flags = false
        }
    }

    private fun onClickAddLoaiSanPham() {
        binding.imbAdd.setOnClickListener {
            val name = binding.edtTensanpham.text.toString().trim()
            val imagePath = mediaPath.takeIf { !it.isNullOrEmpty() } ?: category?.imageUrl ?: ""
            val service = RetrofitClient.retrofitInstance.create(APICallCategories::class.java)

            val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val imagePart: MultipartBody.Part? = if (imagePath.isNotEmpty()) {
                val file = File(imagePath)
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                MultipartBody.Part.createFormData("fileImage", file.name, requestFile)
            } else {
                null
            }

            if (!flags) {
                if (imagePart != null) {
                    val call = service.themLoaiSanPhamMoi(nameRequestBody, imagePart)
                    call.enqueue(object : Callback<CategoryModel> {
                        override fun onResponse(
                            call: Call<CategoryModel>,
                            response: Response<CategoryModel>
                        ) {
                            Log.d("Response", response.toString())
                            if (response.isSuccessful) {
                                Toast.makeText(applicationContext, "Thành công", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(
                                    this@AdminThemLoaiSanPhamActivity,
                                    AdminQLLoaiSanPhamActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                            t.printStackTrace()
                            Log.d("loi", t.message.toString())
                            Toast.makeText(
                                applicationContext,
                                "Không thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }else {
                    if (imagePart?.equals("") != true) {
                        val call = service.themLoaiSanPhamMoi(nameRequestBody)
                        call.enqueue(object : Callback<CategoryModel> {
                            override fun onResponse(
                                call: Call<CategoryModel>,
                                response: Response<CategoryModel>
                            ) {
                                Log.d("Response", response.toString())
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Thành công",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    val intent = Intent(
                                        this@AdminThemLoaiSanPhamActivity,
                                        AdminQLLoaiSanPhamActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                }
                            }

                            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
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
                }
            } else {

                val id = category?.id ?: return@setOnClickListener
                if (imagePart != null) {
                    val call = service.suaLoaiSanPham(id, nameRequestBody, imagePart)
                    call.enqueue(object : Callback<CategoryModel> {
                        override fun onResponse(
                            call: Call<CategoryModel>,
                            response: Response<CategoryModel>
                        ) {
                            Log.d("Response", response.toString())
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
                                category = null
                            }
                        }

                        override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                            t.printStackTrace()
                            Log.d("loisua", t.message.toString())
                        }
                    })
                } else {
                    if (imagePart?.equals("") != true) {
                        val call = service.suaLoaiSanPham(id, nameRequestBody)
                        call.enqueue(object : Callback<CategoryModel> {
                            override fun onResponse(
                                call: Call<CategoryModel>,
                                response: Response<CategoryModel>
                            ) {
                                Log.d("Response", response.toString())
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
                                    category = null
                                }
                            }

                            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                                t.printStackTrace()
                                Log.d("loisua", t.message.toString())
                            }
                        })
                    }
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
