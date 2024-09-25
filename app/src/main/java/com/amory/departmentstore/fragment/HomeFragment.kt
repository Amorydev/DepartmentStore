package com.amory.departmentstore.fragment

import android.annotation.SuppressLint
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickRvLoaiSanPham
import com.amory.departmentstore.Interface.OnClickRvSanPham
import com.amory.departmentstore.Interface.OnLoadMoreListener
import com.amory.departmentstore.activity.user.ChiTietSanPhamActivity
import com.amory.departmentstore.activity.user.SanPhamTheoLoaiActivity
import com.amory.departmentstore.adapter.RvLoadMoreScroll
import com.amory.departmentstore.adapter.RvLoaiSanPham
import com.amory.departmentstore.adapter.RvProducts
import com.amory.departmentstore.databinding.FragmentHomeBinding
import com.amory.departmentstore.model.Category
import com.amory.departmentstore.model.Constant.VIEW_TYPE_ITEM
import com.amory.departmentstore.model.Constant.VIEW_TYPE_LOADING
import com.amory.departmentstore.model.Product
import com.amory.departmentstore.model.Promotion
import com.amory.departmentstore.viewModel.HomeViewModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: RvProducts
    private lateinit var scrollListener: RvLoadMoreScroll
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setUpViewModel()
        setUpObserver()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setUpViewModel() {
        homeViewModel.fetchDataProducts()
        homeViewModel.fetchDataCategories()
        homeViewModel.fetchDataPromotions()
    }

    private fun setUpObserver() {
        homeViewModel.listPromotion.observe(viewLifecycleOwner) { listPromotion ->
            renderSlidePromotion(listPromotion)
        }
        homeViewModel.listCategories.observe(viewLifecycleOwner) { listCategories ->
            renderCategories(listCategories)
        }
        homeViewModel.listProduct.observe(viewLifecycleOwner) { listProduct ->
            renderProducts(listProduct)
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner){isLoading ->
            if (isLoading!!){
                binding.shimmerframe.visibility = View.VISIBLE
                binding.shimmerframe.startShimmer()
            }else{
                binding.shimmerframe.visibility = View.GONE
                binding.shimmerframe.stopShimmer()
                binding.layoutContrains.visibility = View.VISIBLE
            }
        }
    }
    private fun renderCategories(listCategories: MutableList<Category>?) {
        val adapter = RvLoaiSanPham(listCategories, object : OnClickRvLoaiSanPham {
            override fun onClickLoaiSanPham(position: Int) {
                val intent = Intent(
                    requireContext(),
                    SanPhamTheoLoaiActivity::class.java
                )
                intent.putExtra("loai", listCategories!![position].id)
                intent.putExtra("tenloaisanpham", listCategories[position].name)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        })
        binding.rvloaisanpham.adapter = adapter
        binding.rvloaisanpham.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL, false
        )
    }

    private fun renderSlidePromotion(listPromotion: MutableList<Promotion>?) {
        val listUrl = ArrayList<SlideModel>()
        for (i in listPromotion!!.indices) {
            listUrl.add(SlideModel(listPromotion[i].imageUrl))
        }
        binding.imageSlider.setItemClickListener(
            object : ItemClickListener {
                override fun doubleClick(position: Int) {
                }

                override fun onItemSelected(position: Int) {

                }
            }
        )
        binding.imageSlider.setImageList(listUrl, ScaleTypes.FIT)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderProducts(listProduct: MutableList<Product>?) {
        val list = listProduct!!.shuffled().take(12) as MutableList<Product>
        adapter = RvProducts(object : OnClickRvSanPham {
            override fun onClickSanPham(position: Int) {
                startChiTietSanPham(list[position])
            }
        })
        adapter.updateList(list as MutableList<Product>)
        adapter.notifyDataSetChanged()
        binding.rvSanpham.adapter = adapter
        setRVLayoutManager()
        addEventLoad(listProduct, list)
    }

    private fun startChiTietSanPham(product: Product) {
        val intent = Intent(
            requireContext(), ChiTietSanPhamActivity::class.java
        )
        intent.putExtra("idsanpham", product.id)
        startActivity(intent)
    }

    private fun addEventLoad(produce: MutableList<Product>, list: MutableList<Product>) {
        scrollListener = RvLoadMoreScroll(mLayoutManager as GridLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                loadMoreData(produce.toMutableList(), list)
            }
        })
        binding.rvSanpham.addOnScrollListener(scrollListener)
    }

    private fun setRVLayoutManager() {
        mLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvSanpham.layoutManager = mLayoutManager
        binding.rvSanpham.setHasFixedSize(true)
        binding.rvSanpham.adapter = adapter
        (mLayoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        VIEW_TYPE_ITEM -> 1
                        VIEW_TYPE_LOADING -> 3
                        else -> -1
                    }
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMoreData(produce: MutableList<Product>, current: MutableList<Product>) {
        adapter.addLoadingView()
        lifecycleScope.launch {
            delay(1000)
            adapter.removeLoadingView()
            val remainingItems = produce.filter {
                !current.contains(it)
            }
            val newList = remainingItems.take(12)

            current.addAll(newList)
            adapter.addData(newList)

            scrollListener.setLoaded()
            binding.rvSanpham.post {
                adapter.notifyDataSetChanged()
            }
        }
    }


}