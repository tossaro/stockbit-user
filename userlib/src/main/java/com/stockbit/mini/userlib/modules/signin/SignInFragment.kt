package com.stockbit.mini.userlib.modules.signin

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.stockbit.mini.corelib.base.StockbitActivity
import com.stockbit.mini.corelib.base.StockbitViewModelFragment
import com.stockbit.mini.userlib.R
import com.stockbit.mini.userlib.databinding.SignInFragmentBinding
import com.stockbit.mini.userlib.repositories.cache.storage.entity.User
import timber.log.Timber

class SignInFragment : StockbitViewModelFragment<SignInFragmentBinding, SignInViewModel>(
    layoutResId = R.layout.sign_in_fragment,
    SignInViewModel::class
) {

    var doubleBackToExitPressedOnce = false

    override fun actionBarTitle(): String = getString(R.string.sign_in_title)

    override fun initBinding(binding: SignInFragmentBinding, viewModel: SignInViewModel) {
        super.initBinding(binding, viewModel)
        binding.viewModel = viewModel.also {
            it.loadingIndicator.observe(this, ::loadingIndicator)
            it.alertMessage.observe(this, ::showAlert)
            it.user.observe(this, ::isSignedIn)
        }
        activity?.let {
            it.application?.let { app ->
                if (BiometricManager.from(app).canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) binding.btnFingerPrint.isEnabled = false
            }
        }
    }

    private fun isSignedIn(user: User?) {
        user?.let { _ ->
            view?.let { v ->
                val request = NavDeepLinkRequest.Builder
                    .fromUri("android-app://mini.stockbit.com/stock".toUri())
                    .build()
                (activity as? StockbitActivity)?.navHostFragment()?.findNavController()?.navigate(request)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_signin, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_call_cs) {
            AlertDialog.Builder(context)
                .setMessage(getString(R.string.sign_in_call_cs_sure))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.sign_in_call_cs_yes) { _, _ ->
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.sign_in_call_number)))
                    startActivity(intent)
                }
                .setNegativeButton(R.string.sign_in_call_cs_no, null).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == viewModel.RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                viewModel.onGetAccessToken("google", account.idToken.toString())
            } catch (e: ApiException) {
                Timber.e("signInResult:failed code=" + e.statusCode)
            }
        } else {
            viewModel.callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            activity?.finish()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(context, getString(R.string.sign_in_tap_to_minimize), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

}