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
import com.amory.departmentstore.databinding.ActivityAdminAddKhuyenMaiBinding
import com.amory.departmentstore.model.KhuyenMai
import com.amory.departmentstore.model.KhuyenMaiModel
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

class AdminAddKhuyenMaiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminAddKhuyenMaiBinding
    private var mediaPath = ""
    var flags = false
    var listKhuyenMai:KhuyenMai ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddKhuyenMaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickAdd()
        onCLickThemHinhAnh()
        onClickBack()
        if (intent != null && intent.hasExtra("khuyenmai")) {
            listKhuyenMai = intent.getSerializableExtra("khuyenmai") as? KhuyenMai
        }
        if (listKhuyenMai != null) {
            binding.txt.text = "Sửa khuyến mãi"
            binding.edtKhuyenmai.setText(listKhuyenMai?.khuyenmai)
            binding.edtThongtin.setText(listKhuyenMai?.thongtin)
            Glide.with(binding.root).load(listKhuyenMai?.image_url).centerCrop().into(binding.imvHinhanh)
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
            var image_url = ""
            image_url = mediaPath.ifEmpty {
                "https://cdn.tgdd.vn/bachhoaxanh/banners/5599/san-sale-gia-soc-cung-bhx-12032024133716.jpg"
            }
            if (flags) {
                val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
                val call = service.themkhuyenmai(khuyenmai, thongtin, image_url)
                call.enqueue(object : Callback<KhuyenMaiModel> {
                    override fun onResponse(
                        call: Call<KhuyenMaiModel>,
                        response: Response<KhuyenMaiModel>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@AdminAddKhuyenMaiActivity,
                                "Thêm khuyến mãi thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@AdminAddKhuyenMaiActivity,AdminKhuyeMaiActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<KhuyenMaiModel>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }else{

                val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
                val call = service.suakhuyenmai(khuyenmai,thongtin,
                    listKhuyenMai?.image_url.toString(), listKhuyenMai?.id!!)
                call.enqueue(object : Callback<KhuyenMaiModel>{
                    override fun onResponse(
                        call: Call<KhuyenMaiModel>,
                        response: Response<KhuyenMaiModel>
                    ) {
                        if (response.isSuccessful){
                            Toast.makeText(this@AdminAddKhuyenMaiActivity,"Cập nhật thành công",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AdminAddKhuyenMaiActivity,AdminKhuyeMaiActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<KhuyenMaiModel>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
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