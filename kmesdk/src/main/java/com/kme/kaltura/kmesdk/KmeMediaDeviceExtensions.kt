package com.kme.kaltura.kmesdk

import com.kme.kaltura.kmesdk.ws.message.type.KmeMediaDeviceState

fun KmeMediaDeviceState.enabled(): Boolean = this == KmeMediaDeviceState.LIVE

fun KmeMediaDeviceState.softDisabled(): Boolean = this == KmeMediaDeviceState.DISABLED_LIVE

fun KmeMediaDeviceState.hardDisabled(): Boolean = this == KmeMediaDeviceState.UNLIVE

fun KmeMediaDeviceState.hasPermissions(): Boolean = this != KmeMediaDeviceState.DISABLED_NO_PERMISSIONS

fun KmeMediaDeviceState.toggleByUser() = when (this) {
    KmeMediaDeviceState.LIVE -> KmeMediaDeviceState.DISABLED_LIVE
    KmeMediaDeviceState.DISABLED_LIVE -> KmeMediaDeviceState.LIVE
    else -> KmeMediaDeviceState.DISABLED
}