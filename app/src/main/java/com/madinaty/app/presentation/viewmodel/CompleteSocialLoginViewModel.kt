package com.madinaty.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toUser
import com.madinaty.app.data.response.RegisterInfoResponse
import com.madinaty.app.domain.model.User
import com.madinaty.app.domain.repository.ProfileRepository
import com.madinaty.app.domain.repository.RegisterRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.Constants
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteSocialLoginViewModel @Inject constructor(
    private val application: Application,
    private val repo: ProfileRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _registerState = MutableStateFlow(false)
    val registerState: StateFlow<Boolean> get() = _registerState

    fun registerState(value: Boolean) {
        _registerState.value = value
    }

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    val phoneNumberState = MutableLiveData("")
    val phoneNumberErrorState = MutableLiveData("")

    val dobState = MutableLiveData("")
    val dobErrorState = MutableLiveData("")

    val genderState = MutableLiveData(true)

    private val _startRegister = MutableStateFlow(false)
    val startRegister: StateFlow<Boolean> get() = _startRegister
    fun startRegister(value: Boolean) {
        _startRegister.value = value
    }

    fun onRegisterBtnClicked() {
        var pass = true
        if (!validatePhoneNumber(phoneNumberState.value!!)) pass = false
        if (!validateDob(dobState.value!!)) pass = false

        if (pass) startRegister(true)
    }

    fun registerUser() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["mobile"] = phoneNumberState.value!!
            map["gender"] =
                if (genderState.value!!) Constants.EDIT_MALE_VALUE else Constants.EDIT_FEMALE_VALUE
            map["dob"] = dobState.value!!
            map["user_id"] = state.get<String>("userId")!!

            repo.updateProfileInfo(map).collectLatest { result ->
                startRegister(false)
                when (result) {
                    is DataState.Loading -> {
                        _loadingState.emit(true)
                        _errorState.emit("")
                    }
                    is DataState.Success -> {
                        _loadingState.emit(false)
                        _errorState.emit("")
                        saveUser(result.data!!)
                    }
                    is DataState.Error -> {
                        _loadingState.emit(false)
                        _errorState.emit(result.message!!)
                    }
                }
            }
        }
    }

    private fun saveUser(user: User) {
        UserInfo.userId = user.id
        UserInfo.username = user.username
        UserInfo.firstName = user.firstName
        UserInfo.lastName = user.lastName
        UserInfo.email = user.email
        UserInfo.phoneNumber = user.phoneNumber
        UserInfo.gender = user.gender
        UserInfo.dateOfBirth = user.dateOfBirth
        UserInfo.isApproved = user.isApproved
        UserInfo.isVerified = user.isVerified
        UserInfo.loginType = 2
        registerState(true)
    }

    private fun validatePhoneNumber(mobile: String) = if (mobile.isEmpty()) {
        phoneNumberErrorState.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if(mobile.length<11){
            phoneNumberErrorState.value = application.getString(R.string.invalid_mobile_error_message)
            false
        }else true
    }

    private fun validateDob(dob: String) = if (dob.isEmpty()) {
        dobErrorState.value = application.getString(R.string.empty_field_error_message)
        false
    } else true
}