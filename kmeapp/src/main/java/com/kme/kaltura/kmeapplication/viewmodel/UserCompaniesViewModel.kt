package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.prefs.IAppPreferences
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserCompany

class UserCompaniesViewModel(
    private val kmeSdk: KME,
    private val appPreferences: IAppPreferences
) : ViewModel() {

    private val isLoading = MutableLiveData<Boolean>()
    val isLoadingLiveData get() = isLoading as LiveData<Boolean>

    private val userCompanies = MutableLiveData<List<KmeUserCompany>>()
    val userCompaniesLiveData get() = userCompanies as LiveData<List<KmeUserCompany>>

    private val userCompaniesError = MutableLiveData<String?>()
    val userCompaniesErrorLiveData get() = userCompaniesError as LiveData<String?>

    fun fetchUserCompanies() {
        isLoading.value = true
        kmeSdk.userController.getUserInformation(success = {
            isLoading.value = false
            userCompanies.value = it.data?.userCompanies?.companies ?: emptyList()
        }, error = {
            isLoading.value = false
            userCompaniesError.value = it.message
        })
    }

}
