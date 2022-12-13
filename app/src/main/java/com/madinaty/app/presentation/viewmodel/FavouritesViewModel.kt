package com.madinaty.app.presentation.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.domain.model.Favourite
import com.madinaty.app.domain.repository.FavouritesRepository
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repo: FavouritesRepository,
) : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    private val _favourites = MutableStateFlow<List<Favourite>>(emptyList())
    val favourites: StateFlow<List<Favourite>> get() = _favourites

    private val _emptyState = MutableStateFlow(View.GONE)
    val emptyState: StateFlow<Int> get() = _emptyState

    init {
        getFavourites()
    }

    fun getFavourites() {
        viewModelScope.launch {
            repo.fetchFavourites().collectLatest { result ->
                when (result) {
                    is DataState.Success -> {
                        _loadingState.emit(false)
                        _errorState.emit("")
                        val data = result.data!!.toMutableList()
                        if (data.isEmpty()) {
                            _emptyState.emit(View.VISIBLE)
                        } else {
                            _emptyState.emit(View.GONE)
                            _favourites.emit(data)
                        }
                    }
                    is DataState.Error -> {
                        _loadingState.emit(false)
                        _errorState.emit(result.message!!)
                        _emptyState.emit(View.GONE)
                        _favourites.emit(emptyList())
                    }
                    is DataState.Loading -> {
                        _loadingState.emit(true)
                        _errorState.emit("")
                        _emptyState.emit(View.GONE)
                        _favourites.emit(emptyList())
                    }
                }
            }
        }
    }
}