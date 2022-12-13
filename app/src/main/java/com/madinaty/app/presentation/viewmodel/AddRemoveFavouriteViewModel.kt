package com.madinaty.app.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.domain.repository.AddRemoveFavouriteRepository
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRemoveFavouriteViewModel @Inject constructor(
    private val application: Application,
    private val repo: AddRemoveFavouriteRepository
) : ViewModel() {
    /* check if add place request is done successfully */
    private val _addRemoveFavouriteState = MutableStateFlow(false)
    val addRemoveFavouriteState: StateFlow<Boolean> get() = _addRemoveFavouriteState

    fun addRemoveFavouriteState(value: Boolean) {
        _addRemoveFavouriteState.value = value
    }

    private val _startAddRemoveFavouriteState = MutableStateFlow(false)
    val startAddRemoveFavouriteState: StateFlow<Boolean> get() = _startAddRemoveFavouriteState

    var placeId: String? = null
    fun startAddRemoveFavouriteState(value: Boolean, placeId: String?=null) {
        this.placeId = placeId
        _startAddRemoveFavouriteState.value = value

    }

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    fun addRemoveFavourite() {
        viewModelScope.launch {
            repo.addRemoveFavourite(placeId = placeId!!).collectLatest { result ->
                when (result) {
                    is DataState.Success -> {
                        _errorState.emit("")
                        _addRemoveFavouriteState.emit(true)
                    }
                    is DataState.Error -> {
                        _errorState.emit(result.message!!)
                        startAddRemoveFavouriteState(false)
                    }
                    is DataState.Loading -> {
                        /* DO NOTHING */
                    }
                }
            }
        }
    }
}