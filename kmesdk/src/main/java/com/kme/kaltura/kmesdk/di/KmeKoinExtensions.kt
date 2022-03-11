package com.kme.kaltura.kmesdk.di

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kme.kaltura.kmesdk.controller.room.IKmeModule
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinScope.*
import com.kme.kaltura.kmesdk.util.ResetableLazy
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication

/**
 * Base class for the KME controllers which has an access for Koin container
 */
object KmeKoinContext {

    internal lateinit var sdkKoin: Koin

    internal fun isInitialized() = ::sdkKoin.isInitialized

    internal val controllerScope: ResetableLazy<Scope> = ResetableLazy({
        if (isScopeReleased(ROOM_CONTROLLER)) {
            sdkKoin.createScope(
                ROOM_CONTROLLER.id,
                named(ROOM_CONTROLLER.named)
            )
        }
        sdkKoin.getScope(ROOM_CONTROLLER.id)
    })

    internal val modulesScope: ResetableLazy<Scope> = ResetableLazy({
        if (isScopeReleased(MODULES)) {
            sdkKoin.createScope(MODULES.id, named(MODULES.named))
        }
        sdkKoin.getScope(MODULES.id)
    })

    internal val viewModelsScope: ResetableLazy<Scope> = ResetableLazy({
        if (isScopeReleased(VIEW_MODELS)) {
            sdkKoin.createScope(VIEW_MODELS.id, named(VIEW_MODELS.named))
        }
        sdkKoin.getScope(VIEW_MODELS.id)
    })

    fun init(appContext: Context) {
        if (isInitialized()) {
            throw IllegalStateException("SDK is already initialized!")
        }

        sdkKoin = koinApplication {
            androidContext(appContext.applicationContext)
            modules(restModule)
            modules(apiServicesModule)
            modules(controllersModule)
            modules(roomModules)
            modules(contentShareViewModels)
            modules(preferencesModule)
            modules(loggerModule)
            modules(webSocketModule)
            modules(webRTCModule)
            modules(helpersModule)
        }.koin
    }

    private fun isScopeReleased(scope: KmeKoinScope) =
        sdkKoin.getScopeOrNull(scope.id)?.closed ?: true

}

internal interface KmeKoinViewModel : KmeKoinComponent {
    fun onClosed()
}

internal interface KmeKoinComponent : KoinComponent {

    override fun getKoin(): Koin {
        if (!KmeKoinContext.isInitialized()) {
            throw IllegalStateException("SDK is not initialized. Try to use KME.init() first.")
        }
        return KmeKoinContext.sdkKoin
    }

    fun releaseScopes() {
        for (scopeType in KmeKoinScope.values()) {
            val scope = getScope(scopeType)
            scope.value.close()
            scope.invalidate()
        }
    }

}

internal fun getScope(scope: KmeKoinScope) = when (scope) {
    ROOM_CONTROLLER -> KmeKoinContext.controllerScope
    MODULES -> KmeKoinContext.modulesScope
    VIEW_MODELS -> KmeKoinContext.viewModelsScope
}

internal inline fun <reified T> scopedInject(
    scope: KmeKoinScope? = null
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    when {
        scope != null -> {
            getScope(scope).value.get()
        }
        T::class.java == IKmeRoomController::class.java -> {
            KmeKoinContext.controllerScope.value.get()
        }
        IKmeModule::class.java.isAssignableFrom(T::class.java) -> {
            KmeKoinContext.modulesScope.value.get()
        }
        ViewModel::class.java.isAssignableFrom(T::class.java) -> {
            KmeKoinContext.viewModelsScope.value.get()
        }
        else -> {
            throw IllegalArgumentException("Unknown scope type")
        }
    }
}