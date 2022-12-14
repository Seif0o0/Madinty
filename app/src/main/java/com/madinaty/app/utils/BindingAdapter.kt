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
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
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
    UserInfo.city = ""
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