package com.madinaty.app.presentation.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madinaty.app.R
import com.madinaty.app.domain.model.User
import com.madinaty.app.domain.repository.ProfileRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.Constants
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val application: Application,
    private val repo: ProfileRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    var userInfo = state.get<User>("userInfo")!!

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState

    /* check if login request is done successfully */
    private val _updateInfoState = MutableStateFlow(false)
    val updateInfoState: StateFlow<Boolean> get() = _updateInfoState

    fun updateInfoState(value: Boolean) {
        _updateInfoState.value = value
    }

    val firstNameState = MutableLiveData(userInfo.firstName)
    val firstNameErrorState = MutableLiveData("")

    val lastNameState = MutableLiveData(userInfo.lastName)
    val lastNameErrorState = MutableLiveData("")

    val dobState = MutableLiveData(userInfo.dateOfBirth)
    val dobErrorState = MutableLiveData("")

    /* true => male , false => female */
    val genderState = MutableLiveData(userInfo.gender == Constants.MALE_VALUE)

    private val _startUpdate = MutableStateFlow(false)
    val startUpdate: StateFlow<Boolean> get() = _startUpdate
    fun startUpdate(value: Boolean) {
        _startUpdate.value = value
    }

    fun onSaveBtnClicked() {
        var pass = true
        val firstName = firstNameState.value!!
        if (firstName.isEmpty()) {
            firstNameErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }
        val lastName = lastNameState.value!!
        if (lastName.isEmpty()) {
            lastNameErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }

        val dob = dobState.value!!
        if (dob.isEmpty()) {
            dobErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }

        if (pass) {
            Log.d("EditViewModel", "Gender: ${genderState.value}")
            val user = User(
                id = userInfo.id,
                username = userInfo.username,
                firstName = firstName,
                lastName = lastName,
                email = userInfo.email,
                phoneNumber = userInfo.phoneNumber,
                gender = if (genderState.value!!) Constants.MALE_VALUE else Constants.FEMALE_VALUE,
                dateOfBirth = dob,
                city = userInfo.city,
                isApproved = userInfo.isApproved,
                isVerified = userInfo.isVerified
            )

            if (user == userInfo) {
                _errorState.value =
                    application.getString(R.string.no_changes_detected_to_be_updated)
                return
            }

            startUpdate(true)
        }
    }

    fun convertToShortMonth(monthIndex: Int): String {
        return application.resources.getStringArray(R.array.short_months)[monthIndex]
    }

    fun updateUserInfo() {
        viewModelScope.launch {

            val map = mutableMapOf<String, String>()
            val firstName = firstNameState.value!!
            if (firstName != userInfo.firstName)
                map["first_name"] = firstName
            val lastName = lastNameState.value!!
            if (lastName != userInfo.lastName)
                map["last_name"] = lastName
            val gender = if (genderState.value!!) Constants.MALE_VALUE else Constants.FEMALE_VALUE
            if (gender != userInfo.gender)
                map["gender"] =
                    if (gender == Constants.MALE_VALUE) Constants.EDIT_MALE_VALUE else Constants.EDIT_FEMALE_VALUE
            val dob = dobState.value!!
            if (dob != userInfo.dateOfBirth) {
                val splitDob = dob.split("-")

                map["dob"] =
                    "${splitDob[0]}-${
                        application.resources.getStringArray(R.array.short_months)
                            .indexOf(splitDob[1].lowercase()) + 1
                    }-${splitDob[2]}"
            }


            repo.updateProfileInfo(map).collectLatest { result ->
                startUpdate(false)
                when (result) {
                    is DataState.Success -> {
                        _loadingState.emit(false)
                        _errorState.emit("")
                        saveUser(result.data!!)

                    }
                    is DataState.Error -> {
                        _loadingState.emit(false)
                        _errorState.emit(result.message!!)
                    }
                    is DataState.Loading -> {
                        _loadingState.emit(true)
                        _errorState.emit("")
                    }
                }

            }
        }
    }

    private fun saveUser(user: User) {
        userInfo = user.copy()
        UserInfo.firstName = user.firstName
        UserInfo.lastName = user.lastName
        UserInfo.city = user.city ?: ""
        UserInfo.gender = user.gender
        UserInfo.dateOfBirth = user.dateOfBirth
        updateInfoState(true)
    }

}