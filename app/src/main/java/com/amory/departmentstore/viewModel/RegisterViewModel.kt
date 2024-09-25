package com.amory.departmentstore.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amory.departmentstore.manager.AccountManager
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.RegisterModel

class RegisterViewModel : ViewModel() {
    private val _registerResult = MutableLiveData<RegisterModel>()
    val registerResult: LiveData<RegisterModel> = _registerResult

    private val _registerError = MutableLiveData<String>()
    val registerError: LiveData<String> = _registerError

    fun register(firstName: String, lastName: String, email: String, password: String) {
        AccountManager.register(firstName, lastName, email, password, { registerResult ->
            _registerResult.postValue(registerResult)
        }, { errorMessage ->
            _registerError.postValue(errorMessage)
        })
    }
}