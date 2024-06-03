package com.amory.departmentstore.activity.user

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvItems
import com.amory.departmentstore.databinding.ActivityThongTinDonHangBinding
import com.amory.departmentstore.model.EventBus.ThongTinDonHangEvent
import com.amory.departmentstore.model.OrderRespone
import com.amory.departmentstore.model.UpdateOrderModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallDonHang
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ThongTinDonHangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThongTinDonHangBinding
    private var listOrder: OrderRespone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThongTinDonHangBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun ThongTinDonHang(event: ThongTinDonHangEvent) {
        listOrder = event.thongtindonhang
        initViews()
        setupRecyclerView()
        showRv()
        showBtnHuy()
        onClickBack()
        onClickBtnHuy()
        onClickTxtCapNhat()
    }

    private fun onClickTxtCapNhat() {
        binding.txtCapnhat.setOnClickListener {
            val intent = Intent(this, DiaChiActivity::class.java)
            intent.putExtra("order_info",true)
            intent.putExtra("order_id",listOrder?.id)
            intent.putExtra("order_fullname",listOrder?.fullName)
            intent.putExtra("order_phone",listOrder?.phone)
            intent.putExtra("order_address",listOrder?.address)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun onClickBtnHuy() {
        binding.btnHuy.setOnClickListener {
            val service = RetrofitClient.retrofitInstance.create(APICallDonHang::class.java)
            val call = service.huyDonHang(listOrder?.id)
            call.enqueue(object : Callback<UpdateOrderModel>{
                override fun onResponse(
                    call: Call<UpdateOrderModel>,
                    response: Response<UpdateOrderModel>
                ) {
                    if (response.isSuccessful){
                        val intent = Intent(this@ThongTinDonHangActivity, ChiTietDonHangActivity::class.java)
                        intent.putExtra("showFragment",4)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<UpdateOrderModel>, t: Throwable) {
                }
            })
        }
    }

    private fun onClickBack() {
        binding.imvBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showBtnHuy() {
        if (listOrder?.status == "pending"){
            binding.btnHuy.visibility = View.VISIBLE
            binding.txtCapnhat.visibility = View.VISIBLE
        }
    }

    private fun showRv() {
        /*Toast.makeText(this,listOrder.toString(),Toast.LENGTH_LONG).show()*/
        listOrder?.let {
            val adapter = RvItems(it.cartItems)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.btnHuy.visibility = View.INVISIBLE
        binding.txtCapnhat.visibility = View.INVISIBLE
        listOrder?.let {
            binding.txtName.text = it.fullName
            binding.txtPhone.text = it.phone
            binding.txtAddress.text = it.address
            if (it.paymentMethod == "cash"){
                binding.txtPhuongthuc.text = "Thanh toán khi nhận hàng"
            }else{
                binding.txtPhuongthuc.text = "Thanh toán bằng VNPay"
            }
            binding.txtTongtien.text = formatAmount(it.totalMoney)

            binding.txtTime.text = formatDateTime(it.updatedAt)
            Log.d("time",formatDateTime(it.updatedAt))
            binding.txtMadonhang.text = it.id.toString()
        }
    }
    private fun formatDateTime(input: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
        val dateTime = LocalDateTime.parse(input, inputFormatter)
        return dateTime.format(outputFormatter)
    }
    private fun formatAmount(amount: Float): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        showRv()
    }
}
