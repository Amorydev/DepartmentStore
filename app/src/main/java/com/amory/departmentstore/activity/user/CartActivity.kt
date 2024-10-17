package com.amory.departmentstore.activity.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.adapter.RvSanPhamTrongGioHang
import com.amory.departmentstore.databinding.ActivityGioHangBinding
import com.amory.departmentstore.model.EventBus.TinhTongEvent
import com.amory.departmentstore.viewModel.CartViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGioHangBinding
    private val viewModel : CartViewModel by viewModels()
    private var _totalMoney:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGioHangBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        setUpObserver()
        OnClickBack()
        onClickCbTatCa()
        onShowProgress()

    }

    private fun setUpObserver() {
        viewModel.totalMoney.observe(this){totalMoney ->
            _totalMoney = totalMoney
        }
    }

    private fun initViews() {
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
            renderRvCarts()
        }

    }

    private fun renderRvCarts() {
        val adapter = RvSanPhamTrongGioHang(Utils.manggiohang)
        binding.rvSanphamTronggiohang.adapter = adapter
        binding.rvSanphamTronggiohang.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
                    binding.rvSanphamTronggiohang.findViewHolderForAdapterPosition(i) as RvSanPhamTrongGioHang.ViewHolder?
                viewHolder?.binding?.checkboxSanpham?.isChecked = isChecked
                val sanPham = Utils.manggiohang[i]
                viewModel.toggleSelectAll(isChecked)
            }
            val totalAmount = _totalMoney.toFloat()
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

}