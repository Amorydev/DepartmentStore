package com.amory.departmentstore.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.Interface.OnClickRvVoucher
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvVoucherAdmin
import com.amory.departmentstore.adapter.RvVouvher
import com.amory.departmentstore.databinding.ActivityQuanLyMaGiamGiaBinding
import com.amory.departmentstore.model.EventBus.SuaXoaMaGiamGiaEvent
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.model.Voucher
import com.amory.departmentstore.model.VoucherModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.amory.departmentstore.retrofit.APIBanHang.APICallVouchers
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuanLyMaGiamGiaActivity : AppCompatActivity() {
    private lateinit var binding:ActivityQuanLyMaGiamGiaBinding
    private lateinit var voucher: Voucher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuanLyMaGiamGiaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showVoucher()
        onClickAdd()
    }

    private fun onClickAdd() {
        binding.btnThem.setOnClickListener {
            val intent = Intent(this,AddMaGiamGiaActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun showVoucher() {
        val service = RetrofitClient.retrofitInstance.create(APICallVouchers::class.java)
        val call = service.getVoucherAdmin()
        call.enqueue(object : Callback<VoucherModel> {
            override fun onResponse(call: Call<VoucherModel>, response: Response<VoucherModel>) {
                if (response.isSuccessful) {
                    val resultList = response.body()?.data
                    if (!resultList.isNullOrEmpty()) {
                        val adapter = RvVoucherAdmin(resultList)
                        binding.rvMagiamgia.adapter = adapter
                        binding.rvMagiamgia.setHasFixedSize(true)
                        binding.rvMagiamgia.layoutManager = LinearLayoutManager(
                            this@QuanLyMaGiamGiaActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }
            }

            override fun onFailure(call: Call<VoucherModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title?.equals("Sửa") == true){
            updateVoucher()
        }else{
            deleteVoucher()
        }
        return super.onContextItemSelected(item)
    }

    private fun updateVoucher() {
        val intent = Intent(this@QuanLyMaGiamGiaActivity, AddMaGiamGiaActivity::class.java)
        intent.putExtra("updateVoucher",voucher)
        startActivity(intent)
    }

    private fun deleteVoucher() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Bạn có chắc chắn muốn xóa")
        dialog.setPositiveButton("Có") { _, which ->
            val service = RetrofitClient.retrofitInstance.create(APICallVouchers::class.java)
            val call = service.deleteVoucher(voucher.id)
            call.enqueue(object : Callback<VoucherModel>{
                override fun onResponse(
                    call: Call<VoucherModel>,
                    response: Response<VoucherModel>
                ) {
                    if (response.isSuccessful){
                        showVoucher()
                    }
                }

                override fun onFailure(call: Call<VoucherModel>, t: Throwable) {
                }
            })
        }
        dialog.setNegativeButton("Không") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun suaXoaMaGiamGiaEvent(event: SuaXoaMaGiamGiaEvent){
        voucher = event.voucher
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