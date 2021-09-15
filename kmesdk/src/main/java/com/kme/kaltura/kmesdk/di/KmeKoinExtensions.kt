package com.kme.kaltura.kmesdk.di

import android.content.Context
import android.util.Log
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import java.io.Serializable
import kotlin.reflect.KProperty

/**
 * Base class for the KME controllers which has an access for Koin container
 */
object KmeKoinContext {

    internal val controllerScope: ResetableLazy<Scope> = ResetableLazy({
        sdkKoin.getScope(SDK_SCOPE_CONTROLLERS_ID)
    })

    internal val modulesScope: ResetableLazy<Scope> = ResetableLazy({
        sdkKoin.getScope(SDK_SCOPE_MODULES_ID)
    })

    internal val viewModelsScope: ResetableLazy<Scope> = ResetableLazy({
        sdkKoin.getScope(SDK_SCOPE_VIEW_MODELS_ID)
    })

    internal lateinit var sdkKoin: Koin

    internal fun isInitialized() = ::sdkKoin.isInitialized

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

}

internal interface KmeKoinComponent : KoinComponent {

    fun createControllersScope() {
        if (isControllersScopesReleased()) {
            getKoin().createScope(
                SDK_SCOPE_CONTROLLERS_ID,
                named(SCOPE_CONTROLLER)
            )
        }
    }

    fun createModulesScope() {
        if (isModulesScopesReleased()) {
            getKoin().createScope(
                SDK_SCOPE_MODULES_ID,
                named(SCOPE_MODULES)
            )
        }
    }

    fun createViewModelsScope() {
        if (isViewModelsScopesReleased()) {
            getKoin().createScope(
                SDK_SCOPE_VIEW_MODELS_ID,
                named(SCOPE_VIEW_MODELS)
            )
        }
    }

    fun controllersScope(): ResetableLazy<Scope> = KmeKoinContext.controllerScope

    fun modulesScope(): ResetableLazy<Scope> = KmeKoinContext.modulesScope

    fun viewModelsScope(): ResetableLazy<Scope> = KmeKoinContext.viewModelsScope

    fun isControllersScopesReleased(): Boolean {
        return getKoin().getScopeOrNull(SDK_SCOPE_CONTROLLERS_ID)?.closed ?: true
    }

    fun isModulesScopesReleased(): Boolean {
        return getKoin().getScopeOrNull(SDK_SCOPE_MODULES_ID)?.closed ?: true
    }

    fun isViewModelsScopesReleased(): Boolean {
        return getKoin().getScopeOrNull(SDK_SCOPE_VIEW_MODELS_ID)?.closed ?: true
    }

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

private object UNINITIALIZED_VALUE
class ResetableLazy<T>(private val initializer: () -> T, lock: Any? = null) : Lazy<T>,
    Serializable {

    init {
        Log.e("TAG", "InvalidatableLazyImpl")
    }

    @Volatile
    private var _value: Any? = UNINITIALIZED_VALUE

    private val lock = lock ?: this

    fun invalidate() {
        _value = UNINITIALIZED_VALUE
    }

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    _v2 as T
                } else {
                    val typedValue = initializer.invoke()
                    _value = typedValue
                    typedValue
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String =
        if (isInitialized()) value.toString() else "Lazy value not initialized yet."

    operator fun setValue(any: Any, property: KProperty<*>, t: T) {
        _value = t
    }
}

inline fun <reified T> Lazy<Scope>.inject(): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    val v = value.get<T>()
    v
}