package com.amory.departmentstore.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amory.departmentstore.manager.PromotionManager
import com.amory.departmentstore.model.Promotion

class PromotionViewModel : ViewModel() {
    private val _listPromotion = MutableLiveData<MutableList<Promotion>>()
    val listPromotion : LiveData<MutableList<Promotion>> get() = _listPromotion

    fun fetchDataPromotions() {
        PromotionManager.getPromotions({ listPromotions ->
            _listPromotion.postValue(listPromotions!!)
        }, { _ ->
        })
    }
}