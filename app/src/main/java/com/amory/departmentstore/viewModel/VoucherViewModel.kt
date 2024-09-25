package com.amory.departmentstore.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amory.departmentstore.manager.VoucherManager
import com.amory.departmentstore.model.Voucher

class VoucherViewModel : ViewModel() {
    private val _listVoucher = MutableLiveData<MutableList<Voucher>>()
    val listVoucher: LiveData<MutableList<Voucher>> = _listVoucher

    private val _searchVoucherResult = MutableLiveData<MutableList<Voucher>>()
    val searchVoucherResult: LiveData<MutableList<Voucher>> = _searchVoucherResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getVoucher() {
        VoucherManager.getVoucher({ voucherList ->
            if (voucherList.isNotEmpty()) {
                _listVoucher.postValue(voucherList)
            } else {
                _errorMessage.postValue("No voucher found")
            }
        }, { errorMessage ->
            _errorMessage.postValue(errorMessage)
        })
    }

    fun searchVoucher(key: String) {
        VoucherManager.searchVoucher(key, { voucherList ->
            if (voucherList.isNotEmpty()) {
                _searchVoucherResult.postValue(voucherList)
            } else {
                _errorMessage.postValue("No voucher found")
            }
        }, { errorMessage ->
            _errorMessage.postValue(errorMessage)
        })
    }
}