package com.kme.kaltura.kmeapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.rest.response.room.KmeBaseRoom

class RoomInfoViewModel(
    private val kmeSdk: KME
) : ViewModel() {

    private val isLoading = MutableLiveData<Boolean>()
    val isLoadingLiveData get() = isLoading as LiveData<Boolean>

    private val room = MutableLiveData<KmeBaseRoom>()
    val roomInfoLiveData get() = room as LiveData<KmeBaseRoom>

    private val roomError = MutableLiveData<String?>()
    val roomInfoErrorLiveData get() = roomError as LiveData<String?>

    fun fetchRoomInfo(roomAlias: String) {
        isLoading.value = true
        kmeSdk.roomController.roomModule.getRoomInfo(roomAlias, 0, 0,
            success = {
                isLoading.value = false
                it.data?.let { roomValue ->
                    room.value = roomValue
                } ?: run {
                    roomError.value = null
                }
            }, error = {
                isLoading.value = false
                roomError.value = it.message
            })
    }

}
