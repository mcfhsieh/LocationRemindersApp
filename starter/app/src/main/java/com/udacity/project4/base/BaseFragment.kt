package com.udacity.project4.base

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.authentication.FirebaseUserLiveData
import com.udacity.project4.utils.wrapEspressoIdlingResource
import com.google.android.gms.tasks.OnCompleteListener as OnCompleteListener1

/**
 * Base Fragment to observe on the common LiveData objects
 */
abstract class BaseFragment : Fragment() {
    /**
     * Every fragment has to have an instance of a view model that extends from the BaseViewModel
     */
    abstract val _viewModel: BaseViewModel
    private lateinit var authenticationState: LiveData<AuthenticationState>

    override fun onStart() {
        super.onStart()

//        checkAuthenticationState()

//        authenticationState.observe(this, Observer { authenticationState ->
//            when (authenticationState) {
//                AuthenticationState.AUTHENTICATED -> {
//                    println("user active")
//                }
//                AuthenticationState.UNAUTHENTICATED -> {
//                    loginUser()
//                }
//            }
//        })

        _viewModel.showErrorMessage.observe(this, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })

        _viewModel.showToast.observe(this, Observer {
                Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        _viewModel.showSnackBar.observe(this, Observer {
            Snackbar.make(this.view!!, it, Snackbar.LENGTH_LONG).show()
        })
        _viewModel.showSnackBarInt.observe(this, Observer {
            Snackbar.make(this.view!!, getString(it), Snackbar.LENGTH_LONG).show()
        })

        _viewModel.navigationCommand.observe(this, Observer { command ->
            when (command) {
                is NavigationCommand.To -> findNavController().navigate(command.directions)
                is NavigationCommand.Back -> findNavController().popBackStack()
                is NavigationCommand.BackTo -> findNavController().popBackStack(
                    command.destinationId,
                    false
                )
            }
        })
    }

    private fun checkAuthenticationState() {

        authenticationState = FirebaseUserLiveData().map { user ->
            if (user != null) {
                AuthenticationState.AUTHENTICATED
            } else AuthenticationState.UNAUTHENTICATED
        }

    }

    private fun loginUser() {
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        startActivity(intent)
    }
}