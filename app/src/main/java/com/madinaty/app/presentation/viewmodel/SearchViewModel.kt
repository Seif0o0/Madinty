package com.madinaty.app.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.madinaty.app.domain.model.Place
import com.madinaty.app.domain.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: PlacesRepository,
) : ViewModel() {
    val query = MutableLiveData("")
    val places: Flow<PagingData<Place>> = query.asFlow().flatMapLatest { queryString ->
        repo.fetchPlaces(query = queryString).cachedIn(viewModelScope)
    }
}