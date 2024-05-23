package com.amory.departmentstore.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.adapter.RvSanPhamTrongGioHang
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

        if (Utils.manggiohang.size == 0) {
            binding.imvNoProducts.visibility = View.VISIBLE
            binding.txtNoProducts.visibility = View.VISIBLE
            binding.rvSanphamTronggiohang.visibility = View.INVISIBLE
        } else {
            binding.imvNoProducts.visibility = View.INVISIBLE
            binding.txtNoProducts.visibility = View.INVISIBLE
            binding.rvSanphamTronggiohang.visibility = View.VISIBLE
            onCLickMuaHang()
            tienHang()
            /* Toast.makeText(this,tinhTongTienHang().toString(),Toast.LENGTH_SHORT).show()*/
            val adapter = RvSanPhamTrongGioHang(Utils.manggiohang)
            binding.rvSanphamTronggiohang.adapter = adapter
            binding.rvSanphamTronggiohang.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

        OnClickBack()
        onClickCbTatCa()
        onShowProgress()

    }

    private fun onShowProgress() {
        binding.niftySlider.apply {
            setThumbCustomDrawable(R.drawable.ic_fast_delivery)
            visibility = View.VISIBLE
        }
    }

    private fun onClickCbTatCa() {
        binding.cbTatca.setOnCheckedChangeListener { _, isChecked ->
            for (i in 0 until binding.rvSanphamTronggiohang.childCount) {
                val viewHolder =
                    binding.rvSanphamTronggiohang.findViewHolderForAdapterPosition(i) as RvSanPhamTrongGioHang.viewHolder?
                viewHolder?.binding?.checkboxSanpham?.isChecked = isChecked
                val sanPham = Utils.manggiohang[i]
                if (isChecked) {
                    if (!Utils.mangmuahang.contains(sanPham)) {
                        Utils.mangmuahang.add(sanPham)
                    }
                } else {
                    Utils.mangmuahang.remove(sanPham)
                }
            }
            val totalAmount = tinhTongTienHang().toFloat()
            binding.niftySlider.valueTo = 300000F
            binding.niftySlider.setValue(totalAmount)
            onShowProgress()
            EventBus.getDefault().postSticky(TinhTongEvent())
        }
    }

    private fun onCLickMuaHang() {
        binding.btnMuahang.setOnClickListener {
            if (Utils.mangmuahang.isEmpty()) {
                Toast.makeText(this, "Bạn chưa chọn sản phẩm để mua", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ThanhToanActivity::class.java)
                intent.putExtra("tienhang", tinhTongTienHang())
                startActivity(intent)
            }
        }
    }

    private fun OnClickBack() {
        binding.imvBack.setOnClickListener {
            Utils.mangmuahang.clear()
            onBackPressed()
        }
    }

    private fun tinhTongTienHang(): Long {
        var tongtienhang: Long = 0
        for (i in 0 until Utils.mangmuahang.size) {
            tongtienhang += Utils.mangmuahang[i].giasanphamgiohang.toLong()
        }
        return tongtienhang
    }

    private fun tienHang() {
        binding.txtTongtien.text = formatAmount(tinhTongTienHang().toString())
        binding.niftySlider.apply {
            setThumbCustomDrawable(R.drawable.ic_fast_delivery)
            visibility = View.VISIBLE
            setValue(tinhTongTienHang().toFloat())
        }
    }

    private fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public fun eventTinhTien(event: TinhTongEvent) {
        tienHang()
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}