package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.adapter.ItemOffsetDecoration
import com.amory.departmentstore.adapter.RvLoadMoreScroll
import com.amory.departmentstore.adapter.RvSanPhamCacLoai
import com.amory.departmentstore.databinding.ActivityTraiCayBinding
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.OnClickSanPhamTheoLoai
import com.amory.departmentstore.model.OnLoadMoreListener
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class TraiCayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTraiCayBinding
    lateinit var adapter: RvSanPhamCacLoai
    private lateinit var scrollListener: RvLoadMoreScroll
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private val loai:Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraiCayBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        laySanPhamTraiCay()
        
    }

    private fun laySanPhamTraiCay() {
            val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
            val call = service.getSanPhamTheoLoai(loai)
            call.enqueue(object : retrofit2.Callback<SanPhamModel>{
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                    if(response.isSuccessful){
                        val produce = response.body()?.result
                        val randomSanPham = produce?.shuffled()
                        val list = randomSanPham?.take(12)
                        /*Toast.makeText(applicationContext,produce?.get(2)?.tensanpham,Toast.LENGTH_SHORT).show()*/
                        adapter = produce?.let { RvSanPhamCacLoai(it,object :
                            OnClickSanPhamTheoLoai {
                            override fun onClickSanPhamTheoLoai(position: Int) {
                                Toast.makeText(this@TraiCayActivity,list!![position].tensanpham,Toast.LENGTH_SHORT).show()

                            }
                        }) }!!

                        binding.rvsanphamtheoloaiTraicay.adapter = adapter
                        adapter.notifyDataSetChanged()

                        /* set khoảng cách giữa các item*/
                        val itemDecoration = ItemOffsetDecoration(3)
                        binding.rvsanphamtheoloaiTraicay.addItemDecoration(itemDecoration)
                        /*binding.rvsanphamtheoloaiTraicay.layoutManager = GridLayoutManager(
                            this@TraiCayActivity,
                            3,
                            GridLayoutManager.VERTICAL,
                            false
                        )*/
                        setRVLayoutManager()
                        addEventLoad(produce, list as MutableList<SanPham>)
                    }
                }
                override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                    t.printStackTrace()
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
        binding.rvsanphamtheoloaiTraicay.addOnScrollListener(scrollListener)
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
            val newlist = remainingItems.take(12)
            laySanPhamTraiCay()
            current.addAll(newlist)
            adapter.addData(newlist)
            scrollListener.setLoaded()
            binding.rvsanphamtheoloaiTraicay.post {
                adapter.notifyDataSetChanged()
            }
        }, 3000)
    }

    private fun setRVLayoutManager() {
        mLayoutManager = GridLayoutManager(this, 3)
        binding.rvsanphamtheoloaiTraicay.layoutManager = mLayoutManager
        binding.rvsanphamtheoloaiTraicay.setHasFixedSize(true)
        binding.rvsanphamtheoloaiTraicay.adapter = adapter
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