package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityDiaChiBinding
import com.amory.departmentstore.model.*
import com.amory.departmentstore.retrofit.APIBanHang.APICallDonHang
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.retrofit.APIDiaChi.APIDiaChi
import com.amory.departmentstore.retrofit.APIDiaChi.RetrofitDiaChi
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaChiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaChiBinding
    private lateinit var customProgressDialog: Dialog
    private var nameProvince = ""
    private var nameDistrict = ""
    private var nameCommune = ""
    private var fullName = ""
    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaChiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isUpdate = intent.getBooleanExtra("order_info", false)
        if (isUpdate) {
            handleUpdateOrder()
        } else {
            showCustomProgressBar()
            initViews()
        }

        setupSpinnerListeners()
        setupButtonListeners()
    }

    private fun handleUpdateOrder() {
        showCustomProgressBar()
        binding.apply {
            nameET.setText(intent.getStringExtra("order_fullname"))
            mobileET.setText(intent.getStringExtra("order_phone"))
            diachiET.setText(intent.getStringExtra("order_address")?.split(",")?.get(0))
        }
    }

    private fun updateOrderAddress(orderId: Int) {
        val address = getAddressString()
        val phone = binding.mobileET.text?.trim().toString()
        val fullName = binding.nameET.text?.trim().toString()

        val service = RetrofitClient.retrofitInstance.create(APICallDonHang::class.java)
        val call = service.suaDiaChi(orderId, address, fullName, phone)
        call.enqueue(object : Callback<UpdateOrderModel> {
            override fun onResponse(call: Call<UpdateOrderModel>, response: Response<UpdateOrderModel>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@DiaChiActivity, ThongTinDonHangActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@DiaChiActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DiaChiActivity, "Cập nhật không thành công", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateOrderModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun initViews() {
        binding.constraintLayoutDiaChi.visibility = View.INVISIBLE
        val user = Paper.book().read<User>("user") ?: Utils.user_current
        user?.let {
            fullName = "${it.firstName} ${it.lastName}"
            binding.nameET.setText(fullName)
        }
    }

    private fun getAddressString(): String {
        val houseNumber = binding.diachiET.text?.trim().toString()
        return "$houseNumber, $nameCommune, $nameDistrict, $nameProvince"
    }

    private fun setupButtonListeners() {
        binding.btnXacnhan.setOnClickListener {
            if (isUpdate) {
                val orderId = intent.getIntExtra("order_id", 0)
                updateOrderAddress(orderId)
            } else {
                val address = getAddressString()
                val phone = binding.mobileET.text?.trim().toString()
                val fullName = binding.nameET.text?.trim().toString()

                val intent = Intent().apply {
                    putExtra("address", address)
                    putExtra("fullname", fullName)
                    putExtra("phone", phone)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        binding.imvBack.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("InflateParams")
    private fun showCustomProgressBar() {
        customProgressDialog = Dialog(this).apply {
            setContentView(LayoutInflater.from(this@DiaChiActivity).inflate(R.layout.layout_progressbar, null))
            setCancelable(false)
            show()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            customProgressDialog.dismiss()
            binding.constraintLayoutDiaChi.visibility = View.VISIBLE
        }, 3000)
    }

    private fun setupSpinnerListeners() {
        loadProvinces()
    }

    private fun loadProvinces() {
        val service = RetrofitDiaChi.retrofitInstance.create(APIDiaChi::class.java)
        val call = service.getTinh()
        call.enqueue(object : Callback<ProvinceModel> {
            override fun onResponse(call: Call<ProvinceModel>, response: Response<ProvinceModel>) {
                if (response.isSuccessful) {
                    val provinces = response.body()?.results ?: return
                    setupProvinceSpinner(provinces)
                }
            }

            override fun onFailure(call: Call<ProvinceModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("DiaChiActivity", t.message.toString())
            }
        })
    }

    private fun setupProvinceSpinner(provinces: List<Province>) {
        val provinceNames = provinces.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinceNames)
        binding.spinerTinh.adapter = adapter
        binding.spinerTinh.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                nameProvince = provinces[position].name
                loadDistricts(provinces[position].code)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadDistricts(provinceCode: String) {
        val service = RetrofitDiaChi.retrofitInstance.create(APIDiaChi::class.java)
        val call = service.getHuyen(provinceCode)
        call.enqueue(object : Callback<DistrictModel> {
            override fun onResponse(call: Call<DistrictModel>, response: Response<DistrictModel>) {
                if (response.isSuccessful) {
                    val districts = response.body()?.results ?: return
                    setupDistrictSpinner(districts)
                }
            }

            override fun onFailure(call: Call<DistrictModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setupDistrictSpinner(districts: List<District>) {
        val districtNames = districts.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districtNames)
        binding.spinerHuyen.adapter = adapter
        binding.spinerHuyen.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                nameDistrict = districts[position].name
                loadCommunes(districts[position].code)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadCommunes(districtCode: String) {
        val service = RetrofitDiaChi.retrofitInstance.create(APIDiaChi::class.java)
        val call = service.getXa(districtCode)
        call.enqueue(object : Callback<CommuneModel> {
            override fun onResponse(call: Call<CommuneModel>, response: Response<CommuneModel>) {
                if (response.isSuccessful) {
                    val communes = response.body()?.results ?: return
                    setupCommuneSpinner(communes)
                }
            }

            override fun onFailure(call: Call<CommuneModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setupCommuneSpinner(communes: List<Commune>) {
        val communeNames = communes.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, communeNames)
        binding.spinerXa.adapter = adapter
        binding.spinerXa.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                nameCommune = communes[position].name
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
