package com.kme.kaltura.kmeapplication.util.extensions

import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState

fun KmeMediaDeviceState.defineNewDeviceStateByAdmin(
    enable: Boolean? = null
): KmeMediaDeviceState {
    var newState = KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
    enable?.let {
        if (enable) {
            when (this) {
                KmeMediaDeviceState.UNLIVE -> newState = KmeMediaDeviceState.LIVE
                KmeMediaDeviceState.DISABLED_UNLIVE -> newState = KmeMediaDeviceState.DISABLED_LIVE
                KmeMediaDeviceState.LIVE -> newState = KmeMediaDeviceState.LIVE
                KmeMediaDeviceState.DISABLED_LIVE -> newState = KmeMediaDeviceState.DISABLED_LIVE
                else -> KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
            }
        } else {
            when (this) {
                KmeMediaDeviceState.LIVE,
                KmeMediaDeviceState.LIVE_SUCCESS -> newState = KmeMediaDeviceState.UNLIVE
                KmeMediaDeviceState.DISABLED_LIVE -> newState = KmeMediaDeviceState.DISABLED_UNLIVE
                KmeMediaDeviceState.UNLIVE -> newState = KmeMediaDeviceState.UNLIVE
                KmeMediaDeviceState.DISABLED_UNLIVE -> newState = KmeMediaDeviceState.DISABLED_UNLIVE
                else -> KmeMediaDeviceState.DISABLED_NO_PERMISSIONS
            }
        }
        return newState
    }
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
