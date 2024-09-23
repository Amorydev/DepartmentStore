package com.amory.departmentstore.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amory.departmentstore.R
import com.amory.departmentstore.viewModel.PromotionViewModel

class PromotionFragment : Fragment() {

    companion object {
        fun newInstance() = PromotionFragment()
    }

    private val viewModel: PromotionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_promotion, container, false)
    }
}