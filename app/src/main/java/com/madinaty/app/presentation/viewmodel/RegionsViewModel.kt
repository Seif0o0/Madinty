package com.madinaty.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.madinaty.app.domain.model.Region
import com.madinaty.app.domain.repository.RegionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RegionsViewModel @Inject constructor(
    private val repo: RegionsRepository
) : ViewModel() {
    val regions: Flow<PagingData<Region>> = repo.fetchRegions().cachedIn(viewModelScope)
}