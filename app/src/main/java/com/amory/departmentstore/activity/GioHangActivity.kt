package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvSanPhamTrongGioHang
import com.amory.departmentstore.adapter.Utils
import com.amory.departmentstore.databinding.ActivityGioHangBinding
import com.amory.departmentstore.model.EventBus.TinhTongEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.text.NumberFormat
import java.util.Locale

class GioHangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGioHangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGioHangBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        OnClickBack()
        onCLickMuaHang()
        if(Utils.manggiohang.size == 0){
            Toast.makeText(this,"Chưa có sản phẩm nào được thêm vô giỏ hàng",Toast.LENGTH_SHORT).show()
        }else{

            tienHang()
           /* Toast.makeText(this,tinhTongTienHang().toString(),Toast.LENGTH_SHORT).show()*/
            val adapter = RvSanPhamTrongGioHang(Utils.manggiohang)
            binding.rvSanphamTronggiohang.adapter = adapter
            binding.rvSanphamTronggiohang.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun onCLickMuaHang() {
        binding.btnMuahang.setOnClickListener {
            val intent = Intent(this,ThanhToanActivity::class.java)
            intent.putExtra("tienhang",tinhTongTienHang())
            startActivity(intent)
        }
    }

    private fun OnClickBack() {
        binding.imvBack.setOnClickListener {
            Utils.mangmuahang.clear()
            onBackPressed()
        }
    }

    private fun tinhTongTienHang():Long{
        var tongtienhang:Long = 0
        for(i in 0 until  Utils.mangmuahang.size){
            tongtienhang +=Utils.mangmuahang[i].giasanphamgiohang.toLong()
           /* Toast.makeText(this,Utils.manggiohang[i].soluongsanphamgiohang.toString(),Toast.LENGTH_SHORT).show()
            Toast.makeText(this,Utils.manggiohang[i].giasanphamgiohang,Toast.LENGTH_SHORT).show()*/
        }
        return tongtienhang
    }
    private fun tienHang(){
        binding.tienhang.text = formatAmount(tinhTongTienHang().toString())
        binding.phigiaohang.text = formatAmount(30000.toString())

        binding.tongtien.text = formatAmount((tinhTongTienHang()+30000).toString())
    }
    private fun formatAmount(amount:String):String{
        val number = amount.toLong()
        val format = NumberFormat.getInstance(Locale("vi","VN"))
        return "${format.format(number)}đ"
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(sticky = true , threadMode = ThreadMode.MAIN)
    public fun eventTinhTien(event: TinhTongEvent){
        tienHang()
    }
}