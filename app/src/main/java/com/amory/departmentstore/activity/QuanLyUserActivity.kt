package com.amory.departmentstore.activity

import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
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
                    })
                    binding.rvUser.adapter = adapter
                    binding.rvUser.layoutManager = LinearLayoutManager(this@QuanLyUserActivity,LinearLayoutManager.VERTICAL,false)
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {

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
}