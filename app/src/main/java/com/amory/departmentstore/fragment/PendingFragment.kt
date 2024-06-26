package com.amory.departmentstore.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvChiTietDonHang
import com.amory.departmentstore.databinding.FragmentPendingBinding
import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.OrderRespone
import com.amory.departmentstore.retrofit.APIBanHang.APICallDonHang
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PendingFragment : Fragment() {

    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
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
        binding.rvPending.layoutManager = LinearLayoutManager(requireContext())
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
                        if (order.status == "pending") {
                            listOrderPending.add(order)
                        }
                    }
                    if (listOrderPending.isEmpty()){
                        binding.txtNoOrder.visibility = View.VISIBLE
                        binding.imvNoOrder.visibility = View.VISIBLE
                        binding.rvPending.visibility = View.INVISIBLE
                    }else{
                        binding.txtNoOrder.visibility = View.INVISIBLE
                        binding.imvNoOrder.visibility = View.INVISIBLE
                        binding.rvPending.visibility = View.VISIBLE
                        val txtTinhTrang = "Chờ xác nhận đơn hàng"
                        val adapter = RvChiTietDonHang(listOrderPending,txtTinhTrang)
                        binding.rvPending.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<OrderModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
