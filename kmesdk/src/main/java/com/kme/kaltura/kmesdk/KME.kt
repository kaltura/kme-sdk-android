package com.kme.kaltura.kmesdk

import android.content.Context
import com.kme.kaltura.kmesdk.controller.IKmeMetadataController
import com.kme.kaltura.kmesdk.controller.IKmeSignInController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.controller.room.IKmeRoomController
import com.kme.kaltura.kmesdk.di.KmeKoinComponent
import com.kme.kaltura.kmesdk.di.KmeKoinContext
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.KmeChangeableBaseUrlInterceptor
import com.kme.kaltura.kmesdk.service.CsrfUpdater
import com.kme.kaltura.kmesdk.util.ServerConfiguration
import org.koin.core.inject

/**
 * Main class for KME SDK.
 */
class KME : KmeKoinComponent {

    val signInController: IKmeSignInController by inject()
    val csrfUpdater: CsrfUpdater by inject()

    val userController: IKmeUserController by inject()
    val roomController: IKmeRoomController by inject()

    private val urlInterceptor: KmeChangeableBaseUrlInterceptor by inject()
    private val prefs: IKmePreferences by inject()
    private val metadataController: IKmeMetadataController by inject()

    var isSDKInitialized = false
        private set

    companion object {
        private lateinit var instance: KME

        /**
         * Instantiate a KME singleton
         *
         * @return an instance of KME
         */
        fun getInstance(): KME {
            if (!::instance.isInitialized) {
                instance = KME()
            }
            return instance
        }
    }

    /**
     * Initialization function. Initializes all needed controllers and modules.
     * In the same place - fetching metadata from the server to use it in future REST API calls.
     *
     * @param context application context
     * @param success function to handle success initialization event
     * @param error function to handle error initialization event
     */
    fun initSDK(
        context: Context,
        success: () -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        KmeKoinContext.init(context)

        metadataController.fetchMetadata(success = {
            isSDKInitialized = true

            if (roomController.isConnected()) {
                roomController.disconnect()
            }

            csrfUpdater.start()
            success()
        }, error = {
            error(it)
        })
    }

    /**
     * Initialization function. Reload metadata from the server to use it in future REST API calls.
     *
     * @throws IllegalStateException if it is called before [initSDK]
     * @param success function to handle success initialization event
     * @param error function to handle error initialization event
     */
    fun reloadSDK(
        success: () -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        if (!isSDKInitialized) {
            throw IllegalStateException("SDK is not initialized. Try to use initSDK() first.")
        }
        isSDKInitialized = false
        metadataController.fetchMetadata(success = {
            isSDKInitialized = true

            if (roomController.isConnected()) {
                roomController.disconnect()
            }

            csrfUpdater.start()
            success()
        }, error = {
            error(it)
        })
    }

    /**
     * @return url for accessing files in the room
     */
    fun getFilesUrl() = metadataController.getMetadata()?.filesUrl

    /**
     * @return cookies from metadata
     */
    fun getCookies() = prefs.getString(KmePrefsKeys.COOKIE)

    fun isSameServerConfiguration(roomUrl: String?): Boolean? {
        return urlInterceptor.isSameServerConfiguration(roomUrl)
    }

    fun changeServerConfiguration(configuration: ServerConfiguration) {
        urlInterceptor.setServerConfiguration(configuration)
    }

    fun getServerConfiguration() = urlInterceptor.getServerConfiguration()
}
