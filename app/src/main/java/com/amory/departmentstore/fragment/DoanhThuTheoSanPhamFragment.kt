package com.amory.departmentstore.fragment

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RevenueByProductAdapter
import com.amory.departmentstore.databinding.FragmentDeliveredBinding
import com.amory.departmentstore.databinding.FragmentDoanhThuTheoSanPhamBinding
import com.amory.departmentstore.model.RevenueByCategoriesResponse
import com.amory.departmentstore.model.RevenueByProductModel
import com.amory.departmentstore.model.RevenueByProductResponse
import com.amory.departmentstore.retrofit.APIBanHang.APICallDoanhThu
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoanhThuTheoSanPhamFragment : Fragment() {
    private  var _binding:FragmentDoanhThuTheoSanPhamBinding ?= null
    private val binding get() = _binding!!
    private lateinit var listRevenue:MutableList<RevenueByProductResponse>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoanhThuTheoSanPhamBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listRevenue = mutableListOf()
        onShowRv()
    }

    private fun onShowRv() {
        val service = RetrofitClient.retrofitInstance.create(APICallDoanhThu::class.java)
        val call = service.layDoanhThuTheoSanPham()
        call.enqueue(object : Callback<RevenueByProductModel>{
            override fun onResponse(
                call: Call<RevenueByProductModel>,
                response: Response<RevenueByProductModel>
            ) {
                if (response.isSuccessful){
                    response.body()?.data?.let {
                        listRevenue.addAll(it)
                    }
                    val adapter = RevenueByProductAdapter(listRevenue)
                    binding.rvRevenueProducts.adapter = adapter
                    binding.rvRevenueProducts.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                }
            }

            override fun onFailure(call: Call<RevenueByProductModel>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}