package com.madinaty.app.presentation.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toUser
import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.domain.repository.PhoneLoginRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.CustomDialog
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PhoneLoginViewModel @Inject constructor(
    private val application: Application,
    private val repo: PhoneLoginRepository
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    /* check if login request is done successfully */
    private val _loginState = MutableStateFlow(false)
    val loginState: StateFlow<Boolean> get() = _loginState

    fun loginState(value: Boolean) {
        _loginState.value = value
    }

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    val phoneNumberState = MutableLiveData("")
    val phoneNumberErrorState = MutableLiveData("")

    val passwordState = MutableLiveData("")
    val passwordErrorState = MutableLiveData("")

    private val _startLogging = MutableStateFlow(false)
    val startLogging: StateFlow<Boolean> get() = _startLogging
    fun startLogging(value: Boolean) {
        _startLogging.value = value
    }

    fun onLoginBtnClicked() {
        var pass = true
        if (phoneNumberState.value!!.isEmpty()) {
            phoneNumberErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }
        if (passwordState.value!!.isEmpty()) {
            passwordErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }

        if (pass)
            startLogging(true)
    }

    fun login() {
        viewModelScope.launch {
            repo.login(phoneNumber = "+2${phoneNumberState.value}", password = passwordState.value!!)
                .collect { result ->
                    startLogging(false)
                    when (result) {
                        is DataState.Success -> {
                            _loadingState.emit(false)
                            _errorState.emit("")
                            saveUser(result.data!!)
                        }
                        is DataState.Error -> {
                            _loadingState.emit(false)
                            _errorState.emit(result.message!!)
                        }
                        is DataState.Loading -> {
                            _loadingState.emit(true)
                            _errorState.emit("")
                        }
                    }

                }
        }
    }

    private fun saveUser(data: PhoneLoginInfoResponse) {
        val user = data.user.toUser()
        val token = data.token

        UserInfo.userId = user.id
        UserInfo.token = token
        UserInfo.username = user.username
        UserInfo.firstName = user.firstName
        UserInfo.lastName = user.lastName
        UserInfo.email = user.email
        UserInfo.city = user.city ?: ""
        UserInfo.phoneNumber = user.phoneNumber
        UserInfo.gender = user.gender
        UserInfo.dateOfBirth = user.dateOfBirth
        UserInfo.isApproved = user.isApproved
        UserInfo.isVerified = user.isVerified
        UserInfo.loginType = 0
        loginState(true)
    }
}