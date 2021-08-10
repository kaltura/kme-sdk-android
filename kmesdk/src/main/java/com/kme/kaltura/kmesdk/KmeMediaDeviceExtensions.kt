package com.kme.kaltura.kmesdk

import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState

fun KmeMediaDeviceState.isEnabled(): Boolean = this == KmeMediaDeviceState.LIVE

fun KmeMediaDeviceState.enableByUser(): KmeMediaDeviceState {
    return KmeMediaDeviceState.LIVE
}

fun KmeMediaDeviceState.disableByUser(): KmeMediaDeviceState {
    return KmeMediaDeviceState.DISABLED_LIVE
}

fun KmeMediaDeviceState.toggleByUser() = when (this) {
    KmeMediaDeviceState.LIVE -> KmeMediaDeviceState.DISABLED_LIVE
    KmeMediaDeviceState.DISABLED_LIVE -> KmeMediaDeviceState.LIVE
    else -> KmeMediaDeviceState.DISABLED
}

fun KmeMediaDeviceState.softDisableByAdmin(): KmeMediaDeviceState {
    return KmeMediaDeviceState.DISABLED_LIVE
}

fun KmeMediaDeviceState.hardDisableByAdmin(): KmeMediaDeviceState {
    return KmeMediaDeviceState.UNLIVE
}

fun KmeMediaDeviceState.unlive(): KmeMediaDeviceState {
    return KmeMediaDeviceState.UNLIVE
}