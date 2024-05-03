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
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType
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
        if (listSanPham != null) {
            binding.txt.text = "Sửa sản phẩm"
            binding.edtTensanpham.setText(listSanPham!!.name)
            binding.edtmotasanpham.setText(listSanPham!!.description)
            binding.edtGiasanpham.setText(listSanPham!!.price)
            binding.spinner.setSelection(listSanPham!!.category_id)
            Glide.with(this).load(listSanPham!!.image_url).into(binding.imvHinhanh)
        } else {
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
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getLoaisanPham()
        var category_id = 0
        call.enqueue(object : Callback<LoaiSanPhamModel> {
            override fun onResponse(
                call: Call<LoaiSanPhamModel>,
                response: Response<LoaiSanPhamModel>
            ) {
                if (response.isSuccessful) {
                    val list = mutableListOf<String>()
                    for (i in 0 until response.body()?.result?.size!!) {
                        list.add(response.body()!!.result[i].name)
                    }
                    val adapter = ArrayAdapter(
                        this@AdminThemSanPhamActivity,
                        android.R.layout.simple_spinner_item,
                        list as ArrayList
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

                                val selectedIndex =
                                    response.body()?.result?.indexOfFirst { it.name == selectedCategoryName }
                                if (selectedIndex != -1) {
                                    category_id =
                                        response.body()?.result?.get(selectedIndex!!)?.id ?: 0
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    binding.imbAdd.setOnClickListener {
                        val name = binding.edtTensanpham.text?.trim().toString()
                        val price = binding.edtGiasanpham.text?.trim().toString().toLong()
                        var image_url = mediaPath
                        if (mediaPath.isEmpty()){
                            image_url = listSanPham?.image_url.toString()
                        }
                        val description = binding.edtmotasanpham.text?.trim().toString()

                        if (flags == true){
                            val call2 =
                                service.themsanphammoi(name, price, image_url, category_id, description)
                            call2.enqueue(object : Callback<SanPhamModel> {
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
                                        onBackPressed()
                                    }
                                }

                                override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                                    t.printStackTrace()
                                }
                            })
                        }else{
                            val call3 =
                                service.suasanpham(name, price, image_url, category_id, description,listSanPham!!.id)
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
                                        onBackPressed()
                                    }
                                }

                                override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                                    t.printStackTrace()
                                }
                            })
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun getPath(uri: Uri): String {
        val result: String
        val cursor: Cursor? = contentResolver.query(uri, null, null, null)
        if (cursor == null) {
            result = uri.path.toString()
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(index)
            cursor.close()
        }
        return result
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mediaPath = data?.dataString.toString()
        uploadMultipleFiles()
        Glide.with(this).load(mediaPath).into(binding.imvHinhanh)
        binding.imbThemhinhanh.visibility = View.INVISIBLE
        Log.d("Log", mediaPath)
    }

    private fun uploadMultipleFiles() {
        val uri: Uri = Uri.parse(mediaPath)
        val file = File(getPath(uri))
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val fileToUpload = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.uploadFile(fileToUpload, requestBody)
        call.enqueue(object : Callback<SanPhamModel> {
            override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "thành công", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}