package com.amory.departmentstore.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amory.departmentstore.manager.CategoryManager
import com.amory.departmentstore.manager.ProductManager
import com.amory.departmentstore.manager.PromotionManager
import com.amory.departmentstore.model.Category
import com.amory.departmentstore.model.Product
import com.amory.departmentstore.model.Promotion
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _listProduct = MutableLiveData<MutableList<Product>?>()
    val listProduct: LiveData<MutableList<Product>?> get() = _listProduct

    private val _listCategories = MutableLiveData<MutableList<Category>?>()
    val listCategories: LiveData<MutableList<Category>?> get() = _listCategories

    private val _listPromotion = MutableLiveData<MutableList<Promotion>?>()
    val listPromotion: LiveData<MutableList<Promotion>?> get() = _listPromotion

    private val _isLoading = MutableLiveData<Boolean?>()
    val isLoading: LiveData<Boolean?> get() = _isLoading

    init {
        _isLoading.postValue(true)
    }

    fun fetchDataCategories() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            CategoryManager.getCategories({ listCategories ->
                _listCategories.postValue(listCategories)
                _isLoading.postValue(false)
            }, { _ ->
            })
        }
    }

    fun fetchDataProducts() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            ProductManager.getProducts({ listProducts ->
                _isLoading.postValue(false)
                _listProduct.postValue(listProducts)

            }, { _ ->
            })
        }
    }

    fun fetchDataPromotions() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            PromotionManager.getPromotions({ listPromotions ->
                _isLoading.postValue(false)
                _listPromotion.postValue(listPromotions)
            }, { _ ->
            })

        }
    }
}