package com.madinaty.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.madinaty.app.domain.model.Place
import com.madinaty.app.domain.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val repo: PlacesRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    val places: Flow<PagingData<Place>> =
        repo.fetchPlaces(state.get<String>("id")!!).cachedIn(viewModelScope)
}