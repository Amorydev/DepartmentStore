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
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityAdminThemLoaiSanPhamBinding
import com.amory.departmentstore.model.LoaiSanPham
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

class AdminThemLoaiSanPhamActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminThemLoaiSanPhamBinding
    private var loaiSanPham: LoaiSanPham? = null
    private var mediaPath = ""
    private var flags = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminThemLoaiSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaPath = ""
        onClickBack()
        onCLickThemHinhAnh()
        onClickAddLoaiSanPham()
        if (intent != null && intent.hasExtra("sua")) {
             loaiSanPham= intent.getSerializableExtra("sua") as? LoaiSanPham
        }
        if (loaiSanPham != null) {
            binding.txt.text = "Sửa loại sản phẩm"
            binding.edtTensanpham.setText(loaiSanPham!!.name)
            binding.edtLoaisanpham.setText(loaiSanPham!!.category_id.toString())
            Glide.with(this).load(loaiSanPham!!.image_url).into(binding.imvHinhanh)
        } else {
            flags = true
        }
    }

    private fun onClickAddLoaiSanPham() {
        binding.imbAdd.setOnClickListener {
            val name = binding.edtTensanpham.text.toString().trim()
            val idLoaiSanPham = binding.edtLoaisanpham.text.toString().trim()
            var image_url = mediaPath
            if (mediaPath.isEmpty()){
                image_url = loaiSanPham?.image_url.toString()
            }
            if (flags == true){
                val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
                val call = service.themloaisanphammoi(name,image_url,idLoaiSanPham.toInt())
                call.enqueue(object :Callback<LoaiSanPhamModel>{
                    override fun onResponse(
                        call: Call<LoaiSanPhamModel>,
                        response: Response<LoaiSanPhamModel>
                    ) {
                        if (response.isSuccessful){
                            Toast.makeText(
                                applicationContext,
                                "thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                        t.printStackTrace()
                        Log.d("loi",t.message.toString())
                        Toast.makeText(
                            applicationContext,
                            "Không thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }else
            {
                val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
                val call = service.sualoaisanpham(name,image_url,idLoaiSanPham.toInt(),loaiSanPham!!.id)
                call.enqueue(object :Callback<LoaiSanPhamModel>{
                    override fun onResponse(
                        call: Call<LoaiSanPhamModel>,
                        response: Response<LoaiSanPhamModel>
                    ) {
                        if (response.isSuccessful){
                            Toast.makeText(
                                applicationContext,
                                "thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
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