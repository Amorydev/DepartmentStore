package com.amory.departmentstore.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.Interface.OnClickHuyDonHang
import com.amory.departmentstore.activity.ThongTinDonHangActivity
import com.amory.departmentstore.adapter.RvChiTietDonHang
import com.amory.departmentstore.databinding.FragmentProcessingBinding
import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.OrderRespone
import com.amory.departmentstore.retrofit.APIBanHang.APICallDonHang
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProcessingFragment : Fragment() {
    private var _binding: FragmentProcessingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProcessingBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtNoOrder.visibility = View.INVISIBLE
        binding.imvNoOrder.visibility = View.INVISIBLE
        setupRecyclerView()
        showDonHang()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupRecyclerView() {
        binding.rvProcessing.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showDonHang() {
        val serviceOrder = RetrofitClient.retrofitInstance.create(APICallDonHang::class.java)
        val callOrder = serviceOrder.getOrder()
        callOrder.enqueue(object : Callback<OrderModel> {
            override fun onResponse(call: Call<OrderModel>, response: Response<OrderModel>) {
                if (response.isSuccessful) {
                    val list = response.body()?.data
                    val listOrderPending: MutableList<OrderRespone> = mutableListOf()
                    list?.forEach { order ->
                        if (order.status == "processing") {
                            listOrderPending.add(order)
                        }
                    }
                    if (listOrderPending.isEmpty()){
                        binding.txtNoOrder.visibility = View.VISIBLE
                        binding.imvNoOrder.visibility = View.VISIBLE
                        binding.rvProcessing.visibility = View.INVISIBLE
                    }else {
                        binding.txtNoOrder.visibility = View.INVISIBLE
                        binding.imvNoOrder.visibility = View.INVISIBLE
                        binding.rvProcessing.visibility = View.VISIBLE
                        val txtTinhTrang = "Đơn hàng đang được xử lý"
                        val adapter = RvChiTietDonHang(listOrderPending,txtTinhTrang)
                        binding.rvProcessing.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<OrderModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}