package com.madinaty.app.presentation.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.R
import com.madinaty.app.domain.repository.OTPVerificationRepository
import com.madinaty.app.domain.repository.SendCodeRepository
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val application: Application,
    private val repo: OTPVerificationRepository,
    private val codeRepo: SendCodeRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _verifyCodeState = MutableStateFlow(false)
    val verifyCodeState: StateFlow<Boolean> get() = _verifyCodeState

    fun verifyCodeState(value: Boolean) {
        _verifyCodeState.value = value
    }

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    val codeState = MutableLiveData("")
    val codeErrorState = MutableLiveData("")

    private val _startVerifyCode = MutableStateFlow(false)
    val startVerifyCode: StateFlow<Boolean> get() = _startVerifyCode
    fun startVerifyCode(value: Boolean) {
        _startVerifyCode.value = value
    }

    private val _startReSendCode = MutableStateFlow(false)
    val startReSendCode: StateFlow<Boolean> get() = _startReSendCode
    fun startReSendCode(value: Boolean) {
        _startReSendCode.value = value
    }

//    private val _resendCodeState = MutableStateFlow(false)
//    val resendCodeState: StateFlow<Boolean> get() = _resendCodeState
//
//    fun resendCodeState(value: Boolean) {
//        _resendCodeState.value = value
//    }

    private val _timer = MutableStateFlow("5:00")
    val timer: StateFlow<String> get() = _timer

    init {
        startCounter()
    }

    lateinit var countDownTimer: CountDownTimer
    private fun startCounter() {
        // time count down for 5 minutes,
        // with 1 second as countDown interval
        countDownTimer = object : CountDownTimer(300000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(milliseconds: Long) {
                val minutes = milliseconds / 1000 / 60
                val seconds = milliseconds / 1000 % 60
                _timer.value = "$minutes:$seconds"
            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                _timer.value = ""
            }
        }.start()


    }


    fun onVerifyBtnClicked(code: String) {
        codeState.value = code
        var pass = true
        if (codeState.value!!.isEmpty()) {
            codeErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }

        if (pass) startVerifyCode(true)
    }


    fun verifyCode() {
        viewModelScope.launch {
            repo.verifyCode(code = codeState.value!!, userId = state.get<String>("userId")!!)
                .collect { result ->
                    startVerifyCode(false)
                    when (result) {
                        is DataState.Success -> {
                            _loadingState.emit(false)
                            _errorState.emit("")
                            verifyCodeState(true)
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

    fun reSendCode() {
        viewModelScope.launch {
            codeRepo.sendCode(phoneNumber = state.get<String>("phoneNumber")!!).collect { result ->
                startReSendCode(false)
                when (result) {
                    is DataState.Success -> {
                        _loadingState.emit(false)
                        _errorState.emit("")
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

    override fun onCleared() {
        super.onCleared()
        countDownTimer.cancel()
    }
}