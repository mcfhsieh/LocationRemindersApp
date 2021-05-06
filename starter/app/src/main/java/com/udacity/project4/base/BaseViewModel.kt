package com.udacity.project4.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseUser
//import com.udacity.project4.authentication.FirebaseUserLiveData
import com.udacity.project4.utils.SingleLiveEvent

/**
 * Base class for View Models to declare the common LiveData objects in one place
 */

enum class AuthenticationState{
    AUTHENTICATED, UNAUTHENTICATED
}
abstract class BaseViewModel(app: Application) : AndroidViewModel(app) {

    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showNoData: MutableLiveData<Boolean> = MutableLiveData()
//    val userStatus = FirebaseUserLiveData()
//    val authenticationState = FirebaseUserLiveData().map { user ->
//        if (user != null){
//            AuthenticationState.AUTHENTICATED
//        }else AuthenticationState.UNAUTHENTICATED
//    }

}