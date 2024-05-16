package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityDiaChiBinding
import com.amory.departmentstore.model.Commune
import com.amory.departmentstore.model.District
import com.amory.departmentstore.model.Province
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APIDiaChi
import com.amory.departmentstore.retrofit.RetrofitDiaChi
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class DiaChiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaChiBinding
    private var name_province = ""
    private var name_district = ""
    private var name_communce = ""
    private var full_name = ""
    private var phone = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaChiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        onClickXacNhan()
    }



    private fun initViews() {
        val user = Paper.book().read<User>("user")
        if (user == null) {
            full_name = Utils.user_current?.first_name + " " + Utils.user_current?.last_name
            phone = Utils.user_current?.mobiphone.toString()
            binding.nameET.setText(full_name)
            binding.mobileET.setText(phone)
        } else {
            full_name = user.first_name + " " + user.last_name
            phone = user.mobiphone
            binding.nameET.setText(full_name)
            binding.mobileET.setText(phone)
        }
        ShowSpinerTinh()
    }
    private fun onClickXacNhan() {
        binding.btnXacnhan.setOnClickListener {
            val sonha = binding.diachiET.text?.trim().toString()
            val address = "$sonha, $name_communce, $name_district, $name_province"
            /*Toast.makeText(applicationContext, address,Toast.LENGTH_SHORT).show()*/
            val intent = Intent(this,ThanhToanActivity::class.java)
            intent.putExtra("address",address)
            intent.putExtra("fullname",full_name)
            intent.putExtra("phone",phone)
            startActivity(intent)
            finish()

        }
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