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
                    val produce_snack = response.body()?.result
                    val randomSanPham = produce_snack?.shuffled()
                    val list = randomSanPham?.take(12)
                    /*Toast.makeText(applicationContext,produce?.get(2)?.tensanpham,Toast.LENGTH_SHORT).show()*/
                    adapter = produce_snack?.let { RvSanPhamCacLoai(it,object :
                        OnClickSanPhamTheoLoai {
                        override fun onClickSanPhamTheoLoai(position: Int) {
                            Toast.makeText(this@SnackActivity,list!![position].tensanpham,Toast.LENGTH_SHORT).show()

                        }
                    }) }!!

                    binding.rvsanphamtheoloaiSnack.adapter = adapter
                    adapter.notifyDataSetChanged()

                    /* set khoảng cách giữa các item*/
                    val itemDecoration = ItemOffsetDecoration(3)
                    binding.rvsanphamtheoloaiSnack.addItemDecoration(itemDecoration)
                   /* binding.rvsanphamtheoloaiSnack.layoutManager = GridLayoutManager(
                        this@SnackActivity,
                        3,
                        GridLayoutManager.VERTICAL,
                        false
                    )*/
                    setRVLayoutManager()
                    addEventLoad(produce_snack, list as MutableList<SanPham>)
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
            val newlist = remainingItems.take(12)
            laySanPhamSnack()
            current.addAll(newlist)
            adapter.addData(newlist)
            scrollListener.setLoaded()
            binding.rvsanphamtheoloaiSnack.post {
                adapter.notifyDataSetChanged()
            }
        }, 3000)
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