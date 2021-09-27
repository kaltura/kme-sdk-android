package com.kme.kaltura.kmesdk.di

private const val SCOPE_ROOM_CONTROLLER_ID = "SCOPE_ROOM_CONTROLLER_ID"
private const val SCOPE_MODULES_ID = "SCOPE_MODULES_ID"
private const val SCOPE_BOR_MODULES_ID = "SCOPE_BOR_MODULES_ID"
private const val SCOPE_VIEW_MODELS_ID = "SCOPE_VIEW_MODELS_ID"

private const val SCOPE_ROOM_CONTROLLER = "scope_room_controller"
private const val SCOPE_MODULES = "scope_modules"
private const val SCOPE_BOR_MODULES = "scope_bor_modules"
private const val SCOPE_VIEW_MODELS = "scope_view_models"

enum class KmeKoinScope(val id: String, val named: String) {
    ROOM_CONTROLLER(SCOPE_ROOM_CONTROLLER_ID, SCOPE_ROOM_CONTROLLER),
    MODULES(SCOPE_MODULES_ID, SCOPE_MODULES),
    BOR_MODULES(SCOPE_BOR_MODULES_ID, SCOPE_BOR_MODULES),
    VIEW_MODELS(SCOPE_VIEW_MODELS_ID, SCOPE_VIEW_MODELS);

    override fun toString(): String {
        return named
    }
}