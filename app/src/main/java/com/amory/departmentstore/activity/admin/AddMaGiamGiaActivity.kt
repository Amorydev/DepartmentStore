package com.amory.departmentstore.activity.admin

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityAddMaGiamGiaBinding
import com.amory.departmentstore.model.Voucher
import com.amory.departmentstore.model.VoucherModel
import com.amory.departmentstore.model.VoucherRequest
import com.amory.departmentstore.retrofit.APIBanHang.APICallVouchers
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddMaGiamGiaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMaGiamGiaBinding
    private var loaiGiamGia = ""
    private var isAddingNewVoucher = true
    private var voucher: Voucher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaGiamGiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if intent has extra data for updating a voucher
        if (intent != null && intent.hasExtra("updateVoucher")) {
            voucher = intent.getSerializableExtra("updateVoucher") as Voucher
            isAddingNewVoucher = false
        }

        // If we are editing an existing voucher, populate the fields
        voucher?.let {
            binding.txt.text = "Sửa mã giảm giá"
            binding.edtMagiamgia.setText(it.code)
            binding.edtGiam.setText(it.discountValue.toString())
            binding.edtDieukien.setText(it.term.toString())
            binding.HsdET.text = convertDateString(it.expirationDate)
        }

        initViews()
        setupAddOrUpdateVoucher()
    }

    private fun initViews() {
        val list = mutableListOf("Giảm theo phần trăm (%)", "Giảm theo số tiền (vnd)")
        val adapter = ArrayAdapter(
            this@AddMaGiamGiaActivity,
            android.R.layout.simple_spinner_item,
            list
        )
        binding.spnLoaimagiam.adapter = adapter
        binding.spnLoaimagiam.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    loaiGiamGia = if(list[position] == "Giảm theo phần trăm (%)") {
                        "percent"
                    }else{
                        "fixed"
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding.HsdET.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun setupAddOrUpdateVoucher() {
        binding.imbAdd.setOnClickListener {
            val service = RetrofitClient.retrofitInstance.create(APICallVouchers::class.java)
            val code = binding.edtMagiamgia.text?.trim().toString()
            val discountValue = binding.edtGiam.text?.trim().toString()
            val term = binding.edtDieukien.text?.trim().toString()
            val expirationDate = binding.HsdET.text?.trim().toString()

            val voucherRequest = VoucherRequest(code, loaiGiamGia, discountValue, term, expirationDate)

            if (isAddingNewVoucher) {
                val call = service.addVoucher(voucherRequest)
                call.enqueue(object : Callback<VoucherModel> {
                    override fun onResponse(
                        call: Call<VoucherModel>,
                        response: Response<VoucherModel>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddMaGiamGiaActivity, "Thêm voucher thành công", Toast.LENGTH_SHORT).show()
                            navigateToVoucherList()
                        }
                    }

                    override fun onFailure(call: Call<VoucherModel>, t: Throwable) {
                        Toast.makeText(this@AddMaGiamGiaActivity, "Thêm voucher thất bại", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                val call = service.updateVoucher(voucher!!.id, voucherRequest)
                call.enqueue(object : Callback<VoucherModel> {
                    override fun onResponse(
                        call: Call<VoucherModel>,
                        response: Response<VoucherModel>
                    ) {
                        Toast.makeText(this@AddMaGiamGiaActivity, "Sửa voucher thành công", Toast.LENGTH_SHORT).show()
                        navigateToVoucherList()
                    }

                    override fun onFailure(call: Call<VoucherModel>, t: Throwable) {
                        Toast.makeText(this@AddMaGiamGiaActivity, "Sửa voucher thất bại", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun navigateToVoucherList() {
        val intent = Intent(this@AddMaGiamGiaActivity, QuanLyMaGiamGiaActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun convertDateString(inputDateString: String): String {
        return try {
            val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateTime = LocalDateTime.parse(inputDateString, inputFormatter)
            dateTime.format(outputFormatter)
        } catch (e: DateTimeParseException) {
            inputDateString
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = Calendar.getInstance().time
                val selectedDateParsed = dateFormat.parse(selectedDate)

                if (selectedDateParsed!!.before(currentDate)) {
                    Toast.makeText(this, "Ngày đã chọn không được trước ngày hiện tại!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Ngày đã chọn: $selectedDate", Toast.LENGTH_LONG).show()
                    binding.HsdET.text = convertDateFormat(selectedDate)
                }
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun convertDateFormat(inputDateString: String): String {
        val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputDateFormat.parse(inputDateString)
        return outputDateFormat.format(date!!)
    }
}
