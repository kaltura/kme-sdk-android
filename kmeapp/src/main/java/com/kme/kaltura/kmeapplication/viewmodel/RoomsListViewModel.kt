package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmeapplication.util.extensions.parseRoomAlias
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.rest.response.room.KmeBaseRoom
import com.kme.kaltura.kmesdk.rest.response.user.KmeGetUserInfoResponse
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserCompany

class RoomsListViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    private val isLoading = MutableLiveData<Boolean>()
    val isLoadingLiveData get() = isLoading as LiveData<Boolean>

    private val rooms = MutableLiveData<List<KmeBaseRoom>>()
    val roomsListLiveData get() = rooms as LiveData<List<KmeBaseRoom>>

    private val openRoomByLink = MutableLiveData<String>()
    val openRoomByLinkLiveData get() = openRoomByLink as LiveData<String>

    private val openRoomLinkError = MutableLiveData<Nothing>()
    val openRoomByLinkErrorLiveData get() = openRoomLinkError as LiveData<Nothing>

    private val roomsError = MutableLiveData<String?>()
    val roomsListErrorLiveData get() = roomsError as LiveData<String?>

    private val userCompanies = MutableLiveData<List<KmeUserCompany>>()
    val userCompaniesLiveData get() = userCompanies as LiveData<List<KmeUserCompany>>

    private val userCompaniesError = MutableLiveData<String?>()
    val userCompaniesErrorLiveData get() = userCompaniesError as LiveData<String?>

    private val selectedCompany = MutableLiveData<KmeUserCompany>()
    val selectedCompanyLiveData get() = selectedCompany as LiveData<KmeUserCompany>

    private val logout = MutableLiveData<Boolean>()
    val logoutLiveData get() = logout as LiveData<Boolean>

    fun fetchUserCompanies() {
        isLoading.value = true
        kmeSdk.userController.getUserInformation(success = {
            isLoading.value = false
            userCompanies.value = it.data?.userCompanies?.companies ?: emptyList()
            it.getDefaultCompany()?.let { defaultCompany ->
                selectedCompany.value = defaultCompany
            }
        }, error = {
            isLoading.value = false
            userCompaniesError.value = it.message
        })
    }

    fun fetchRoomsList(companyId: Long) {
        isLoading.value = true
        kmeSdk.roomController.roomModule.getRooms(companyId, 0, 20,
            success = {
                isLoading.value = false
                rooms.value = it.data?.rooms ?: emptyList()
            }, error = {
                isLoading.value = false
                roomsError.value = it.message
            })
    }

    fun selectCompany(company: KmeUserCompany) {
        selectedCompany.value = company
    }

    fun openRoomByLink(link: String?) {
        link.parseRoomAlias()?.let {
            openRoomByLink.value = it
        } ?: run {
            openRoomLinkError.value = null
        }
    }

    fun logout() {
        isLoading.value = true
        kmeSdk.userController.logout(success = {
            isLoading.value = false
            logout.postValue(true)
        }, error = {
            isLoading.value = false
            logout.postValue(false)
        })
    }

    private fun KmeGetUserInfoResponse.getDefaultCompany(): KmeUserCompany? {
        return data?.userCompanies?.companies?.find { kmeUserCompany -> kmeUserCompany.id == data?.userCompanies?.activeCompanyId }
    }

}
