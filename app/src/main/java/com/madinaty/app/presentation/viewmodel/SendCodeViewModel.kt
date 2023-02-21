package com.madinaty.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.R
import com.madinaty.app.domain.repository.SendCodeRepository
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendCodeViewModel @Inject constructor(
    private val application: Application,
    private val repo: SendCodeRepository
) : ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _sendCodeState = MutableStateFlow(false)
    val sendCodeState: StateFlow<Boolean> get() = _sendCodeState

    fun sendCodeState(value: Boolean) {
        _sendCodeState.value = value
    }

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    val phoneNumberState = MutableLiveData("")
    val phoneNumberErrorState = MutableLiveData("")

    private val _startSendCode = MutableStateFlow(false)
    val startSendCode: StateFlow<Boolean> get() = _startSendCode
    fun startSendCode(value: Boolean) {
        _startSendCode.value = value
    }

    fun onSendBtnClicked() {
        var pass = true
        if (phoneNumberState.value!!.isEmpty()) {
            phoneNumberErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        } else {
            if (phoneNumberState.value!!.length < 11) {
                phoneNumberErrorState.value =
                    application.getString(R.string.invalid_mobile_error_message)
                pass = false
            }
        }

        if (pass)
            startSendCode(true)
    }

    var userId = ""
    fun sendCode() {
        viewModelScope.launch {
            repo.sendCode(phoneNumber = "+2${phoneNumberState.value}")
                .collect { result ->
                    startSendCode(false)
                    when (result) {
                        is DataState.Success -> {
                            _loadingState.emit(false)
                            _errorState.emit("")
                            userId = result.data?.info?.target?.userId ?: ""
                            sendCodeState(true)
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
}