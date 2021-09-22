package com.kme.kaltura.kmesdk.di

import android.content.Context
import com.kme.kaltura.kmesdk.util.ResetableLazy
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.java.KoinJavaComponent.getKoin

/**
 * Base class for the KME controllers which has an access for Koin container
 */
object KmeKoinContext {

    internal lateinit var sdkKoin: Koin

    internal fun isInitialized() = ::sdkKoin.isInitialized

    internal val controllerScope: ResetableLazy<Scope> = ResetableLazy({
        if (isControllersScopesReleased()) {
            sdkKoin.createScope(
                SDK_SCOPE_CONTROLLERS_ID,
                named(SCOPE_CONTROLLER)
            )
        }
        sdkKoin.getScope(SDK_SCOPE_CONTROLLERS_ID)
    })

    internal val modulesScope: ResetableLazy<Scope> = ResetableLazy({
        if (isModulesScopesReleased()) {
            sdkKoin.createScope(
                SDK_SCOPE_MODULES_ID,
                named(SCOPE_MODULES)
            )
        }
        sdkKoin.getScope(SDK_SCOPE_MODULES_ID)
    })

    internal val viewModelsScope: ResetableLazy<Scope> = ResetableLazy({
        if (isViewModelsScopesReleased()) {
            sdkKoin.createScope(
                SDK_SCOPE_VIEW_MODELS_ID,
                named(SCOPE_VIEW_MODELS)
            )
        }
        sdkKoin.getScope(SDK_SCOPE_VIEW_MODELS_ID)
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
            modules(webSocketModule)
            modules(webRTCModule)
            modules(helpersModule)
        }.koin
    }

    private fun isControllersScopesReleased() =
        sdkKoin.getScopeOrNull(SDK_SCOPE_CONTROLLERS_ID)?.closed ?: true

    private fun isModulesScopesReleased(): Boolean =
        sdkKoin.getScopeOrNull(SDK_SCOPE_MODULES_ID)?.closed ?: true

    private fun isViewModelsScopesReleased(): Boolean =
        sdkKoin.getScopeOrNull(SDK_SCOPE_VIEW_MODELS_ID)?.closed ?: true

}

internal interface KmeKoinComponent : KoinComponent {

    fun controllersScope(): ResetableLazy<Scope> = KmeKoinContext.controllerScope

    fun modulesScope(): ResetableLazy<Scope> = KmeKoinContext.modulesScope

    fun viewModelsScope(): ResetableLazy<Scope> = KmeKoinContext.viewModelsScope

    override fun getKoin(): Koin {
        if (!KmeKoinContext.isInitialized()) {
            throw IllegalStateException("SDK is not initialized. Try to use KME.init() first.")
        }
        return KmeKoinContext.sdkKoin
    }

    fun releaseScopes() {
        controllersScope().value.close()
        controllersScope().invalidate()

        modulesScope().value.close()
        modulesScope().invalidate()

        viewModelsScope().value.close()
        viewModelsScope().invalidate()
    }

}

inline fun <reified T> Lazy<Scope>.inject(): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    value.get()
}

inline fun <reified T> Lazy<Scope>.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    getKoin().get(qualifier, parameters)
}
