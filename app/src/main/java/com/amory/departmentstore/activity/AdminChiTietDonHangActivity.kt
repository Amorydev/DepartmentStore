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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvCHiTietDonHangAdmin
import com.amory.departmentstore.adapter.RvChiTietDatHang
import com.amory.departmentstore.adapter.Utils
import com.amory.departmentstore.databinding.ActivityAdminChiTietDonHangBinding
import com.amory.departmentstore.model.DonHangModel
import com.amory.departmentstore.model.Donhang
import com.amory.departmentstore.model.EventBus.DonHangEvent
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import io.paperdb.Paper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminChiTietDonHangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminChiTietDonHangBinding
    private lateinit var donhang: Donhang
    private var status:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminChiTietDonHangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        layChiTietDonHang()
        onCLickDanhMuc()
        onClickNavViewAdmin()
    }
    private fun layChiTietDonHang() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.xemdonhang(0)
        call.enqueue(object : Callback<DonHangModel> {
            override fun onResponse(call: Call<DonHangModel>, response: Response<DonHangModel>) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    binding.rvDonhang.apply {
                        layoutManager = LinearLayoutManager(this@AdminChiTietDonHangActivity,
                            LinearLayoutManager.VERTICAL,false)
                        adapter = RvCHiTietDonHangAdmin(result)
                        setHasFixedSize(true)
                    }
                }
            }

            override fun onFailure(call: Call<DonHangModel>, t: Throwable) {
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
                    Paper.book().delete("user")
                    val intent = Intent(this, DangNhapActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.xemdonhang ->
                {
                    val intent = Intent(this, AdminChiTietDonHangActivity::class.java)
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
        list.add("Đơn hàng đang được xử lí")
        list.add("Đơn hàng đã bàn giao cho đơn vị vận chuyển")
        list.add("Đơn hàng đang được giao")
        list.add("Đơn hàng giao thành công")
        list.add("Đơn hàng đã hủy")

        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,list)
        spinner.adapter = adapter
        spinner.setSelection(donhang.status)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                status = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog:AlertDialog = builder.create()
        dialog.show()

        btn_xacnhan.setOnClickListener {
            capNhatTrangThaiDonHang()
        }
    }

    private fun capNhatTrangThaiDonHang() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.updatetinhtrang(donhang.id,status)
        call.enqueue(object : Callback<DonHangModel>{
            override fun onResponse(call: Call<DonHangModel>, response: Response<DonHangModel>) {
                if (response.isSuccessful){
                    layChiTietDonHang()
                }
            }

            override fun onFailure(call: Call<DonHangModel>, t: Throwable) {
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
}