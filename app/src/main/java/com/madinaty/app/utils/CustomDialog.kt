package com.madinaty.app.utils

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavGraph
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable
import com.madinaty.app.R
import com.madinaty.app.databinding.AddPlaceSuccessDialogLayoutBinding
import com.madinaty.app.databinding.ErrorDialogLayoutBinding
import com.madinaty.app.databinding.LogoutDialogLayoutBinding
import com.madinaty.app.databinding.SuccessDialogLayoutBinding
import com.madinaty.app.presentation.activity.AuthActivity


object CustomDialog {
    fun showSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        context: Context,
        successMessage: String,
    ) {
        val dialog = MaterialDialog(context, dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelable(false)
            customView(
                R.layout.success_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = SuccessDialogLayoutBinding.bind(dialog.getCustomView())
        binding.successMessage.text = successMessage

        binding.errorAnimation.setAnimation("success.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.okButton.setOnClickListener {
            binding.errorAnimation.cancelAnimation()
            dialog.dismiss()
        }

    }

    fun showErrorDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        context: Context,
        errorMessage: String
    ) {
        val dialog = MaterialDialog(context, dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelable(false)
            customView(
                R.layout.error_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = ErrorDialogLayoutBinding.bind(dialog.getCustomView())
        binding.errorMessage.text = errorMessage

        binding.errorAnimation.setAnimation(if (errorMessage == context.getString(R.string.no_internet_connection)) "no_internet_connection.json" else "error_dialog_animation.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.dismissText.setOnClickListener {
            binding.errorAnimation.cancelAnimation()
            dialog.dismiss()
        }
    }


    fun showLogoutDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        context: Context,
        onYesBtnClicked: () -> Unit
    ) {
        val dialog = MaterialDialog(context, dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            cancelable(false)
            customView(
                R.layout.logout_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val dialogBinding = LogoutDialogLayoutBinding.bind(dialog.getCustomView())

        dialogBinding.yesBtn.setOnClickListener {
            dialog.dismiss()
            onYesBtnClicked()
        }



        dialogBinding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun showAddPlaceSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        context: Context,
        onOkBtnClicked: () -> Unit
    ) {
        val dialog = MaterialDialog(context, dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(false)
            cancelable(false)

            customView(
                R.layout.add_place_success_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = AddPlaceSuccessDialogLayoutBinding.bind(dialog.getCustomView())

        binding.okBtn.setOnClickListener {
            dialog.dismiss()
            onOkBtnClicked()
        }
    }
}