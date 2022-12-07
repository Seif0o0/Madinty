package com.madinaty.app.presentation.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.domain.model.Offer
import com.madinaty.app.domain.repository.PinOffersRepository
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinOffersViewModel @Inject constructor(
    private val repo: PinOffersRepository
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    private val _pinOffers = MutableStateFlow<List<Offer>>(emptyList())
    val pinOffers: StateFlow<List<Offer>> get() = _pinOffers

    /*
     *  if list is empty View.Gone -hide viewPager & indicator-
     *  else View.VISIBLE -show viewPager & indicator-
     */
    private val _emptyState = MutableStateFlow(View.GONE)
    val emptyState: StateFlow<Int> get() = _emptyState

    init {
        getOffers()
    }

    fun getOffers() {
        viewModelScope.launch {
            repo.fetchPinOffers().collectLatest { result ->
                when (result) {
                    is DataState.Success -> {
                        _loadingState.emit(false)
                        _errorState.emit("")
                        val data = result.data!!.toMutableList()
                        if (data.isEmpty())
                            _emptyState.emit(View.GONE)
                        else {
                            _emptyState.emit(View.VISIBLE)
                            _pinOffers.emit(result.data.toMutableList())
                        }
                    }
                    is DataState.Error -> {
                        _loadingState.emit(false)
                        _errorState.emit(result.message!!)
                        _emptyState.emit(View.INVISIBLE)
                        _pinOffers.emit(emptyList())
                    }
                    is DataState.Loading -> {
                        _loadingState.emit(true)
                        _errorState.emit("")
                        _emptyState.emit(View.INVISIBLE)
                        _pinOffers.emit(emptyList())
                    }
                }
            }
        }
    }
}