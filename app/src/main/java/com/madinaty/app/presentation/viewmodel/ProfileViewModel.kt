package com.madinaty.app.presentation.viewmodel

import android.util.Log
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.domain.model.User
import com.madinaty.app.domain.repository.DataStoreRepository
import com.madinaty.app.domain.repository.ProfileRepository
import com.madinaty.app.utils.Constants
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository,
    private val dataStoreRepo: DataStoreRepository,
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> get() = _userInfo

    fun updateUserInfo(user: User){
        _userInfo.value = user
    }

    init {
        getUserInfo()
    }

    fun getUserInfo() {
        viewModelScope.launch {
            repo.fetchProfileInfo().collectLatest { result ->
                when (result) {
                    is DataState.Success -> {
                        _loadingState.emit(false)
                        _errorState.emit("")
                        _userInfo.emit(result.data)
                    }
                    is DataState.Error -> {
                        _loadingState.emit(false)
                        _errorState.emit(result.message!!)
                        _userInfo.emit(null)
                    }
                    is DataState.Loading -> {
                        _loadingState.emit(true)
                        _errorState.emit("")
                        _userInfo.emit(null)
                    }
                }
            }
        }
    }
}