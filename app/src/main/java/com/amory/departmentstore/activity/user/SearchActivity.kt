package com.amory.departmentstore.activity.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import com.amory.departmentstore.databinding.ActivitySearchBinding
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Response

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
        val categoryId = intent.getIntExtra("categoryId",0)
       /* Toast.makeText(this,categoryId.toString(),Toast.LENGTH_SHORT).show()*/
        val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
        val call = service.timkiem(categoryId, search)
        val adapter = ArrayAdapter(this@SearchActivity, android.R.layout.simple_list_item_1, list)
        call.enqueue(object : retrofit2.Callback<SanPhamModel> {
            override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                if (response.isSuccessful) {
                    val result = response.body()?.data
                    for (i in 0 until response.body()?.data?.size!!) {
                        list.add(result?.get(i)?.name!!)
                    }
                    binding.lvSearch.adapter = adapter
                    binding.lvSearch.setOnItemClickListener { parent, view, position, id ->
                        val intent = Intent(this@SearchActivity, ChiTietSanPhamActivity::class.java)
                        intent.putExtra("name", result?.get(position)?.name)
                        intent.putExtra("idsanpham", result?.get(position)?.id)
                        intent.putExtra("price", result?.get(position)?.price)
                        intent.putExtra("hinhanhsanpham", result?.get(position)?.imageUrl)
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