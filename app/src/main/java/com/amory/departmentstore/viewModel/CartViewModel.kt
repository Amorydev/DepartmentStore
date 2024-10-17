package com.amory.departmentstore.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.model.GioHang

class CartViewModel:ViewModel() {
    private val _totalMoney = MutableLiveData<Long>()
    val totalMoney: LiveData<Long> get() = _totalMoney

    private var cartProducts = MutableLiveData<MutableList<GioHang>>()

    private val _selectedProducts = MutableLiveData<MutableList<GioHang>>()
    val selectedProducts:LiveData<MutableList<GioHang>> get() = _selectedProducts

    init {
        cartProducts.value = Utils.manggiohang
        _selectedProducts.value = mutableListOf()
    }

    fun toggleSelectAll(isChecked: Boolean) {
        val selectedList = selectedProducts.value ?: mutableListOf()
        if (isChecked) {
            cartProducts.value?.forEach { product ->
                if (!selectedList.contains(product)) {
                    selectedList.add(product)
                }
            }
        } else {
            selectedList.clear()
        }
        _selectedProducts.postValue(selectedList)
    }

     fun tinhTongTienHang() {
        var tongtienhang: Long = 0
        for (i in 0 until Utils.mangmuahang.size) {
            tongtienhang += Utils.mangmuahang[i].giasanphamgiohang.toLong()
        }
        _totalMoney.postValue(tongtienhang)
    }
}