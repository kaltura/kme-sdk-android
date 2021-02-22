package com.kme.kaltura.kmeapplication.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.rest.response.signin.KmeGuestLoginResponse.KmeGuestLoginData
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse.KmeLoginData

class SignInViewModel(
        private val kmeSdk: KME,
        private val context: Context
) : ViewModel() {

    private val isLoading = MutableLiveData<Boolean>()
    val isLoadingLiveData get() = isLoading as LiveData<Boolean>

    private val loginResponse = MutableLiveData<KmeLoginData>()
    val loginResponseLiveData get() = loginResponse as LiveData<KmeLoginData>

    private val guestLoginResponse = MutableLiveData<KmeGuestLoginData>()
    val guestLoginResponseLiveData get() = guestLoginResponse as LiveData<KmeGuestLoginData>

    private val loginError = MutableLiveData<String?>()
    val loginErrorLiveData get() = loginError as LiveData<String?>

    fun login(email: String, password: String) {
        if (!validateLogin(email, password)) {
            return
        }
        isLoading.value = true
        kmeSdk.signInController.login(email, password, success = {
            isLoading.value = false
            loginResponse.value = it.data
        }, error = {
            isLoading.value = false
            loginError.value = it.message
        })
    }

    fun guest(name: String, email: String, roomAlias: String) {
        if (!validateGuestLogin(name, email)) {
            return
        }
        isLoading.value = true
        kmeSdk.signInController.guest(name, email, roomAlias, success = {
            isLoading.value = false
            guestLoginResponse.value = it.data
        }, error = {
            isLoading.value = false
            loginError.value = it.message
        })
    }

    private fun validateLogin(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            loginError.value = context.getString(R.string.empty_email)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginError.value = context.getString(R.string.incorrect_email)
            return false
        }

        if (password.isEmpty()) {
            loginError.value = context.getString(R.string.empty_password)
            return false
        }

        return true
    }

    private fun validateGuestLogin(name: String, email: String): Boolean {
        if (name.isEmpty()) {
            loginError.value = context.getString(R.string.empty_guest_name)
            return false
        }

        if (email.isEmpty()) {
            loginError.value = context.getString(R.string.empty_email)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginError.value = context.getString(R.string.incorrect_email)
            return false
        }

        return true
    }

}
