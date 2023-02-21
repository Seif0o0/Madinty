package com.madinaty.app.utils

object Constants {
    const val PAGE_SIZE = 8
    const val BASE_URL = "https://control.madinty2022.com/api/"

    const val USER_PREFERENCES = "user_preferences"

    const val AUTH_ROOT = "auth/"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "getAuthenticatedUser"
    const val FAVOURITES = "favorite-places"
    const val EDIT_PROFILE = "update-profile"
    const val MY_PLACES = "my-places"
    const val SOCIAL_LOGIN = "/callback"
    const val SEND_CODE = "sendCode"
    const val VERIFY_CODE = "verifyCode"

    const val ADMIN_ROOT = "admin/"
    const val DEPARTMENTS = "departments" /* for department details {url}/{department_id} */
    const val REGIONS = "regions"
    const val OFFERS = "paid-advertisements"
    const val PLACES = "incoming-advertisement-requests"
    const val ADD_PLACE = "incoming-advertisement-requests"

    const val COMMON_INFO = "common-data"

    const val MALE_VALUE = "ذكر"
    const val EDIT_MALE_VALUE = "male"
    const val FEMALE_VALUE = "أنثى"
    const val EDIT_FEMALE_VALUE = "female"

    const val USER_ID_PREF_KEY_NAME = "userId"
    const val TOKEN_PREF_KEY_NAME = "token"
    const val USERNAME_PREF_KEY_NAME = "username"
    const val FIRST_NAME_PREF_KEY_NAME = "firstName"
    const val LAST_NAME_PREF_KEY_NAME = "lastName"
    const val EMAIL_PREF_KEY_NAME = "email"
    const val CITY_PREF_KEY_NAME = "city"
    const val PHONE_NUMBER_PREF_KEY_NAME = "phoneNumber"
    const val GENDER_PREF_KEY_NAME = "gender"
    const val DOB_PREF_KEY_NAME = "dateOfBirth"
    const val IS_APPROVED_PREF_KEY_NAME = "isApproved"
    const val IS_VERIFIED_PREF_KEY_NAME = "isVerified"

}

/*
* Using Hilt, you can inject a run-time parcelable object into a SharedViewModel by using the @Parcelize annotation.

First, create a class that implements the Parcelable interface and annotate it with @Parcelize:

@Parcelize
class MyObject(val name: String): Parcelable

Then, in your SharedViewModel, add an argument for the parcelable object:

class SharedViewModel @ViewModelInject constructor(private val myObject: MyObject) : ViewModel() { ... }

Finally, in your activity or fragment where you are injecting the SharedViewModel, use the Hilt's @AndroidEntryPoint annotation to inject the parcelable object as well:

@AndroidEntryPoint class MyActivity : AppCompatActivity() { private val sharedViewModel: SharedViewModel by viewModels() { InjectorUtils.provideSharedViewModelFactory(myObject) } ... }

Here, InjectorUtils is a utility class which provides a factory method for providing the SharedViewModel with the necessary arguments. The method looks like this:

 fun provideSharedViewModelFactory(myObject: MyObject): ViewModelProvider.Factory { return object : ViewModelProvider.Factory { override fun <T : ViewModel?> create(modelClass: Class<T>): T { if (modelClass.isAssignableFrom(SharedViewModel::class.java)) { return SharedViewModel(myObject) as T } throw IllegalArgumentException("Unknown View Model Class") } } }

 With this setup, you can now inject a run-time parcelable object into your SharedViewModel using Hilt!
*/