package com.madinaty.app.utils

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.madinaty.app.R
import com.madinaty.app.domain.model.Attachment
import com.madinaty.app.domain.model.DepartmentChild
import com.madinaty.app.kot_pref.UserInfo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun logUserOut() {
    UserInfo.userId = ""
    UserInfo.token = ""
    UserInfo.username = ""
    UserInfo.firstName = ""
    UserInfo.lastName = ""
    UserInfo.email = ""
    UserInfo.phoneNumber = ""
    UserInfo.gender = ""
    UserInfo.dateOfBirth = ""
    UserInfo.isApproved = false
    UserInfo.isVerified = false
}

@BindingAdapter("image_url")
fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.mipmap.splash_logo)
        .error(R.mipmap.splash_logo)
        .into(this)
}

@BindingAdapter("attachment")
fun ImageView.loadImage(attachment: Attachment?) {
    attachment?.let {
        Glide.with(this)
            .load(it.url)
            .placeholder(R.mipmap.splash_logo)
            .error(R.mipmap.splash_logo)
            .into(this)
    }
}


@BindingAdapter("is_arabic")
fun MaterialButton.setLangButtonsColors(isArabic: Boolean) {
    if ((UserInfo.appLanguage == context.getString(R.string.arabic_value) && isArabic) ||
        (UserInfo.appLanguage == context.getString(R.string.english_value) && !isArabic)
    ) {
        backgroundTintList =
            ContextCompat.getColorStateList(context, R.color.language_screen_main_buttons_color)
        icon = ContextCompat.getDrawable(context, R.drawable.ic_right_mark)
        iconTint = ContextCompat.getColorStateList(context, R.color.language_screen_main_text_color)
        strokeColor = ContextCompat.getColorStateList(context, android.R.color.transparent)
    } else {
        backgroundTintList =
            ContextCompat.getColorStateList(context, android.R.color.transparent)
        icon = ContextCompat.getDrawable(context, R.drawable.ic_right_mark)
        iconTint = ContextCompat.getColorStateList(context, android.R.color.transparent)
        strokeColor =
            ContextCompat.getColorStateList(context, R.color.language_screen_main_text_color)
    }
}

@BindingAdapter("place_first_photo")
fun ImageView.loadFirstAttachment(attachments: List<Attachment>) {
    val url = if (attachments.isEmpty()) "" else attachments[0].url
    loadImage(url)
}

@BindingAdapter("error_message")
fun EditText.setErrorMessage(errorMessage: String) {
    if (errorMessage.isNotEmpty()) error = errorMessage
}

@BindingAdapter("is_favourite")
fun ImageView.setFavIcon(isFavourite: Boolean) {
    setImageResource(if (isFavourite) R.drawable.ic_full_heart_icon else R.drawable.ic_heart_icon)
}

@BindingAdapter("gender")
fun TextView.setGender(gender: String?) {
    text = if (gender.isNullOrEmpty() || gender == Constants.MALE_VALUE)
        context.getString(R.string.male)
    else
        context.getString(R.string.female)
}


@BindingAdapter("birth_date")
fun EditText.setDateOfBirth(dateOfBirth: String) {
    if (dateOfBirth.isEmpty()) {
        setText("")
        return
    }

    val splitDate = dateOfBirth.split("-")
    val month = resources.getStringArray(R.array.short_months).indexOf(splitDate[1].lowercase()) + 1

    val dob = "${splitDate[0]}-${if (month < 10) "0$month" else month}-${splitDate[2]}"
    setText(dob)
}

@BindingAdapter("birth_date_error")
fun TextView.setError(errorMessage: String) {
    if (errorMessage.isEmpty()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        text = errorMessage
    }
}

@BindingAdapter("loading_status")
fun LottieAnimationView.setLoadingStatus(isLoading: Boolean) {
    if (isLoading) {
        visibility = View.VISIBLE
        setAnimation("progress_bar.json")
        playAnimation()
        repeatCount = LottieDrawable.INFINITE
    } else {
        visibility = View.GONE
        cancelAnimation()
    }
}

@BindingAdapter("loading_status")
fun FrameLayout.setLoadingStatus(isLoading: Boolean) {
    visibility = if (isLoading) View.VISIBLE
    else View.GONE
}

@BindingAdapter("error_status", "error_message")
fun LottieAnimationView.setErrorStatus(isError: Boolean, errorMessage: String) {
    if (isError) {
        setAnimation(if (errorMessage == context.getString(R.string.no_internet_connection)) "no_internet_connection.json" else "error_dialog_animation.json")
        playAnimation()
        repeatCount = LottieDrawable.INFINITE
    } else {
        cancelAnimation()
    }
}

@BindingAdapter("error_status")
fun ConstraintLayout.setErrorStatus(isError: Boolean) {
    visibility = if (isError) View.VISIBLE
    else View.GONE
}

@BindingAdapter("start_time", "end_time")
fun TextView.setTime(startTime: String, endTime: String) {
    val startSplit = startTime.split(":")
    val endSplit = endTime.split(":")
    text = try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val finalSdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val startDate = sdf.parse("${startSplit[0]}:${startSplit[1]}")
        val endDate = sdf.parse("${endSplit[0]}:${endSplit[1]}")
        "${finalSdf.format(startDate!!)} - ${finalSdf.format(endDate!!)}"
    } catch (e: ParseException) {
        "$startTime - $endTime"
    }
}


@BindingAdapter("reference")
fun ImageView.setVisibility(reference: String?) {
    visibility = if (reference.isNullOrEmpty()) View.GONE
    else View.VISIBLE
}

@BindingAdapter("loading_status")
fun CardView.setElevation(isLoading: Boolean) {
    elevation = if (isLoading) 0f
    else 10f
}

@BindingAdapter("childs")
fun TextView.concatChildsName(childs: List<DepartmentChild>) {
    var rslt = ""
    childs.forEachIndexed() { index, child ->
        rslt += child.name
        if (index != childs.size - 1) rslt += ", "
    }

    text = rslt
}

@BindingAdapter("error_message")
fun TextView.setErrorMessage(errorMessage: String) {
    if (errorMessage.isEmpty()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        text = errorMessage
    }
}

@BindingAdapter("resend_code_timer")
fun TextView.setResendCodeClickable(timer: String) {
    isClickable = timer.isEmpty()
}

@BindingAdapter("my_place_status")
fun ImageView.setMyPlaceStatus(status: Boolean) {
    if (status)
        setImageResource(R.drawable.approved_icon)
    else
        setImageResource(R.drawable.pending_icon)
}

@BindingAdapter("my_place_status")
fun MaterialButton.setEditBtnVisibility(status: Boolean) {
    visibility = if (status) View.VISIBLE else View.INVISIBLE
}