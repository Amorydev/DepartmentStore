package com.amory.departmentstore.activity.user

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
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
import com.amory.departmentstore.model.Order
import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.model.NotificationReponse
import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.SendNotification
import com.amory.departmentstore.model.VnPayResponse
import com.amory.departmentstore.retrofit.APIBanHang.APICallVnPay
import com.amory.departmentstore.retrofit.APINotification.APIPushNotification
import com.amory.departmentstore.retrofit.APINotification.RetrofitNotification
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class ThanhToanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThanhToanBinding
    private lateinit var customProgressDialog: Dialog
    private var tongtien: Double = 0.0
    private var isTienMat: Boolean = true
    private var isVNPay: Boolean = false
    private val REQUEST_CODE_ADDRESS = 1
    private val REQUEST_CODE_VOUCHER = 2
    private var address = ""
    private var fullName = ""
    private var phone = ""
    private var tonggiamgia = 0.0
    private var discountType = ""
    private var discountValue = 0.0
    private var discountCode = ""
    private var discountCondition = 0.0
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThanhToanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        firestore = FirebaseFirestore.getInstance()

        initViews()
        tinhTongThanhToan()
        onClickBack()
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
            checkboxzalo.isChecked = isVNPay

            checkboxtienmat.setOnClickListener {
                isTienMat = !isTienMat
                if (isTienMat) {
                    isVNPay = false
                    checkboxzalo.isChecked = false
                }
                checkboxtienmat.isChecked = isTienMat
            }

            checkboxzalo.setOnClickListener {
                isVNPay = !isVNPay
                if (isVNPay) {
                    isTienMat = false
                    checkboxtienmat.isChecked = false
                }
                checkboxzalo.isChecked = isVNPay
            }

            btnXacNhan.setOnClickListener {
                if (isVNPay) {
                    binding.imvPhuongthuc.setImageResource(R.drawable.logo_vnpay)
                    binding.txtPhuongthuc.text = "Thanh toán bằng VNPay"
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
        if (isVNPay) {
            binding.imvPhuongthuc.setImageResource(R.drawable.logo_vnpay)
            binding.txtPhuongthuc.text = "Thanh toán bằng VNPay"
        } else if (isTienMat) {
            binding.imvPhuongthuc.setImageResource(R.drawable.ic_salary)
            binding.txtPhuongthuc.text = "Thanh toán khi nhận hàng"
        }
        tinhTongThanhToan()
        Log.d("code", discountCode)
        Log.d("condition", discountCondition.toString())
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
        binding.rvSanphamTrongthanhtoan.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSanphamTrongthanhtoan.setHasFixedSize(true)
    }

    @SuppressLint("SetTextI18n")
    private fun tinhTongThanhToan() {
        var giamGiaVoucher = 0.0
        val tongTienHang = tinhTongTienHang()
        val phiVanChuyen = 30000.0

        /*Toast.makeText(this, discountCondition.toString(), Toast.LENGTH_SHORT).show()*/

        if (discountCondition <= tongTienHang) {
            if (discountCode.isNotEmpty()) {
                binding.txtVoucherCode.text = discountCode
            }

            giamGiaVoucher = if (discountType == "percent") {
                tongTienHang * (discountValue / 100)
            } else {
                discountValue
            }
        } else {
            binding.txtVoucherCode.text = "Chọn hoặc nhập mã"
            Toast.makeText(this, "Không thể sử dụng voucher", Toast.LENGTH_SHORT).show()
        }

        binding.txtTamtinh.text = formatAmount(tongTienHang)

        binding.txtPhivanchuyen.text = formatAmount(phiVanChuyen)

        var tongGiamGia = 0.0
        if (tongTienHang >= 300000) {
            binding.txtGiamgia.text = "- ${formatAmount(phiVanChuyen)}"
            tongGiamGia += (phiVanChuyen + giamGiaVoucher)
        } else {
            tongGiamGia += giamGiaVoucher
            binding.txtGiamgia.text = formatAmount(0.0)
        }
        tonggiamgia = tongGiamGia
        binding.txtTonggiamgia.text = "- ${formatAmount(tongGiamGia)}"

        tongtien = tongTienHang - tongGiamGia
        Log.d("TONGTIEN",tongtien.toString())
        if (tongtien > 0) {
            binding.txtTongtien.text = formatAmount(tongtien)
            binding.txtTongtienTam.text = formatAmount(tongtien)
        } else {
            binding.txtTongtien.text = formatAmount(0.0)
            binding.txtTongtienTam.text = formatAmount(0.0)
        }
        onClickDatHang()
    }

    private fun tinhTongTienHang(): Double {
        var tongTienHang: Double = 0.0
        for (sanPham in Utils.mangmuahang) {
            tongTienHang += sanPham.giasanphamgiohang.toLong()
        }
        return tongTienHang
    }


    private fun onClickDatHang() {
        /*val fullName = binding.txtName.text.toString()
        val phone = binding.txtPhone.text.toString()*/
        val totalMoney = tongtien.toFloat()
        Log.d("ABC", tongtien.toString())
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
        var paymentMethod = "cash"
        if (isTienMat) {
            paymentMethod = "cash"
        } else if (isVNPay){
            paymentMethod = "online"
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

            var orderRequest = OrderRequest(
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
            if (isVNPay) {
                paymentMethod = "online"
                orderRequest = OrderRequest(
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
                /*requestZalo(orderRequest)*/
                val serviceOrder = RetrofitClient.retrofitInstance.create(APICallVnPay::class.java)
                val call = serviceOrder.getUrlVNPay(orderRequest)
                Log.d("orderRequest",orderRequest.toString())
                call.enqueue(object : Callback<VnPayResponse>{
                    override fun onResponse(
                        call: Call<VnPayResponse>,
                        response: Response<VnPayResponse>
                    ) {
                        if (response.isSuccessful){
                            val url = response.body()?.data?.payment_url
                            if (!url.isNullOrEmpty()) {
                                val urlIntent = Intent(this@ThanhToanActivity,WebViewActivity::class.java)
                                urlIntent.putExtra("URL",url)
                                urlIntent.putExtra("order_request",orderRequest)
                                urlIntent.putExtra("hoadon_name", fullName)
                                urlIntent.putExtra("hoadon_phone", phone)
                                urlIntent.putExtra("hoadon_address", address)
                                urlIntent.putExtra("hoadon_gia", tinhTongTienHang())
                                urlIntent.putExtra("hoadon_tongtien", tongtien)
                                urlIntent.putExtra("hoadon_giamgia", tonggiamgia)
                                startActivity(urlIntent)
                            }
                        }
                    }

                    override fun onFailure(call: Call<VnPayResponse>, t: Throwable) {
                    }
                })
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
            intent.putExtra("hoadon_phuongthuc", isTienMat)
            startActivity(intent)
            finish()
            pushNotification()
        }, 2000)

    }

    private fun pushNotification() {
        getTokenFCM(5) { token ->
            if (token == null) {
                Log.e("pushNotification", "Failed to retrieve FCM token")
                return@getTokenFCM
            }
            Log.d("tokenFCM", token)

            val data: MutableMap<String, String> = HashMap()
            data["body"] = "Bạn có đơn hàng mới"
            data["title"] = "Thông báo"

            val sendNoti = SendNotification(token,data)
            Log.d("FCM", sendNoti.toString())

            val service =
                RetrofitNotification.retrofitInstance.create(APIPushNotification::class.java)
            val call = service.sendNotification(sendNoti)
            call.enqueue(object : Callback<NotificationReponse> {
                override fun onResponse(
                    call: Call<NotificationReponse>,
                    response: Response<NotificationReponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ThanhToanActivity, "Thành công nhé", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("FCM Response", "Notification sent successfully")
                    } else {
                        Log.e(
                            "FCM Response",
                            "Failed to send notification. Response code: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<NotificationReponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("FCM Failure", "Error sending notification", t)
                }
            })
        }
    }

    private fun getTokenFCM(userId: Int, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val tokensCollection = db.collection("tokens")

        tokensCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    callback(null)
                } else {
                    val document = querySnapshot.documents[0]
                    val token = document.getString("token")
                    callback(token)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                callback(null)
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
            discountCondition = data?.getDoubleExtra("discount_condition", 0.0)!!
            discountType = data.getStringExtra("discount_type").toString()
            discountValue = data.getDoubleExtra("discount_value", 0.0)
            discountCode = data.getStringExtra("discount_code").toString()
        }
    }

}