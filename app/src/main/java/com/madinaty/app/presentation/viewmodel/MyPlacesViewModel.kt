package com.madinaty.app.presentation.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.domain.model.MyPlace
import com.madinaty.app.domain.repository.MyPlacesRepository
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlacesViewModel @Inject constructor(
    private val repo: MyPlacesRepository
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    private val _myPlaces = MutableStateFlow<List<MyPlace>>(emptyList())
    val myPlaces: StateFlow<List<MyPlace>> get() = _myPlaces

    private val _emptyState = MutableStateFlow(View.GONE)
    val emptyState: StateFlow<Int> get() = _emptyState

    init {
        getMyPlaces()
    }

    fun getMyPlaces() {
        viewModelScope.launch {
            repo.fetchMyPlaces().collectLatest { result ->
                when (result) {
                    is DataState.Success -> {
                        _loadingState.emit(false)
                        _errorState.emit("")
                        val data = result.data!!.toMutableList()
                        if (data.isEmpty()) {
                            _emptyState.emit(View.VISIBLE)
                        } else {
                            _emptyState.emit(View.GONE)
                            _myPlaces.emit(data)
                        }
                    }
                    is DataState.Loading -> {
                        _loadingState.emit(true)
                        _errorState.emit("")
                        _emptyState.emit(View.GONE)
                        _myPlaces.emit(emptyList())
                    }
                    is DataState.Error -> {
                        _loadingState.emit(false)
                        _errorState.emit(result.message!!)
                        _emptyState.emit(View.GONE)
                        _myPlaces.emit(emptyList())
                    }
                }
            }
        }
    }
}