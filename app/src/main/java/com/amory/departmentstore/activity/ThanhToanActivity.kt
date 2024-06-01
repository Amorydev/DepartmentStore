package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvMuaNgay
import com.amory.departmentstore.databinding.ActivityThanhToanBinding
import com.amory.departmentstore.model.CreateOrder
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.Order
import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.User
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.model.NotificationReponse
import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.SendNotification
import com.amory.departmentstore.retrofit.APINotification.APIPushNotification
import com.amory.departmentstore.retrofit.APINotification.RetrofitNotification
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ThanhToanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThanhToanBinding
    private lateinit var customProgressDialog: Dialog
    private var tongtien: Double = 0.0
    private var isTienMat: Boolean = true
    private var isZalopay: Boolean = false
    private val REQUEST_CODE_ADDRESS = 1
    private val REQUEST_CODE_VOUCHER = 2
    private var address = ""
    private var fullName = ""
    private var phone = ""
    private var tonggiamgia = 0.0
    private var discountType = ""
    private var discountValue = 0.0
    private var discountCode = ""
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThanhToanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX)
        firestore = FirebaseFirestore.getInstance()

        initViews()
        tinhTongThanhToan()
        onClickBack()
        onClickDatHang()
        onClickVoucher()
        showRV()
        onCLickDiaChi()
        showBottomSheet()
        pushNotification()
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
                    binding.txtPhuongthuc.text = "Thanh toán khi nhận hàng"
                }
                dialog.cancel()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }
    }

    private fun initViews() {
        if (fullName.isEmpty() && address.isEmpty() && phone.isEmpty()) {
            binding.txtValid.visibility = View.VISIBLE
            binding.view2.visibility = View.INVISIBLE
            binding.txtName.visibility = View.INVISIBLE
            binding.txtPhone.visibility = View.INVISIBLE
            binding.txtAddress.visibility = View.INVISIBLE
        } else {
            binding.txtValid.visibility = View.INVISIBLE
            binding.view2.visibility = View.VISIBLE
            binding.txtName.visibility = View.VISIBLE
            binding.txtPhone.visibility = View.VISIBLE
            binding.txtAddress.visibility = View.VISIBLE
            binding.txtName.text = fullName
            binding.txtPhone.text = phone
            binding.txtAddress.text = address
        }
        /*Toast.makeText(this, fullName,Toast.LENGTH_SHORT).show()*/
        if (isZalopay) {
            binding.imvPhuongthuc.setImageResource(R.drawable.ic_zalopay)
            binding.txtPhuongthuc.text = "Thanh toán bằng ZaloPay"
        } else if (isTienMat) {
            binding.imvPhuongthuc.setImageResource(R.drawable.ic_salary)
            binding.txtPhuongthuc.text = "Thanh toán khi nhận hàng"
        }
        tinhTongThanhToan()
        Log.d("code",discountCode)
    }

    private fun onCLickDiaChi() {
        binding.constraintLayout10.setOnClickListener {
            val intent = Intent(this, DiaChiActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADDRESS)
        }
    }


    private fun onClickVoucher() {
        binding.voucher.setOnClickListener {
            val intent = Intent(this, VoucherActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_VOUCHER)
        }
    }

    private fun showRV() {
        val adapter = RvMuaNgay(Utils.mangmuahang)
        binding.rvSanphamTrongthanhtoan.adapter = adapter
        binding.rvSanphamTrongthanhtoan.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSanphamTrongthanhtoan.setHasFixedSize(true)
    }

    @SuppressLint("SetTextI18n")
    private fun tinhTongThanhToan() {
        if (discountCode.isNotEmpty()){
            binding.txtVoucherCode.text = discountCode
        }

        binding.txtTamtinh.text = formatAmount(tinhTongTienHang())
        binding.txtPhivanchuyen.text = formatAmount(30000.0)
        binding.txtVoucherCode.text

        val giamgiavoucher = if (discountType == "percent") {
            (tinhTongTienHang() * (discountValue / 100))
        } else {
            discountValue
        }

       /* Log.d("giamgiavoucher", giamgiavoucher.toString())*/
        if (tinhTongTienHang() >= 300000) {
            binding.txtGiamgia.text = "- ${formatAmount(30000.0)}"
            tonggiamgia = giamgiavoucher + 30000
            binding.txtTonggiamgia.text = "- ${formatAmount(tonggiamgia)}"
            tongtien = tinhTongTienHang() - tonggiamgia
            binding.txtTongtien.text = formatAmount(tongtien)
            binding.txtTongtienTam.text = formatAmount(tongtien)
        } else {
            binding.txtGiamgia.text = formatAmount(0.0)
            tonggiamgia = giamgiavoucher
            binding.txtTonggiamgia.text = "- ${formatAmount(tonggiamgia)}"
            tongtien = tinhTongTienHang() + 30000 - giamgiavoucher
            if (tongtien > 0) {
                binding.txtTongtien.text = formatAmount(tongtien)
                binding.txtTongtienTam.text = formatAmount(tongtien)
            } else {
                binding.txtTongtien.text = formatAmount(0.0)
                binding.txtTongtienTam.text = formatAmount(0.0)
            }
        }

    }

    private fun tinhTongTienHang(): Double {
        var tongtienhang: Double = 0.0
        for (i in 0 until Utils.mangmuahang.size) {
            tongtienhang += Utils.mangmuahang[i].giasanphamgiohang.toLong()
        }
        return tongtienhang
    }

    private fun onClickDatHang() {
        /*val fullName = binding.txtName.text.toString()
        val phone = binding.txtPhone.text.toString()*/
        val totalMoney = tongtien.toFloat()
        val status = "pending"

        val orderDetails: MutableList<Order> = mutableListOf()
        for (item in Utils.mangmuahang) {
            orderDetails.add(
                Order(
                    item.idsanphamgiohang,
                    item.giasanphamgiohang.toDouble(),
                    item.soluongsanphamgiohang,
                    item.giasanphamgiohang.toDouble() * item.soluongsanphamgiohang
                )
            )
        }
        /*Log.d("items", orderDetails.toString())*/

        val paymentMethod = if (isTienMat) {
            "cash"
        } else {
            "online"
        }

        val user = Paper.book().read<User>("user")
        val userId = user?.id ?: Utils.user_current?.id ?: -1
        val email = user?.email ?: Utils.user_current?.email.orEmpty()

        if (userId == -1 || email.isEmpty()) {
            Toast.makeText(this, "Thieeus thong tin User", Toast.LENGTH_SHORT).show()
            return
        }


        binding.btnDathang.setOnClickListener {
            /* Toast.makeText(this, fullName, Toast.LENGTH_SHORT).show()
             Toast.makeText(this, phone, Toast.LENGTH_SHORT).show()
             Toast.makeText(this, address, Toast.LENGTH_SHORT).show()*/
            val note = binding.noteET.text.trim().toString()
            val address = binding.txtAddress.text.toString()
            /*Toast.makeText(this,address,Toast.LENGTH_SHORT).show()*/

            if (address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val orderRequest = OrderRequest(
                userId,
                fullName,
                email,
                phone,
                address,
                note,
                status,
                totalMoney,
                paymentMethod,
                orderDetails
            )
            if (isZalopay) {
                requestZalo(orderRequest)
            } else {
                val serviceOrder = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
                val call = serviceOrder.taodonhang(orderRequest)
                call.enqueue(object : Callback<OrderModel> {
                    override fun onResponse(
                        call: Call<OrderModel>,
                        response: Response<OrderModel>
                    ) {
                        if (response.isSuccessful) {

                            showCustomProgressBar()
                        } else {
                            Toast.makeText(
                                this@ThanhToanActivity,
                                "Đặt hàng thất bại",
                                Toast.LENGTH_SHORT
                            ).show()
                            /*Log.d("response error", response.errorBody()?.string().orEmpty())*/
                        }
                    }

                    override fun onFailure(call: Call<OrderModel>, t: Throwable) {
                        Toast.makeText(
                            this@ThanhToanActivity,
                            "Không thành công",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        /* Log.d("error", t.message.toString())*/
                        t.printStackTrace()
                    }
                })
            }
        }
    }

    private fun showCustomProgressBar() {
        customProgressDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_progressbar, null)
        customProgressDialog.setContentView(view)
        customProgressDialog.setCancelable(true)
        customProgressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            customProgressDialog.dismiss()
            val intent = Intent(this@ThanhToanActivity, HoaDonActivity::class.java)
            intent.putExtra("hoadon_name", fullName)
            intent.putExtra("hoadon_phone", phone)
            intent.putExtra("hoadon_address", address)
            intent.putExtra("hoadon_gia", tinhTongTienHang())
            intent.putExtra("hoadon_tongtien", tongtien)
            intent.putExtra("hoadon_giamgia", tonggiamgia)
            startActivity(intent)
            finish()
            pushNotification()
        }, 2000)

    }


    private fun requestZalo(orderRequest:OrderRequest) {
        val orderApi = CreateOrder()
        val txtAmount = 10000

        try {
            val data = orderApi.createOrder(txtAmount.toString())
            /* Log.d("Amount", txtAmount.toString())*/
            val code = data.getString("return_code")
            /*Toast.makeText(applicationContext, "return_code: $code", Toast.LENGTH_LONG).show()*/

            if (code == "1") {
                val token = data.getString("zp_trans_token")
                ZaloPaySDK.getInstance().payOrder(
                    this@ThanhToanActivity,
                    token,
                    "demozpdk://app",
                    object : PayOrderListener {
                        override fun onPaymentSucceeded(
                            transactionId: String?,
                            transToken: String?,
                            appTransID: String?
                        ) {
                            runOnUiThread {
                                AlertDialog.Builder(this@ThanhToanActivity)
                                    .setTitle("Payment Success")
                                    .setMessage(
                                        String.format(
                                            "TransactionId: %s - TransToken: %s",
                                            transactionId,
                                            transToken
                                        )
                                    )
                                    .setPositiveButton("OK") { dialog, _ ->
                                        val serviceOrder = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
                                        val call = serviceOrder.taodonhang(orderRequest)
                                        call.enqueue(object : Callback<OrderModel> {
                                            override fun onResponse(
                                                call: Call<OrderModel>,
                                                response: Response<OrderModel>
                                            ) {
                                                if (response.isSuccessful) {

                                                    showCustomProgressBar()
                                                } else {
                                                    Toast.makeText(
                                                        this@ThanhToanActivity,
                                                        "Đặt hàng thất bại",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    /*Log.d("response error", response.errorBody()?.string().orEmpty())*/
                                                }
                                            }

                                            override fun onFailure(call: Call<OrderModel>, t: Throwable) {
                                                Toast.makeText(
                                                    this@ThanhToanActivity,
                                                    "Không thành công",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                                /* Log.d("error", t.message.toString())*/
                                                t.printStackTrace()
                                            }
                                        })
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            }
                        }

                        override fun onPaymentCanceled(
                            zpTransToken: String?,
                            appTransID: String?
                        ) {
                            AlertDialog.Builder(this@ThanhToanActivity)
                                .setTitle("User Cancel Payment")
                                .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                                .setPositiveButton("OK") { _, _ -> }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }

                        override fun onPaymentError(
                            zaloPayError: ZaloPayError?,
                            zpTransToken: String?,
                            appTransID: String?
                        ) {
                            AlertDialog.Builder(this@ThanhToanActivity)
                                .setTitle("Payment Fail")
                                .setMessage(
                                    String.format(
                                        "ZaloPayErrorCode: %s \nTransToken: %s",
                                        zaloPayError.toString(),
                                        zpTransToken
                                    )
                                )
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

    private fun pushNotification() {
        getToken() { token ->
            Log.d("tokenFCM",token.toString())
            val data: MutableMap<String, String> = HashMap()
            data["title"] = "Thông báo"
            data["body"] = "Bạn có đơn hàng mới"

            val sendNoti = SendNotification(token.toString(), data)
            val service = RetrofitNotification.retrofitInstance.create(APIPushNotification::class.java)
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

    private fun getToken( onTokenRetrieved: (String?) -> Unit) {
        val ref = firestore.collection("tokens")
        ref.document("tokens").get()
            .addOnSuccessListener { documentSnapshot ->
                val token = documentSnapshot.getString("token")
                onTokenRetrieved(token)
            }
            .addOnFailureListener { e ->
                onTokenRetrieved(null)
            }

    }

    private fun onClickBack() {
        binding.imvBackThanhtoan.setOnClickListener {
            onBackPressed()
            Utils.mangmuahang.clear()
        }
    }


    private fun formatAmount(amount: Double): String {
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

    override fun onResume() {
        super.onResume()
        initViews()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADDRESS && resultCode == Activity.RESULT_OK) {
            address = data?.getStringExtra("address").toString()
            fullName = data?.getStringExtra("fullname").toString()
            phone = data?.getStringExtra("phone").toString()
        }
        if (requestCode == REQUEST_CODE_VOUCHER && resultCode == Activity.RESULT_OK) {
            discountType = data?.getStringExtra("discount_type").toString()
            discountValue = data?.getDoubleExtra("discount_value", 0.0)!!
            discountCode = data.getStringExtra("discount_code").toString()
        }
    }

}