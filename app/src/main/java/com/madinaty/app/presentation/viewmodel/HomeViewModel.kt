package com.madinaty.app.presentation.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.madinaty.app.domain.model.Department
import com.madinaty.app.domain.repository.DataStoreRepository
import com.madinaty.app.domain.repository.HomeRepository
import com.madinaty.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: HomeRepository
) : ViewModel() {
    val departments = repo.fetchDepartments().cachedIn(viewModelScope)
}