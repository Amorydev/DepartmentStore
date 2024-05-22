package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityAdminThemSanPhamBinding
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallCategories
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
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

class AdminThemSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminThemSanPhamBinding
    private var mediaPath: String = ""
    var listSanPham: SanPham? = null
    var flags: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminThemSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onclickSpiner()
        mediaPath = ""
        onCLickThemHinhAnh()
        onClickBack()
        if (intent != null && intent.hasExtra("sua")) {
            listSanPham = intent.getSerializableExtra("sua") as? SanPham
        }

        listSanPham?.let { sanPham ->
            binding.txt.text = "Sửa sản phẩm"
            binding.edtTensanpham.setText(sanPham.name)
            binding.edtmotasanpham.setText(sanPham.description)
            binding.edtGiasanpham.setText(sanPham.price.toString())
            sanPham.categoryId?.let { category ->
                binding.spinner.setSelection(category.id)
            }
            Glide.with(this).load(sanPham.imageUrl).into(binding.imvHinhanh)
        } ?: run {
            flags = true
        }
    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
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

    private fun onclickSpiner() {
        var categoryId = 0
        val service = RetrofitClient.retrofitInstance.create(APICallCategories::class.java)
        val call = service.getLoaisanPham()
        call.enqueue(object : Callback<LoaiSanPhamModel> {
            override fun onResponse(
                call: Call<LoaiSanPhamModel>,
                response: Response<LoaiSanPhamModel>
            ) {
                if (response.isSuccessful) {
                    val list =
                        response.body()?.data?.map { it.name }?.toMutableList() ?: mutableListOf()
                    val adapter = ArrayAdapter(
                        this@AdminThemSanPhamActivity,
                        android.R.layout.simple_spinner_item,
                        list
                    )
                    binding.spinner.adapter = adapter
                    binding.spinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedCategoryName = list[position]
                                val selectedIndex = response.body()?.data?.indexOfFirst { it.name == selectedCategoryName }
                                if (selectedIndex != null && selectedIndex != -1) {
                                    categoryId = response.body()?.data?.get(selectedIndex)?.id ?: 0
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }

                    binding.imbAdd.setOnClickListener {
                        val name = binding.edtTensanpham.text?.trim().toString()
                        val price = binding.edtGiasanpham.text?.trim().toString()
                        val imagePath =
                            mediaPath.takeIf { it.isNotEmpty() } ?: listSanPham?.imageUrl.toString()
                        val description = binding.edtmotasanpham.text?.trim().toString()

                        if (flags) {
                            val nameRequestBody =
                                RequestBody.create("text/plain".toMediaTypeOrNull(), name)
                            val priceRequestBody =
                                RequestBody.create("text/plain".toMediaTypeOrNull(), price)
                            val descRequestBody =
                                RequestBody.create("text/plain".toMediaTypeOrNull(), description)
                            val categoryIdRequestBody = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                categoryId.toString()
                            )

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
                                val serviceProducts = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
                                val call2 = serviceProducts.themsanphammoi(
                                    nameRequestBody,
                                    priceRequestBody,
                                    imagePart,
                                    descRequestBody,
                                    categoryIdRequestBody
                                )
                                call2.enqueue(object : Callback<SanPhamModel> {
                                    override fun onResponse(
                                        call: Call<SanPhamModel>,
                                        response: Response<SanPhamModel>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Thành công",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(
                                                this@AdminThemSanPhamActivity,
                                                AdminQLSanPhamActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }
                                    }

                                    override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                                        t.printStackTrace()
                                        Log.d("add loi", t.message.toString())
                                    }
                                })
                            }
                            if (imagePart?.equals("") != true) {
                                val serviceProducts = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
                                val call2 = serviceProducts.themsanphammoi(
                                    nameRequestBody,
                                    priceRequestBody,
                                    descRequestBody,
                                    categoryIdRequestBody
                                )
                                call2.enqueue(object : Callback<SanPhamModel> {
                                    override fun onResponse(
                                        call: Call<SanPhamModel>,
                                        response: Response<SanPhamModel>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Thành công",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(
                                                this@AdminThemSanPhamActivity,
                                                AdminQLSanPhamActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }
                                    }

                                    override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                                        t.printStackTrace()
                                        Log.d("add loi", t.message.toString())
                                    }
                                })
                            }
                        }else{
                            val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
                            val priceRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), price)
                            val descRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
                            val categoryIdRequestBody = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                categoryId.toString()
                            )

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
                                val serviceProducts = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
                                val call3 = serviceProducts.suasanpham(listSanPham?.id,nameRequestBody,priceRequestBody,imagePart,descRequestBody,categoryIdRequestBody)
                                call3.enqueue(object : Callback<SanPhamModel> {
                                    override fun onResponse(
                                        call: Call<SanPhamModel>,
                                        response: Response<SanPhamModel>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "thành công",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(this@AdminThemSanPhamActivity,AdminQLSanPhamActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }

                                    override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                                        t.printStackTrace()
                                    }
                                })
                            }
                            if (imagePart?.equals("") != true) {
                                val serviceProducts = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
                                val call3 = serviceProducts.suasanpham(listSanPham?.id,nameRequestBody,priceRequestBody,descRequestBody,categoryIdRequestBody)
                                call3.enqueue(object : Callback<SanPhamModel> {
                                    override fun onResponse(
                                        call: Call<SanPhamModel>,
                                        response: Response<SanPhamModel>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "thành công",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(this@AdminThemSanPhamActivity,AdminQLSanPhamActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }

                                    override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                                        t.printStackTrace()
                                        Log.d("Error Products",t.message.toString())
                                    }
                                })
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
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
    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

}