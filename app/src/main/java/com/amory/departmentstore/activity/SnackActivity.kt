package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.adapter.ItemOffsetDecoration
import com.amory.departmentstore.adapter.RvLoadMoreScroll
import com.amory.departmentstore.adapter.RvSanPhamCacLoai
import com.amory.departmentstore.databinding.ActivitySnackBinding
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.OnClickSanPhamTheoLoai
import com.amory.departmentstore.model.OnLoadMoreListener
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class SnackActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnackBinding
    lateinit var adapter: RvSanPhamCacLoai
    private lateinit var scrollListener: RvLoadMoreScroll
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private val loai: Int = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        laySanPhamSnack()
    }

    private fun laySanPhamSnack() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getSanPhamTheoLoai(loai)
        call.enqueue(object : retrofit2.Callback<SanPhamModel> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                if (response.isSuccessful) {
                    val produce = response.body()?.result
                    if (!produce.isNullOrEmpty()) {

                        val randomSanPham = produce.shuffled()
                        val list = randomSanPham.take(12)

                        if (list.isNotEmpty()) {
/*
                            Toast.makeText(applicationContext, produce[2].tensanpham,Toast.LENGTH_SHORT).show()
*/
                            adapter = RvSanPhamCacLoai(
                                list.toMutableList(),
                                object : OnClickSanPhamTheoLoai {
                                    override fun onClickSanPhamTheoLoai(position: Int) {
                                        /* Toast.makeText(this@GaoActivity,list[position].tensanpham,Toast.LENGTH_SHORT).show()*/
                                        val intent = Intent(
                                            this@SnackActivity,
                                            ChiTietSanPhamActivity::class.java
                                        )
                                        intent.putExtra("tensanpham", list[position].tensanpham)
                                        intent.putExtra("giasanpham", list[position].giasanpham)
                                        intent.putExtra("hinhanhsanpham", list[position].hinhanh)
                                        intent.putExtra("motasanpham", list[position].mota)
/*
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
*/
                                        startActivity(intent)
                                    }
                                })
                            binding.rvsanphamtheoloaiSnack.adapter = adapter
                            adapter.notifyDataSetChanged()
                            setRVLayoutManager()
                            addEventLoad(produce, list as MutableList<SanPham>)
                        }
                    }


                }
            }

            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
                Log.e("Amory", "Error occurred: ${t.message}", t)
            }
        })
    }

    private fun addEventLoad(produce: MutableList<SanPham>, list: MutableList<SanPham>) {
        scrollListener = RvLoadMoreScroll(mLayoutManager as GridLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                LoadMoreData(produce, list)
            }
        })
        binding.rvsanphamtheoloaiSnack.addOnScrollListener(scrollListener)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun LoadMoreData(produce: MutableList<SanPham>, current: MutableList<SanPham>) {

        adapter.addLoadingView()
        Handler().postDelayed({
            adapter.removeLoadingView()
            val remainingItems = produce.filter {
                /*san pham đã hiênr thị không còn chưa trong produce*/
                !current.contains(it)
            }
            val newList = remainingItems.take(12)
            laySanPhamSnack()
            current.addAll(newList)
            adapter.addData(newList)
            scrollListener.setLoaded()
            binding.rvsanphamtheoloaiSnack.post {
                adapter.notifyDataSetChanged()
            }
        }, 1000)
    }

    private fun setRVLayoutManager() {
        mLayoutManager = GridLayoutManager(this, 3)
        binding.rvsanphamtheoloaiSnack.layoutManager = mLayoutManager
        binding.rvsanphamtheoloaiSnack.setHasFixedSize(true)
        binding.rvsanphamtheoloaiSnack.adapter = adapter
        (mLayoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        Constant.VIEW_TYPE_ITEM -> 1
                        Constant.VIEW_TYPE_LOADING -> 3
                        else -> -1
                    }
                }
            }
    }
}