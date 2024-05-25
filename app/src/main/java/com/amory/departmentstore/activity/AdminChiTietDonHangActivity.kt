package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvCHiTietDonHangAdmin
import com.amory.departmentstore.databinding.ActivityAdminChiTietDonHangBinding
import com.amory.departmentstore.model.EventBus.DonHangEvent
import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.OrderModelAdmin
import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.OrderRespone
import com.amory.departmentstore.model.UpdateOrderModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallDonHang
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import io.paperdb.Paper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminChiTietDonHangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminChiTietDonHangBinding
    private lateinit var donhang: OrderRespone
    private var status:String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminChiTietDonHangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        layChiTietDonHang()
        onCLickDanhMuc()
        onClickNavViewAdmin()
    }
    private fun layChiTietDonHang() {
        val service = RetrofitClient.retrofitInstance.create(APICallDonHang::class.java)
        val call = service.getOrderAdmin()
        call.enqueue(object : Callback<OrderModelAdmin> {
            override fun onResponse(call: Call<OrderModelAdmin>, response: Response<OrderModelAdmin>) {
                if (response.isSuccessful) {
                    val result = response.body()?.data
                    /*Toast.makeText(this@AdminChiTietDonHangActivity,result.toString(),Toast.LENGTH_SHORT).show()*/
                    binding.rvDonhang.apply {
                        layoutManager = LinearLayoutManager(this@AdminChiTietDonHangActivity,
                            LinearLayoutManager.VERTICAL,false)
                        adapter = RvCHiTietDonHangAdmin(result)
                        setHasFixedSize(true)
                    }
                }
            }

            override fun onFailure(call: Call<OrderModelAdmin>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    private fun onClickNavViewAdmin() {
        binding.navViewAdmin.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.quanlyloaisanpham -> {
                    val intent = Intent(this, AdminQLLoaiSanPhamActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.quanlysanpham -> {
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
                        startActivity(intent)
                    }
                    true
                }
                R.id.xemdonhang ->
                {
                    val intent = Intent(this, AdminChiTietDonHangActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.khuyenmai -> {
                    val intent = Intent(this, AdminKhuyeMaiActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    true
                }
            }

        }
    }

    private fun onCLickDanhMuc() {
        binding.imbDanhmucAdmin.setOnClickListener {
            binding.layoutDrawerAdmin.openDrawer(binding.navViewAdmin)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun DonHangEvent(event: DonHangEvent){
        donhang = event.donhang
        val inflater:LayoutInflater = layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_tinhtrangdonhang,null)
        val spinner:Spinner = view.findViewById(R.id.spn_trangthai)
        val btn_xacnhan = view.findViewById<Button>(R.id.btn_xacnhan)

        val list: MutableList<String> = mutableListOf()
        list.add("Pending")
        list.add("Processing")
        list.add("Shipped")
        list.add("Delivered")
        list.add("Cancelled")

        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,list)
        spinner.adapter = adapter
        val tinhtrang = donhang.status
        spinner.setSelection(list.indexOf(tinhtrang))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                status = list[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog:AlertDialog = builder.create()
        dialog.show()

        btn_xacnhan.setOnClickListener {
            if (status == donhang.status) {
                val sameStatusAlert = AlertDialog.Builder(this)
                sameStatusAlert.setTitle("Thông báo")
                sameStatusAlert.setMessage("Không thể chọn tình trạng trùng với tình trạng hiện tại.")
                sameStatusAlert.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }
                sameStatusAlert.show()
            } else {
                val confirmAlert = AlertDialog.Builder(this)
                confirmAlert.setTitle("Cập nhật tình trạng đơn hàng")
                confirmAlert.setMessage("Bạn chắc chắn sửa tình trạng đơn hàng này sang $status?")
                confirmAlert.setPositiveButton("Có") { dialog, which ->
                    capNhatTrangThaiDonHang()
                    dialog.dismiss()
                }
                confirmAlert.setNegativeButton("Không") { dialog, which ->
                    dialog.dismiss()
                }
                confirmAlert.show()
                dialog.cancel()
            }
        }
    }

    private fun capNhatTrangThaiDonHang() {
        val statusRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), status!!)
        val service = RetrofitClient.retrofitInstance.create(APICallDonHang::class.java)
        val call = service.suaTinhTrang(donhang.id,statusRequestBody)
        call.enqueue(object : Callback<UpdateOrderModel>{
            override fun onResponse(call: Call<UpdateOrderModel>, response: Response<UpdateOrderModel>) {
                if (response.isSuccessful){
                    layChiTietDonHang()
                }
            }

            override fun onFailure(call: Call<UpdateOrderModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}