package com.amory.departmentstore.activity

import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.Interface.OnClickAllowedUser
import com.amory.departmentstore.Interface.OnClickBlockUser
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvQuanLyUser
import com.amory.departmentstore.databinding.ActivityQuanLyUserBinding
import com.amory.departmentstore.model.UpdateOrderModel
import com.amory.departmentstore.model.User
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.model.UserResponse
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuanLyUserActivity : AppCompatActivity() {
    private lateinit var binding:ActivityQuanLyUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuanLyUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showRvUser()
        onCLickDanhMuc()
        onClickNavViewAdmin()
    }

    private fun showRvUser() {
        val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
        val call = service.getAllUser()
        call.enqueue(object : Callback<UserModel>{
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful){
                    val list = response.body()?.data
                    val adapter = RvQuanLyUser(list as MutableList<UserResponse>, object : OnClickBlockUser{
                        override fun onClickBlockUser(position: Int) {
                            val id = list[position].id
                            showBlockUser(id)
                        }
                    }, object : OnClickAllowedUser{
                        override fun onClickAllowedUser(position: Int) {
                            val id = list[position].id
                            showAllowedUser(id)
                        }
                    })
                    binding.rvUser.adapter = adapter
                    binding.rvUser.layoutManager = LinearLayoutManager(this@QuanLyUserActivity,LinearLayoutManager.VERTICAL,false)
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {

            }
        })
    }

    private fun showAllowedUser(id: Int) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Khóa tài khoản")
        alertDialog.setMessage("Bạn chắc chắn muốn mở khóa tài khoản này không?")
        alertDialog.setNegativeButton("Không") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton("Có") { dialog, which ->
            allowedUser(id)
        }
        alertDialog.show()
    }

    private fun allowedUser(id: Int) {
        val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
        val call = service.allowedUser(id)
        call.enqueue(object : Callback<UpdateOrderModel>{
            override fun onResponse(
                call: Call<UpdateOrderModel>,
                response: Response<UpdateOrderModel>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(this@QuanLyUserActivity,"Mở khóa tài khoản thành công",Toast.LENGTH_SHORT).show()
                    showRvUser()
                }
            }

            override fun onFailure(call: Call<UpdateOrderModel>, t: Throwable) {

            }
        })
    }

    private fun showBlockUser(id:Int) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Khóa tài khoản")
        alertDialog.setMessage("Bạn chắc chắn muốn khóa tài khoản này không?")
        alertDialog.setNegativeButton("Không") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.setPositiveButton("Có") { dialog, which ->
            blockUser(id)
        }
        alertDialog.show()
    }

    private fun blockUser(id:Int) {
        val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
        val call = service.blockUser(id)
        call.enqueue(object : Callback<UpdateOrderModel>{
            override fun onResponse(
                call: Call<UpdateOrderModel>,
                response: Response<UpdateOrderModel>
            ) {
                if (response.isSuccessful){
                    Toast.makeText(this@QuanLyUserActivity,"Khóa tài khoản thành công",Toast.LENGTH_SHORT).show()
                    showRvUser()
                }
            }

            override fun onFailure(call: Call<UpdateOrderModel>, t: Throwable) {

            }
        })
    }
    private fun onClickNavViewAdmin() {
        binding.navViewAdmin.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.quanlyloaisanpham ->{
                    val intent = Intent(this, AdminQLLoaiSanPhamActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.quanlysanpham ->{
                    val intent = Intent(this, AdminQLSanPhamActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.dangxuat ->
                {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Đăng xuất")
                    alertDialog.setMessage("Bạn chắc chắn muốn đăng xuất")
                    alertDialog.setNegativeButton("Không"){
                            dialog, which ->
                        dialog.dismiss()
                    }
                    alertDialog.setPositiveButton("Có"){
                            dialog, which ->
                        Paper.book().delete("user")
                        val intent = Intent(this, DangNhapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                R.id.xemdonhang ->
                {
                    val intent = Intent(this, AdminChiTietDonHangActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.khuyenmai ->
                {
                    val intent = Intent(this, AdminKhuyeMaiActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.thongke ->{

                    val intent = Intent(this, DoanhSoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.quanlyuser ->{

                    val intent = Intent(this, QuanLyUserActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    true
                }
            }

        }
    }
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun onCLickDanhMuc() {
        binding.imbDanhmucAdmin.setOnClickListener {
            binding.layoutDrawerAdmin.openDrawer(binding.navViewAdmin)
        }
    }
}