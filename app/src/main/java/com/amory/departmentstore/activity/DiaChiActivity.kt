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
import com.amory.departmentstore.model.Commune
import com.amory.departmentstore.model.District
import com.amory.departmentstore.model.OrderRespone
import com.amory.departmentstore.model.Province
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.UpdateDiaChiOrder
import com.amory.departmentstore.model.UpdateOrderModel
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APIBanHang.APICallDonHang
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.retrofit.APIDiaChi.APIDiaChi
import com.amory.departmentstore.retrofit.APIDiaChi.RetrofitDiaChi
import com.google.android.play.integrity.internal.b
import com.google.android.play.integrity.internal.o
import io.paperdb.Paper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class DiaChiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaChiBinding
    private lateinit var customProgressDialog: Dialog
    private var name_province = ""
    private var name_district = ""
    private var name_communce = ""
    private var full_name = ""
    var capnhat = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaChiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        capnhat = intent.getBooleanExtra("order_info", false)
        if (capnhat){
            showCustomProgressBar()
            val order_fullName = intent.getStringExtra("order_fullname")
            val order_phone = intent.getStringExtra("order_phone")
            val order_address = intent.getStringExtra("order_address")
            binding.nameET.setText(order_fullName)
            binding.mobileET.setText(order_phone)
            val part = order_address?.split(",")
            binding.diachiET.setText(part?.get(0))
        }else{
            showCustomProgressBar()
            initViews()
        }
        ShowSpinerTinh()
        onClickXacNhan()
        onclickBack()
    }
    private fun SuaDiaChi(order_id: Int) {
        val sonha = binding.diachiET.text?.trim().toString()
        val diaChi = "$sonha, $name_communce, $name_district, $name_province"
        val phone = binding.mobileET.text?.trim().toString()
        val fullName = binding.nameET.text?.trim().toString()

        val service = RetrofitClient.retrofitInstance.create(APICallDonHang::class.java)
        val call = service.suaDiaChi(order_id, diaChi, fullName, phone)
        call.enqueue(object : Callback<UpdateOrderModel> {
            override fun onResponse(call: Call<UpdateOrderModel>, response: Response<UpdateOrderModel>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DiaChiActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    Log.d("SuaDiaChi", "Response: ${response.body()}")
                } else {
                    Toast.makeText(this@DiaChiActivity, "Cập nhật không thành công", Toast.LENGTH_SHORT).show()
                    Log.e("SuaDiaChi", "Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpdateOrderModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("SuaDiaChi", "Error: ${t.message}")
            }
        })
    }


    private fun onclickBack() {
        binding.imvBack.setOnClickListener {
            onBackPressed()
        }
    }


    private fun initViews() {
        binding.constraintLayoutDiaChi.visibility = View.INVISIBLE
        val user = Paper.book().read<User>("user")
        if (user == null) {
            full_name = Utils.user_current?.firstName + " " + Utils.user_current?.lastName
            binding.nameET.setText(full_name)
        } else {
            full_name = user.firstName + " " + user.lastName
            binding.nameET.setText(full_name)
        }

    }
    private fun onClickXacNhan() {
        binding.btnXacnhan.setOnClickListener {
            if (capnhat){
                val order_id = intent.getIntExtra("order_id",0)
                SuaDiaChi(order_id)
            }else{
                val sonha = binding.diachiET.text?.trim().toString()
                val address = "$sonha, $name_communce, $name_district, $name_province"
                val phone = binding.mobileET.text?.trim().toString()
                val fullName = binding.nameET.text?.trim().toString()
                val intent = Intent(applicationContext, ThanhToanActivity::class.java)
                intent.putExtra("address", address)
                intent.putExtra("fullname", fullName)
                intent.putExtra("phone", phone)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }


    @SuppressLint("InflateParams")
    private fun showCustomProgressBar() {
        customProgressDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_progressbar, null)
        customProgressDialog.setContentView(view)
        customProgressDialog.setCancelable(false)
        customProgressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            customProgressDialog.dismiss()
            binding.constraintLayoutDiaChi.visibility = View.VISIBLE
        }, 3000)
    }
    private fun ShowSpinerTinh() {
        val service = RetrofitDiaChi.retrofitInstance.create(APIDiaChi::class.java)
        val call = service.getTinh()
        call.enqueue(object : Callback<List<Province>> {
            override fun onResponse(
                call: Call<List<Province>>,
                response: Response<List<Province>>
            ) {
                if (response.isSuccessful) {
                    val list: ArrayList<String> = arrayListOf()
                    for (i in 0 until response.body()?.size!!) {
                        list.add(response.body()?.get(i)?.name.toString())
                    }
                    val adapter = ArrayAdapter(
                        applicationContext,
                        android.R.layout.simple_spinner_item,
                        list
                    )
                    binding.spinerTinh.adapter = adapter
                    binding.spinerTinh.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                binding.spinerTinh.setSelection(position)
                                val idProvince = response.body()!![position].idProvince
                                showSpinerHuyen(idProvince)
                                name_province = parent?.getItemAtPosition(position).toString()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            }

            override fun onFailure(call: Call<List<Province>>, t: Throwable) {
                t.printStackTrace()
                Log.d("diachi", t.message.toString())
            }
        })
    }

    private fun showSpinerHuyen(idProvince: String) {
        val service = RetrofitDiaChi.retrofitInstance.create(APIDiaChi::class.java)
        val call = service.getHuyen()
        call.enqueue(object : Callback<List<District>> {
            override fun onResponse(
                call: Call<List<District>>,
                response: Response<List<District>>
            ) {
                if (response.isSuccessful) {
                    val list: ArrayList<String> = arrayListOf()
                    val listDistrict: ArrayList<String> = arrayListOf()
                    for (i in 0 until response.body()?.size!!) {
                        if (response.body()!![i].idProvince == idProvince) {
                            list.add(response.body()!![i].name)
                            listDistrict.add(response.body()!![i].idDistrict)
                        }
                    }
                    val adapter =
                        ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, list)
                    binding.spinerHuyen.adapter = adapter
                    binding.spinerHuyen.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                binding.spinerHuyen.setSelection(position)
                                val idDistrict = listDistrict[position]
                                showSpinerXa(idDistrict)
                                name_district = parent?.getItemAtPosition(position).toString()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            }

            override fun onFailure(call: Call<List<District>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun showSpinerXa(idDistrict: String) {
        val service = RetrofitDiaChi.retrofitInstance.create(APIDiaChi::class.java)
        val call = service.getXa()
        call.enqueue(object : Callback<List<Commune>> {
            override fun onResponse(call: Call<List<Commune>>, response: Response<List<Commune>>) {
                if (response.isSuccessful) {
                    val list: ArrayList<String> = arrayListOf()
                    for (i in 0 until response.body()!!.size) {
                        if (response.body()!![i].idDistrict == idDistrict) {
                            list.add(response.body()!![i].name)
                        }
                    }
                    val adapter =
                        ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, list)
                    binding.spinerXa.adapter = adapter
                    binding.spinerXa.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                binding.spinerXa.setSelection(position)
                                name_communce = parent?.getItemAtPosition(position).toString()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
            }

            override fun onFailure(call: Call<List<Commune>>, t: Throwable) {
            }
        })
    }
}