package com.madinaty.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.madinaty.app.domain.model.Offer
import com.madinaty.app.domain.repository.OffersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class OffersViewModel @Inject constructor(
    private val repo:OffersRepository
):ViewModel(){
    val offers:Flow<PagingData<Offer>> = repo.fetchOffers().cachedIn(viewModelScope)
}