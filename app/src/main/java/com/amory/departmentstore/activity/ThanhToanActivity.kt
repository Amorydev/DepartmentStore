package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
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
import com.amory.departmentstore.model.SendNotification
import com.amory.departmentstore.retrofit.APINotification.APIPushNotification
import com.amory.departmentstore.retrofit.APINotification.RetrofitNotification
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import java.util.Locale

class ThanhToanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThanhToanBinding
    private var tongtien: Long = 0
    private var isTienMat: Boolean = true
    private var isZalopay: Boolean = false
    private lateinit var firestore: FirebaseFirestore
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
        var fullname = ""
        var phone = "0386683237"
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
            fullname = if (user != null) {
                user.firstName + " " + user.lastName
            } else {
                Utils.user_current?.firstName.toString() + " " + Utils.user_current?.lastName.toString()
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

        firestore = FirebaseFirestore.getInstance()
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
        val fullName = binding.txtName.text.toString()
        val userId: Int
        val email: String
        val phone = binding.txtPhone.text.toString()
        val totalMoney = tongtien.toFloat()
        val items: String
        val note = binding.noteET.text.trim().toString()
        val address = binding.txtAddress.text.toString()
        val gson: Gson = GsonBuilder().setLenient().create()
        val order: MutableList<Order> = mutableListOf()
        for (item in Utils.mangmuahang) {
            order.add(
                Order(
                    item.idsanphamgiohang,
                    item.giasanphamgiohang.toDouble(),
                    item.soluongsanphamgiohang,
                    (item.giasanphamgiohang.toDouble() * item.soluongsanphamgiohang)
                )
            )
        }
        items = gson.toJson(order)


        Log.d("items", items)
        var paymentMethod = ""
        paymentMethod = if (isTienMat) {
            "cash"
        } else {
            "online"
        }
        val user = Paper.book().read<User>("user")
        userId = user?.id ?: Utils.user_current?.id!!
        email = user?.email ?: Utils.user_current?.email!!

        /*  Toast.makeText(this,userId.toString(),Toast.LENGTH_LONG).show()*/
        binding.btnDathang.setOnClickListener {
            if (!TextUtils.isEmpty(address)) {
                val serviceOrder = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
                val call = serviceOrder.taodonhang(
                    OrderRequest(
                        userId,
                        fullName,
                        email,
                        phone,
                        address,
                        note,
                        totalMoney,
                        paymentMethod,
                        items
                    )
                )
                call.enqueue(object : Callback<UserModel> {
                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
                            pushNotification()
                            val gioHangSet = Utils.manggiohang.map { it.idsanphamgiohang }.toSet()
                            val muaHangSet = Utils.mangmuahang.map { it.idsanphamgiohang }.toSet()
                            val itemsToRemove = muaHangSet.intersect(gioHangSet)
                            Utils.manggiohang.removeAll { gioHang -> itemsToRemove.contains(gioHang.idsanphamgiohang) }
                            Utils.mangmuahang.clear()
                            if (isZalopay) {
                                requestZalo()
                            } else {
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
                                    .setPositiveButton("OK") { dialog, _ -> }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                            }
                        }

                        override fun onPaymentCanceled(zpTransToken: String?, appTransID: String?) {
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
        getToken("${getIdAdmin()}") { token ->
            val data: MutableMap<String, String> = HashMap()
            data["title"] = "Thông báo"
            data["body"] = "Bạn có đơn hàng mới"
            val sendNoti = SendNotification(token.toString(), data)
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

    private fun getIdAdmin(): Int {
        var adminId = 0
        val serviceUser = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
        val callIdAdmin = serviceUser.getUser()
        callIdAdmin.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val roleUser = response.body()?.role?.id
                    if (roleUser == 2) {
                        adminId = response.body()?.id!!
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                t.printStackTrace()
            }
        })
        return adminId
    }

    private fun getToken(userId: String, onTokenRetrieved: (String?) -> Unit) {
        val ref = firestore.collection("tokens")
        ref.document(userId).get()
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