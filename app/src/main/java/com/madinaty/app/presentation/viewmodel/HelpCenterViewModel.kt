package com.madinaty.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelpCenterViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    /* check if login request is done successfully */
    private val _sendMessageState = MutableStateFlow(false)
    val sendMessageState: StateFlow<Boolean> get() = _sendMessageState

    fun sendMessageState(value: Boolean) {
        _sendMessageState.value = value
    }

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    val subjectState = MutableLiveData("")
    val subjectErrorState = MutableLiveData("")

    val messageState = MutableLiveData("")
    val messageErrorState = MutableLiveData("")

    private val _startSendMessage = MutableStateFlow(false)
    val startSendMessage: StateFlow<Boolean> get() = _startSendMessage
    fun startSendMessage(value: Boolean) {
        _startSendMessage.value = value
    }

    fun onSendBtnClicked() {
        var pass = true
        if (subjectState.value!!.isEmpty()) {
            subjectErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }
        if (messageState.value!!.isEmpty()) {
            messageErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }

        if (pass)
            startSendMessage(true)
    }

    fun sendMessage() {
        viewModelScope.launch {
            _loadingState.emit(true)
            delay(2000)

            _loadingState.emit(false)
            _sendMessageState.emit(true)
        }
    }


}