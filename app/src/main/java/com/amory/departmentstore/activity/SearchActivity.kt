package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivitySearchBinding
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    val list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickback()
        onClickSearch()
    }

    private fun onClickSearch() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 0) {
                    list.clear()
                    val adapter =
                        ArrayAdapter(this@SearchActivity, android.R.layout.simple_list_item_1, list)
                    binding.lvSearch.adapter = adapter
                } else {
                    Search(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun Search(search: String) {
        list.clear()
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.timkiem(search)
        val adapter = ArrayAdapter(this@SearchActivity, android.R.layout.simple_list_item_1, list)
        call.enqueue(object : retrofit2.Callback<SanPhamModel> {
            override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    for (i in 0 until response.body()?.result?.size!!) {
                        list.add(result?.get(i)?.name!!)
                    }
                    binding.lvSearch.adapter = adapter
                    binding.lvSearch.setOnItemClickListener { parent, view, position, id ->
                        val intent = Intent(this@SearchActivity, ChiTietSanPhamActivity::class.java)
                        intent.putExtra("name", result?.get(position)?.name)
                        intent.putExtra("idsanpham", result?.get(position)?.id)
                        intent.putExtra("price", result?.get(position)?.price)
                        intent.putExtra("hinhanhsanpham", result?.get(position)?.image_url)
                        intent.putExtra("motasanpham", result?.get(position)?.description)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } else {
                    list.clear()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun onClickback() {
        binding.imbBack.setOnClickListener {
            onBackPressed()
        }
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}