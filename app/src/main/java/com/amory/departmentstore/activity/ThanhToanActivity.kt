package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.adapter.RvMuaNgay
import com.amory.departmentstore.databinding.ActivityThanhToanBinding
import com.amory.departmentstore.model.CreateOrder
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.NotificationReponse
import com.amory.departmentstore.model.SendNotification
import com.amory.departmentstore.model.User
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.retrofit.APIPushNotification
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.amory.departmentstore.retrofit.RetrofitNotification
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.NumberFormat
import java.util.Locale

class ThanhToanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThanhToanBinding
    private var tongtien: Long = 0
    var isTienMat: Boolean = true
    var isZalopay: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThanhToanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX)

        initViews()
        tinhTongThanhToan()
        onClickBack()
        onClickDatHang()
        onClickVoucher()
        showRV()
        onCLickDiaChi()
        showBottomSheet()
    }

    @SuppressLint("InflateParams", "MissingInflatedId")
    private fun showBottomSheet() {
        binding.chonphuongthuc.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_chonphuongthuc, null)
            val checkboxtienmat = view.findViewById<CheckBox>(R.id.checkboxtienmat)
            val checkboxzalo = view.findViewById<CheckBox>(R.id.checkboxzalopay)
            val btnXacNhan = view.findViewById<Button>(R.id.btnxacnhanphuongthuc)

            checkboxtienmat.isChecked = isTienMat
            checkboxzalo.isChecked = isZalopay

            checkboxtienmat.setOnClickListener {
                isTienMat = !isTienMat
                if (isTienMat) {
                    isZalopay = false
                    checkboxzalo.isChecked = false
                }
                checkboxtienmat.isChecked = isTienMat
            }

            checkboxzalo.setOnClickListener {
                isZalopay = !isZalopay
                if (isZalopay) {
                    isTienMat = false
                    checkboxtienmat.isChecked = false
                }
                checkboxzalo.isChecked = isZalopay
            }

            btnXacNhan.setOnClickListener {
                if (isZalopay) {
                    binding.imvPhuongthuc.setImageResource(R.drawable.ic_zalopay)
                    binding.txtPhuongthuc.text = "Thanh toán bằng ZaloPay"
                } else if (isTienMat) {
                    binding.imvPhuongthuc.setImageResource(R.drawable.ic_salary)
                    binding.txtPhuongthuc.text = "Thanh toán bằng khi nhận hàng"
                }
                dialog.cancel()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }
    }

    private fun initViews() {
        var fullname = ""
        var phone = ""
        var address = ""
        val intentFullname = intent.getStringExtra("fullname")
        val intentPhone = intent.getStringExtra("phone")
        val intentAddress = intent.getStringExtra("address")
        if (intentFullname != null && intentPhone != null && intentAddress != null) {
            fullname = intentFullname
            phone = intentPhone
            address = intentAddress
        } else {
            val user = Paper.book().read<User>("user")
            if (user != null) {
                fullname = user.first_name + " " + user.last_name
                phone = "0386683237"

            } else {
                fullname =
                    Utils.user_current?.first_name.toString() + " " + Utils.user_current?.last_name.toString()
                phone = "0386683237"
            }
        }
        binding.txtName.text = fullname
        binding.txtPhone.text = phone
        binding.txtAddress.text = address

        if (isZalopay) {
            binding.imvPhuongthuc.setImageResource(R.drawable.ic_zalopay)
            binding.txtPhuongthuc.text = "Thanh toán bằng ZaloPay"
        } else if (isTienMat) {
            binding.imvPhuongthuc.setImageResource(R.drawable.ic_salary)
            binding.txtPhuongthuc.text = "Thanh toán khi nhận hàng"
        }
    }

    private fun onCLickDiaChi() {
        binding.constraintLayout10.setOnClickListener {
            val intent = Intent(this, DiaChiActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun onClickVoucher() {
        binding.voucher.setOnClickListener {
            val intent = Intent(this, VoucherActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showRV() {
        val adapter = RvMuaNgay(Utils.mangmuahang)
        binding.rvSanphamTrongthanhtoan.adapter = adapter
        binding.rvSanphamTrongthanhtoan.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSanphamTrongthanhtoan.setHasFixedSize(true)
    }

    private fun tinhTongThanhToan() {
        val loaigiamgia = intent.getStringExtra("discount_type")
        val tiengiamgia = intent.getDoubleExtra("discount_value", 0.0)
        var giamgiavoucher: Long = 0
        binding.txtTamtinh.text = formatAmount(tinhTongTienHang().toString())
        binding.txtPhivanchuyen.text = formatAmount("30000")

        giamgiavoucher = if (loaigiamgia.equals("percent")) {
            (tinhTongTienHang() * (tiengiamgia / 100)).toLong()
        } else {
            tiengiamgia.toLong()
        }

        if (tinhTongTienHang() >= 300000) {
            binding.txtGiamgia.text = formatAmount("30000")
            val tonggiamgia = giamgiavoucher + 30000
            binding.txtTonggiamgia.text = formatAmount(tonggiamgia.toString())
            tongtien = tinhTongTienHang() - tonggiamgia
            binding.txtTongtien.text = formatAmount(tongtien.toString())
            binding.txtTongtienTam.text = formatAmount(tongtien.toString())
        } else {
            binding.txtGiamgia.text = formatAmount("0")
            val tonggiamgia = giamgiavoucher
            binding.txtTonggiamgia.text = formatAmount(tonggiamgia.toString())
            tongtien = tinhTongTienHang() + 30000 - giamgiavoucher
            if (tongtien > 0) {
                binding.txtTongtien.text = formatAmount(tongtien.toString())
                binding.txtTongtienTam.text = formatAmount(tongtien.toString())
            } else {
                binding.txtTongtien.text = formatAmount("0")
                binding.txtTongtienTam.text = formatAmount("0")
            }
        }

    }

    private fun tinhTongTienHang(): Long {
        var tongtienhang: Long = 0
        for (i in 0 until Utils.mangmuahang.size) {
            tongtienhang += Utils.mangmuahang[i].giasanphamgiohang.toLong()
        }
        return tongtienhang
    }

    private fun onClickDatHang() {
        val full_name = binding.txtName.text.toString()
        val user_id: Int
        val phone = binding.txtPhone.text.toString()
        val total = Utils.mangmuahang.getSoluong()
        val total_money = tongtien.toFloat()
        val detail: String
        val gson: Gson = GsonBuilder().setLenient().create()
        detail = gson.toJson(Utils.mangmuahang)
        val user = Paper.book().read<User>("user")
        user_id = user?.id ?: Utils.user_current?.id!!

        /*  Toast.makeText(this,user_id.toString(),Toast.LENGTH_LONG).show()*/
        binding.btnDathang.setOnClickListener {
            val address = binding.txtAddress.text.toString().trim()
            if (!TextUtils.isEmpty(address)) {
                val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
                val call =
                    service.taodonhang(
                        user_id,
                        full_name,
                        phone,
                        total,
                        total_money,
                        address,
                        detail
                    )
                call.enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
/*
                            pushNotification()
*/
                            val gioHangSet = Utils.manggiohang.map { it.tensanphamgiohang }.toSet()
                            val muaHangSet = Utils.mangmuahang.map { it.tensanphamgiohang }.toSet()
                            val itemsToRemove = muaHangSet.intersect(gioHangSet)
                            Utils.manggiohang.removeAll { gioHang -> itemsToRemove.contains(gioHang.tensanphamgiohang) }
                            Utils.mangmuahang.clear()
                            if (isZalopay){
                                requestZalo()
                            }else{
                                requestTienMat()
                                val intent =
                                    Intent(this@ThanhToanActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Toast.makeText(
                            this@ThanhToanActivity,
                            "Khong Thanh cong ",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("loi", t.message.toString())
                        t.printStackTrace()
                    }
                })
            } else {
                Toast.makeText(
                    this@ThanhToanActivity,
                    "Vui lòng nhập địa chỉ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestTienMat() {

    }

    private fun requestZalo() {
        val orderApi = CreateOrder()
        val txtAmount = 10000

        try {
            val data = orderApi.createOrder(txtAmount.toString())
            Log.d("Amount", txtAmount.toString())
            val code = data.getString("return_code")
            /*Toast.makeText(applicationContext, "return_code: $code", Toast.LENGTH_LONG).show()*/

            if (code == "1") {
                val token = data.getString("zp_trans_token")
                ZaloPaySDK.getInstance().payOrder(this@ThanhToanActivity, token, "demozpdk://app", object : PayOrderListener {
                    override fun onPaymentSucceeded(transactionId: String?, transToken: String?, appTransID: String?) {
                        runOnUiThread {
                            AlertDialog.Builder(this@ThanhToanActivity)
                                .setTitle("Payment Success")
                                .setMessage(String.format("TransactionId: %s - TransToken: %s", transactionId, transToken))
                                .setPositiveButton("OK") { dialog, _ -> }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }
                    }

                    override fun onPaymentCanceled(zpTransToken: String?, appTransID: String?) {
                        AlertDialog.Builder(this@ThanhToanActivity)
                            .setTitle("User Cancel Payment")
                            .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                            .setPositiveButton("OK") { dialog, _ -> }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }

                    override fun onPaymentError(zaloPayError: ZaloPayError?, zpTransToken: String?, appTransID: String?) {
                        AlertDialog.Builder(this@ThanhToanActivity)
                            .setTitle("Payment Fail")
                            .setMessage(String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken))
                            .setPositiveButton("OK") { dialog, _ -> }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }

   /* private fun pushNotification() {
        val serviceToken = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val callToken = serviceToken.getToken(1)
        callToken.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful) {
                    for (i in 0 until response.body()?.result?.size!!) {
                        val token = response.body()!!.result[i].token
                        Toast.makeText(
                            this@ThanhToanActivity,
                            token,
                            Toast.LENGTH_SHORT
                        ).show()
                        val data: MutableMap<String, String> = HashMap()
                        data["title"] = "Thông báo"
                        data["body"] = "Bạn có đơn hàng mới"
                        val sendNoti = SendNotification(token, data)
                        val service =
                            RetrofitNotification.retrofitInstance.create(APIPushNotification::class.java)
                        val call = service.sendNotification(sendNoti)
                        call.enqueue(object : Callback<NotificationReponse> {
                            override fun onResponse(
                                call: Call<NotificationReponse>,
                                response: Response<NotificationReponse>
                            ) {
                            }

                            override fun onFailure(call: Call<NotificationReponse>, t: Throwable) {
                                t.printStackTrace()
                            }
                        })
                    }
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.d("amory_notification", t.message.toString())
                t.printStackTrace()
            }
        })

    }*/

    private fun MutableList<GioHang>.getSoluong(): Int {
        var totalSoluong = 0
        for (gioHang in Utils.mangmuahang) {
            totalSoluong += gioHang.soluongsanphamgiohang
        }
        return totalSoluong
    }

    private fun onClickBack() {
        binding.imvBackThanhtoan.setOnClickListener {
            onBackPressed()
            Utils.mangmuahang.clear()
        }
    }

    private fun tienHang(): Long {
        var tienhang = intent.getLongExtra("tienhang", 0)
        tienhang += 30000
        return tienhang
    }

    private fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${format.format(number)}đ"
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

}