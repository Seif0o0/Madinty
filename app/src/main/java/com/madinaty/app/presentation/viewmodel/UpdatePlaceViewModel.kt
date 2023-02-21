package com.madinaty.app.presentation.viewmodel

import android.app.Activity
import android.app.Application
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.drjacky.imagepicker.ImagePicker
import com.madinaty.app.R
import com.madinaty.app.domain.model.MyPlace
import com.madinaty.app.domain.model.Region
import com.madinaty.app.domain.repository.UpdatePlaceRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URI
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class UpdatePlaceViewModel @Inject constructor(
    private val application: Application,
    private val repo: UpdatePlaceRepository,
) : ViewModel() {

    lateinit var place: MyPlace
    fun setMyPlace(place: MyPlace) {
        this.place = place
        nameState.value = place.name
        phoneNumberState.value = place.phoneNumber ?: ""
        whatsAppState.value = place.whatsAppNumber ?: ""
        emailState.value = place.email ?: ""
        addressState.value = place.address
        locationState.value = "${place.latitude},${place.longitude}"
        workingHoursState.value = "${place.startTime} - ${place.endTime}"
        facebookState.value = place.facebookUrl ?: ""
        descriptionState.value = place.description
        region = place.region
        departmentId = place.departmentId
        if (images.value.isNotEmpty())
            images.value = mutableListOf(Uri.parse("header"))
        for (attachment in place.attachments) {
            images.value.add(Uri.parse(attachment.url))
        }
    }

    fun deleteAllData() {
        nameState.value = ""
        phoneNumberState.value = ""
        whatsAppState.value = ""
        emailState.value = ""
        addressState.value = ""
        locationState.value = ""
        workingHoursState.value = ""
        facebookState.value = ""
        descriptionState.value = ""
        region = Region("", "")
        departmentId = ""
        images.value = mutableListOf(Uri.parse("header"))
    }

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    /* check if add place request is done successfully */
    private val _updatePlaceState = MutableStateFlow(false)
    val updatePlaceState: StateFlow<Boolean> get() = _updatePlaceState

    fun updatePlaceState(value: Boolean) {
        _updatePlaceState.value = value
    }

    private val _goToNext = MutableStateFlow(false)
    val goToNext: StateFlow<Boolean> get() = _goToNext

    fun goToNext(value: Boolean) {
        _goToNext.value = value
    }

    private val _startUpdatingPlace = MutableStateFlow(false)
    val startUpdatingPlace: StateFlow<Boolean> get() = _startUpdatingPlace

    fun startUpdatingPlace(value: Boolean) {
        _startUpdatingPlace.value = value
    }

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> get() = _errorState
    fun setErrorStateValue(value: String? = null) {
        _errorState.value = value ?: ""
    }

    var region: Region = Region("", "")

    val nameState = MutableLiveData("")
    val nameErrorState = MutableLiveData("")

    val phoneNumberState = MutableLiveData("")
    val phoneNumberErrorState = MutableLiveData("")

    val whatsAppState = MutableLiveData("")
    val whatsAppErrorState = MutableLiveData("")

    val emailState = MutableLiveData("")
    val emailErrorState = MutableLiveData("")

    val addressState = MutableLiveData("")
    val addressErrorState = MutableLiveData("")

    val locationState = MutableLiveData("")
    val locationErrorState = MutableLiveData(View.GONE)

    val workingHoursState = MutableLiveData("")
    val workingHoursErrorState = MutableLiveData(View.GONE)

    val facebookState = MutableLiveData("")
    val facebookErrorState = MutableLiveData("")

    val descriptionState = MutableLiveData("")
    val descriptionErrorState = MutableLiveData("")

    val images = MutableStateFlow(mutableListOf(Uri.parse("header")))


    fun onNextBtnClicked() {
        var pass = true

        if (nameState.value!!.isEmpty()) {
            nameErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }

        if (phoneNumberState.value!!.isEmpty()) {
            phoneNumberErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        } else {
            if (phoneNumberState.value!!.length < 11) {
                phoneNumberErrorState.value =
                    application.getString(R.string.invalid_mobile_error_message)
                pass = false
            }
        }

        if (whatsAppState.value!!.isEmpty()) {
            whatsAppErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        } else {
            if (whatsAppState.value!!.length < 11) {
                whatsAppErrorState.value =
                    application.getString(R.string.invalid_mobile_error_message)
                pass = false
            }
        }

        if (emailState.value!!.isEmpty()) {
            emailErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        } else {
            if (!isEmailValid(email = emailState.value!!)) {
                emailErrorState.value =
                    application.getString(R.string.email_validation_error_message)
                pass = false
            }
        }

        if (addressState.value!!.isEmpty()) {
            addressErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }
        if (locationState.value!!.isEmpty()) {
            locationErrorState.value = View.VISIBLE
            pass = false
        }
        if (workingHoursState.value!!.isEmpty()) {
            workingHoursErrorState.value = View.VISIBLE
            pass = false
        }
        if (facebookState.value!!.isEmpty()) {
            facebookErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }
        if (descriptionState.value!!.isEmpty()) {
            descriptionErrorState.value = application.getString(R.string.empty_field_error_message)
            pass = false
        }

        if (pass) goToNext(true)
    }

    var departmentId = ""
    fun updatePlace() {
        val map = mutableMapOf<String, RequestBody>()
        map["name"] = createPartFromString(nameState.value!!)
        map["email"] = createPartFromString(emailState.value!!)
        map["address"] = createPartFromString(addressState.value!!)
        val splitLocation = locationState.value!!.split(",")
        map["lat"] = createPartFromString(splitLocation[0])
        map["lng"] = createPartFromString(splitLocation[1])
        val splitWorkingHours = workingHoursState.value!!.split("-")
        map["work_from"] = createPartFromString(splitWorkingHours[0].trim())
        map["work_to"] = createPartFromString(splitWorkingHours[1].trim())
        map["description"] = createPartFromString(descriptionState.value!!)
        map["user_id"] = createPartFromString(UserInfo.userId)
        map["region_id"] = createPartFromString(region.id)
        map["department_id"] = createPartFromString(departmentId)
        map["mobile"] = createPartFromString(phoneNumberState.value!!)
        map["whats_number"] = createPartFromString(whatsAppState.value!!)
        map["facebook_url"] = createPartFromString(facebookState.value!!)


        val imagesMap = mutableListOf<MultipartBody.Part>()
        var i = 0
        images.value.forEachIndexed { index, uri ->
            if (index != 0) {
                if (uri.toString().startsWith("https://")) {
                    map["images[$i]"] = createPartFromString(uri.toString())
                } else {
                    val file = File(URI(uri.toString()))
                    val fileRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    imagesMap.add(
                        MultipartBody.Part.createFormData(
                            name = "images[$i]", file.name, fileRequestBody
                        )
                    )
                }
                i++
            }
        }

        viewModelScope.launch {
            repo.editPlace(
                placeId = place.id, map = map, images = if (imagesMap.isEmpty()) null else imagesMap
            ).collectLatest { result ->
                startUpdatingPlace(false)
                when (result) {
                    is DataState.Success -> {
                        _loadingState.value = false
                        _errorState.emit("")
                        _updatePlaceState.emit(true)
                    }
                    is DataState.Error -> {
                        _loadingState.value = false
                        _errorState.emit(result.message!!)
                    }
                    is DataState.Loading -> {
                        _loadingState.value = true
                        _errorState.emit("")
                    }
                }
            }
        }
    }

    private fun createPartFromString(data: String): RequestBody {
        return data.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun isEmailValid(email: String): Boolean {
        val regExpression =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        val pattern = Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE)

        return pattern.matcher(email).matches()
    }

    fun validateImages() {
        if (images.value.size == 1) {
            _errorState.value = application.getString(R.string.no_photos_error_message)
            return
        }

        startUpdatingPlace(true)
    }

    fun handlePickingImage(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            if (activityResult.data?.hasExtra(ImagePicker.EXTRA_FILE_PATH)!!) {
                if (images.value.size == 12) {
                    _errorState.value = application.getString(R.string.photos_count_limit)
                } else {
                    _errorState.value = ""
                    val uri = activityResult.data?.data!!
                    val values = mutableListOf(uri)
                    images.value.forEach { pickedUriBefore ->
                        if (pickedUriBefore.toString() != "header") values.add(0, pickedUriBefore)
                    }

                    values.add(0, Uri.parse("header"))
                    images.value = values
                }

            } else if (activityResult.data?.hasExtra(ImagePicker.MULTIPLE_FILES_PATH)!!) {
                val uris = ImagePicker.getAllFile(activityResult.data) as MutableList<Uri>
                if (images.value.size + uris.size > 12) {
                    _errorState.value = application.getString(R.string.photos_count_limit)
                } else {
                    _errorState.value = ""
                    images.value.forEach { pickedUriBefore ->
                        if (pickedUriBefore.toString() != "header") uris.add(0, pickedUriBefore)
                    }

                    uris.add(0, Uri.parse("header"))
                    images.value = uris
                }

            }
        } else {

        }
    }
}