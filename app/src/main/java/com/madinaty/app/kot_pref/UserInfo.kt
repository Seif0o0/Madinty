package com.madinaty.app.kot_pref

import com.chibatching.kotpref.KotprefModel
import com.madinaty.app.R

object UserInfo : KotprefModel() {
    var userId by stringPref("")
    var token by stringPref("")
    var username by stringPref("")
    var firstName by stringPref("")
    var lastName by stringPref("")
    var email by stringPref("")
    var city by stringPref("")
    var phoneNumber by stringPref("")
    var gender by stringPref("")
    var dateOfBirth by stringPref("")
    var isApproved by booleanPref(false)
    var isVerified by booleanPref(false)

    var isFirstTime by booleanPref(true) /* for language & intro screens */
    var appLanguage by stringPref(context.getString(R.string.english_value))
}