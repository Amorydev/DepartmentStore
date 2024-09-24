package com.amory.departmentstore.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvPromotion
import com.amory.departmentstore.databinding.FragmentPromotionBinding
import com.amory.departmentstore.model.Promotion
import com.amory.departmentstore.viewModel.PromotionViewModel

class PromotionFragment : Fragment() {
    private var _binding: FragmentPromotionBinding? = null
    val binding get() = _binding!!
    private val viewModel: PromotionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromotionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        setUpObserver()
    }

    private fun setUpViewModel() {
        viewModel.fetchDataPromotions()
    }


    private fun setUpObserver() {
        viewModel.listPromotion.observe(this) { listPromotion ->
            renderPromotion(listPromotion)
        }
    }

    private fun renderPromotion(listPromotions: MutableList<Promotion>?) {
        val adapter = RvPromotion(listPromotions!!)
        binding.rvKhuyenmai.adapter = adapter
        binding.rvKhuyenmai.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvKhuyenmai.setHasFixedSize(true)
    }


}