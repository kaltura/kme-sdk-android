package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.rest.response.signin.KmeGuestLoginResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeGuestLoginResponse.*
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse.*

class SignInViewModel(
    private val kmeSdk: KME
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
        isLoading.value = true
        kmeSdk.signInController.guest(name, email, roomAlias, success = {
            isLoading.value = false
            guestLoginResponse.value = it.data
        }, error = {
            isLoading.value = false
            loginError.value = it.message
        })
    }

}
