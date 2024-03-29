package com.madinaty.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.data.mapper.toUser
import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.domain.model.User
import com.madinaty.app.domain.repository.SocialLoginRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialLoginViewModel @Inject constructor(
    private val application: Application,
    private val repo: SocialLoginRepository
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

    private val _startLogging = MutableStateFlow(false)

    val startLogging: StateFlow<Boolean> get() = _startLogging
    fun startLogging(value: Boolean) {
        _startLogging.value = value
    }

    var provider: String? = null
    var accessToken: String? = null
    var phoneIsEmpty = false
    var userId = ""
    fun socialLogin() {
        viewModelScope.launch {
            repo.socialLogin(provider!!, accessToken!!).collectLatest { result ->
                startLogging(false)
                when (result) {
                    is DataState.Loading -> {
                        _loadingState.emit(true)
                        _errorState.emit("")
                    }
                    is DataState.Success -> {
                        _loadingState.emit(false)
                        _errorState.emit("")
                        val data = result.data!!
                        val user = data.user.toUser()
                        if (user.phoneNumber.isEmpty() || user.phoneNumber == "0123456789") {
                            phoneIsEmpty = true
                            userId = user.id
                            UserInfo.token = data.token
                        } else {
                            phoneIsEmpty = false
                            saveUser(data)
                        }
                        loginState(true)
                    }
                    is DataState.Error -> {
                        _loadingState.emit(false)
                        _errorState.emit(result.message!!)
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
        UserInfo.phoneNumber = user.phoneNumber
        UserInfo.gender = user.gender
        UserInfo.dateOfBirth = user.dateOfBirth
        UserInfo.isApproved = user.isApproved
        UserInfo.isVerified = user.isVerified
        UserInfo.loginType = if (provider == "facebook") 1 else 2

    }
}