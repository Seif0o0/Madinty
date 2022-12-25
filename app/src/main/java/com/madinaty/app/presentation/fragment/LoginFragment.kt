package com.madinaty.app.presentation.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.dynamic.FragmentWrapper
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentLoginBinding
import com.madinaty.app.databinding.LoginSuccessDialogLayoutBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.viewmodel.CommonInfoViewModel
import com.madinaty.app.presentation.viewmodel.SocialLoginViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: SocialLoginViewModel by viewModels()
    private val commonInfoViewModel: CommonInfoViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.commonViewModel = commonInfoViewModel
        binding.lifecycleOwner = requireActivity()

        /* fb-login */
        binding.facebookBackground.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, callbackManager, listOf("email", "public_profile"))
        }

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    viewModel.accessToken = result.accessToken.token
                    viewModel.provider = "facebook"
                    viewModel.startLogging(true)
                }

                override fun onCancel() {}

                override fun onError(error: FacebookException) {
                    Log.d("LoginFragment", "Error: ${error.message}")
                }

            })

        /* fb-login */

        /* Google-login */
        requestGoogleSignIn()
        val googleLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    viewModel.accessToken = account.idToken
                    viewModel.provider = "google"
                    viewModel.startLogging(true)
                } catch (e: ApiException) {
                    Log.d("LoginFragment", "Google Login error: $e")
                }
            }
        binding.googleBackground.setOnClickListener {
            signWithGoogle(googleLoginLauncher)
        }
        /* Google-login */

        lifecycleScope.launchWhenStarted {
            viewModel.startLogging.collectLatest {
                if (it) {
                    viewModel.socialLogin()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.errorState.collectLatest {
                if (it.isNotEmpty()) {
                    logoutSocial()
                    CustomDialog.showErrorDialog(
                        context = requireContext(), errorMessage = it
                    )
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collectLatest {
                if (it) {
                    showSuccessDialog()
                    logoutSocial()
                    viewModel.loginState(false)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            commonInfoViewModel.commonInfo.collectLatest {
                binding.phoneNumber.text = it?.phoneNumber
                binding.whatsNumber.text = it?.whatsAppNumber
            }
        }

        lifecycleScope.launchWhenStarted {
            commonInfoViewModel.errorState.collectLatest {
                if (it.isNotEmpty()) {
                    binding.closeMenu.visibility = View.GONE
                    binding.hiddenFloatingButton.visibility = View.GONE
                    binding.contactUs.visibility = View.GONE
                }
            }
        }
        return binding.root
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private fun requestGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestServerAuthCode(getString(R.string.client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }

    private fun signWithGoogle(googleLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        googleLauncher.launch(signInIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.register.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }
        binding.contactUs.setOnClickListener {
            binding.hiddenFloatingButton.visibility = View.VISIBLE
            binding.contactUs.visibility = View.GONE
            binding.whatsFloatingButton.requestFocus()
        }

        binding.termsConditions.setOnClickListener {
            val termsConditionsDialog = TermsConditionsDialogFragment.newInstance()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val prevFragment =
                requireActivity().supportFragmentManager.findFragmentByTag("TermsConditionsDialog")
            if (prevFragment != null) fragmentTransaction.remove(prevFragment)
            fragmentTransaction.addToBackStack(null)
            termsConditionsDialog.show(fragmentTransaction, "TermsConditionsDialog")
        }

        binding.closeMenu.setOnClickListener {
            binding.hiddenFloatingButton.visibility = View.GONE
            binding.contactUs.visibility = View.VISIBLE
        }

        binding.whatsCard.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://wa.me/${binding.whatsNumber.text}")
                )
            )
        }

        binding.callCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${binding.phoneNumber.text}")
            startActivity(intent)
        }
        binding.phoneBackground.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToPhoneLoginFragment()
            )
        }
    }

    private fun logoutSocial() {
        if (viewModel.provider == "facebook") {
            val accessToken = AccessToken.getCurrentAccessToken()
            accessToken?.let {
                GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/permissions/",
                    null,
                    HttpMethod.DELETE,
                    {
                        AccessToken.setCurrentAccessToken(null)
                        LoginManager.getInstance().logOut()
                    }
                ).executeAsync()
            }
        } else {
            if (::googleSignInClient.isInitialized)
                googleSignInClient.signOut()
        }
    }

    private fun showSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
    ) {
        val dialog = MaterialDialog(requireContext(), dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelable(false)
            customView(
                R.layout.login_success_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val dialogBinding = LoginSuccessDialogLayoutBinding.bind(dialog.getCustomView())
        dialogBinding.successAnimation.setAnimation("success.json")
        dialogBinding.successAnimation.playAnimation()

        dialogBinding.thanksBtn.setOnClickListener {
            dialogBinding.successAnimation.cancelAnimation()
            dialog.dismiss()
            /* comment till wessam setup otp */
//                    findNavController().navigate(
//                        PhoneLoginFragmentDirections.actionPhoneLoginFragmentToVerificationFragment()
//                    )
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
}