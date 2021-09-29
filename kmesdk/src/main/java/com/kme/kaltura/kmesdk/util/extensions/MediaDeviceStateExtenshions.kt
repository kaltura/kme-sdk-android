package com.kme.kaltura.kmesdk.util.extensions

import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState
import com.kme.kaltura.kmesdk.ws.message.type.permissions.KmePermissionValue

fun KmeMediaDeviceState.defineNewDeviceStateByAdmin(
    value: KmePermissionValue? = null
): KmeMediaDeviceState {
    var newState = KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
    value?.let {
        when (it) {
            KmePermissionValue.ON -> {
                when (this) {
                    KmeMediaDeviceState.LIVE,
                    KmeMediaDeviceState.LIVE_SUCCESS -> newState = KmeMediaDeviceState.UNLIVE
                    KmeMediaDeviceState.DISABLED_LIVE -> newState = KmeMediaDeviceState.DISABLED_UNLIVE
                    KmeMediaDeviceState.UNLIVE -> newState = KmeMediaDeviceState.UNLIVE
                    KmeMediaDeviceState.DISABLED_UNLIVE -> newState = KmeMediaDeviceState.DISABLED_UNLIVE
                    else -> KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
                }
            }
            KmePermissionValue.OFF -> {
                when (this) {
                    KmeMediaDeviceState.UNLIVE -> newState = KmeMediaDeviceState.LIVE
                    KmeMediaDeviceState.DISABLED_UNLIVE -> newState = KmeMediaDeviceState.DISABLED_LIVE
                    KmeMediaDeviceState.LIVE -> newState = KmeMediaDeviceState.LIVE
                    KmeMediaDeviceState.DISABLED_LIVE -> newState = KmeMediaDeviceState.DISABLED_LIVE
                    else -> KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
                }
            }
            else -> {
            }
        }
        return newState
    } ?: run {
        when (this) {
            KmeMediaDeviceState.LIVE,
            KmeMediaDeviceState.LIVE_SUCCESS -> newState = KmeMediaDeviceState.UNLIVE
            KmeMediaDeviceState.DISABLED_LIVE -> newState = KmeMediaDeviceState.DISABLED_UNLIVE
            KmeMediaDeviceState.UNLIVE -> newState = KmeMediaDeviceState.LIVE
            KmeMediaDeviceState.DISABLED_UNLIVE -> newState = KmeMediaDeviceState.DISABLED_LIVE
            else -> KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
        }
        return newState
    }
}

fun KmeMediaDeviceState.defineNewDeviceStateByOwn(): KmeMediaDeviceState {
    var newState = KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
    when (this) {
        KmeMediaDeviceState.LIVE -> newState = KmeMediaDeviceState.DISABLED_LIVE
        KmeMediaDeviceState.DISABLED_LIVE -> newState = KmeMediaDeviceState.LIVE
        KmeMediaDeviceState.UNLIVE -> newState = KmeMediaDeviceState.DISABLED_UNLIVE
        KmeMediaDeviceState.DISABLED_UNLIVE -> newState = KmeMediaDeviceState.UNLIVE
        else -> KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
    }
    return newState
}
