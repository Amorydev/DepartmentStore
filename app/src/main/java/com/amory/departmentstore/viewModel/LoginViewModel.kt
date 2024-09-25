package com.amory.departmentstore.viewModel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amory.departmentstore.manager.AccountManager
import com.amory.departmentstore.model.LoginModel
import com.amory.departmentstore.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<LoginModel>()
    val loginResult: MutableLiveData<LoginModel> = _loginResult

    private val _loginError = MutableLiveData<String>()
    val loginError: MutableLiveData<String> = _loginError

    private val _infoUser = MutableLiveData<User>()
    val infoUser: MutableLiveData<User> = _infoUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            AccountManager.login(email, password, { login ->
                _loginResult.postValue(login)
            }, { error ->
                _loginError.postValue(error)

            })
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            AccountManager.getInfoUser({ user ->
                _infoUser.postValue(user)
            }, { error ->
                _loginError.postValue(error)
            })
        }
    }

    fun saveTokenFCMUser(userId: Int?) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (!TextUtils.isEmpty(token)) {
                    sendToFirebase(token, userId!!)
                }
            }
    }

    fun saveTokenFCMAdmin(adminId: Int?) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (!TextUtils.isEmpty(token)) {
                    sendToFirebase(token, adminId!!)
                }
            }
    }

    private fun sendToFirebase(token: String, userId: Int) {
        val db = FirebaseFirestore.getInstance()
        val tokensCollection = db.collection("tokens")

        tokensCollection
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val useToken = hashMapOf(
                        "token" to token,
                        "userId" to userId
                    )
                    tokensCollection
                        .add(useToken)
                        .addOnSuccessListener {
                            Log.d("Token", "Token added successfully")
                        }
                        .addOnFailureListener {
                            Log.d("Error token", it.message.toString())
                        }
                } else {
                    for (document in querySnapshot.documents) {
                        document.reference
                            .update("token", token)
                            .addOnSuccessListener {
                                Log.d("Token", "Token updated successfully")
                            }
                            .addOnFailureListener {
                                Log.d("Error token", it.message.toString())
                            }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("Error token", it.message.toString())
            }
    }
}