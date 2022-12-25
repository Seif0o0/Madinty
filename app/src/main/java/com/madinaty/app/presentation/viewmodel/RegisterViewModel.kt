package com.madinaty.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toUser
import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.domain.repository.RegisterRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.Constants
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val application: Application,
    private val repo: RegisterRepository
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    /* check if login request is done successfully */
    private val _registerState = MutableStateFlow(false)
    val registerState: StateFlow<Boolean> get() = _registerState

    fun registerState(value: Boolean) {
        _registerState.value = value
    }

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    val usernameState = MutableLiveData("")
    val usernameErrorState = MutableLiveData("")

    val firstNameState = MutableLiveData("")
    val firstNameErrorState = MutableLiveData("")

    val lastNameState = MutableLiveData("")
    val lastNameErrorState = MutableLiveData("")

    val emailState = MutableLiveData("")
    val emailErrorState = MutableLiveData("")

    val mobileState = MutableLiveData("")
    val mobileErrorState = MutableLiveData("")

    val dobState = MutableLiveData("")
    val dobErrorState = MutableLiveData("")

    val genderState = MutableLiveData(true)

    val passwordState = MutableLiveData("")
    val passwordErrorState = MutableLiveData("")

    val confirmPasswordState = MutableLiveData("")
    val confirmPasswordErrorState = MutableLiveData("")

    private val _startRegister = MutableStateFlow(false)
    val startRegister: StateFlow<Boolean> get() = _startRegister
    fun startRegister(value: Boolean) {
        _startRegister.value = value
    }

    fun onRegisterBtnClicked() {
        var pass = true
        if (!validateUsername(usernameState.value!!))
            pass = false
        if (!validateFirstName(firstNameState.value!!))
            pass = false
        if (!validateLastName(lastNameState.value!!))
            pass = false
        if (!validateEmail(emailState.value!!))
            pass = false
        if (!validateMobile(mobileState.value!!))
            pass = false
        if (!validateDob(dobState.value!!))
            pass = false
        if (!validatePassword(passwordState.value!!))
            pass = false
        if (!validateConfirmPassword(confirmPasswordState.value!!))
            pass = false

        if (pass)
            startRegister(true)
    }

    fun registerUser() {
        viewModelScope.launch {
            val map = mutableMapOf<String, String>()
            map["username"] = usernameState.value!!
            map["first_name"] = firstNameState.value!!
            map["last_name"] = lastNameState.value!!
            map["email"] = emailState.value!!
            map["mobile"] = mobileState.value!!
            map["gender"] =
                if (genderState.value!!) Constants.EDIT_MALE_VALUE else Constants.EDIT_FEMALE_VALUE
            map["dob"] = dobState.value!!
            map["password"] = passwordState.value!!
            map["password_confirmation"] = confirmPasswordState.value!!

            repo.register(map).collectLatest { result ->
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
        registerState(true)
    }

    private fun validateUsername(username: String) = if (username.isEmpty()) {
        usernameErrorState.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (username.length < 3) {
            usernameErrorState.value =
                application.getString(R.string.username_length_error_message)
            false
        } else
            true
    }

    private fun validateFirstName(firstName: String) = if (firstName.isEmpty()) {
        firstNameErrorState.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (firstName.length < 3) {
            firstNameErrorState.value =
                application.getString(R.string.first_name_length_error_message)
            false
        } else
            true
    }

    private fun validateLastName(lastName: String) = if (lastName.isEmpty()) {
        lastNameErrorState.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (lastName.length < 3) {
            lastNameErrorState.value =
                application.getString(R.string.last_name_length_error_message)
            false
        } else
            true
    }

    private fun validateEmail(email: String) = if (email.isEmpty()) {
        emailErrorState.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (validateEmailFormat(email))
            true
        else {
            emailErrorState.value =
                application.getString(R.string.email_validation_error_message)
            false
        }
    }

    private fun validateEmailFormat(email: String): Boolean {
        val regExpression =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        val pattern = Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE)

        return pattern.matcher(email).matches()
    }

    private fun validateMobile(mobile: String) = if (mobile.isEmpty()) {
        mobileErrorState.value = application.getString(R.string.empty_field_error_message)
        false
    } else true

    private fun validateDob(dob: String) = if (dob.isEmpty()) {
        dobErrorState.value = application.getString(R.string.empty_field_error_message)
        false
    } else true

    private fun validatePassword(password: String) = if (password.isEmpty()) {
        passwordErrorState.value = application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (password.length < 6) {
            passwordErrorState.value =
                application.getString(R.string.password_length_error_message)
            false
        } else
            true
    }

    private fun validateConfirmPassword(confirmPassword: String) = if (confirmPassword.isEmpty()) {
        confirmPasswordErrorState.value =
            application.getString(R.string.empty_field_error_message)
        false
    } else {
        if (confirmPassword != passwordState.value!!) {
            confirmPasswordErrorState.value =
                application.getString(R.string.confirmation_password_error_message)
            false
        } else
            true
    }

}