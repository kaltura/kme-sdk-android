package com.kme.kaltura.kmesdk.controller.impl

import android.content.Context
import com.google.android.gms.safetynet.SafetyNet
import com.kme.kaltura.kmesdk.controller.IKmeMetadataController
import com.kme.kaltura.kmesdk.controller.IKmeSignInController
import com.kme.kaltura.kmesdk.controller.IKmeUserController
import com.kme.kaltura.kmesdk.encryptWith
import com.kme.kaltura.kmesdk.prefs.IKmePreferences
import com.kme.kaltura.kmesdk.prefs.KmePrefsKeys
import com.kme.kaltura.kmesdk.removeCookies
import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.signin.*
import com.kme.kaltura.kmesdk.rest.safeApiCall
import com.kme.kaltura.kmesdk.rest.service.KmeSignInApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * An implementation for signIn/signUp
 */
class KmeSignInControllerImpl(
    private val context: Context
) : KmeController(), IKmeSignInController {

    private val signInApiService: KmeSignInApiService by inject()
    private val userController: IKmeUserController by inject()
    private val metadataController: IKmeMetadataController by inject()
    private val prefs: IKmePreferences by inject()
    private val uiScope = CoroutineScope(Dispatchers.Main)

    /**
     * Registers new user by input data
     */
    override fun register(
        fullName: String,
        email: String,
        password: String,
        forceRegister: Int,
        addToMailingList: Int,
        success: (response: KmeRegisterResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        val encryptedPassword = password.encryptWith(PASS_ENCRYPT_KEY)
        uiScope.launch {
            safeApiCall(
                {
                    signInApiService.register(
                        fullName,
                        email,
                        encryptedPassword,
                        forceRegister,
                        addToMailingList
                    )
                },
                success,
                error
            )
        }
    }

    /**
     * Login user by input data
     */
    override fun login(
        email: String,
        password: String,
        success: (response: KmeLoginResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        val encryptedPassword = password.encryptWith(PASS_ENCRYPT_KEY)
        removeCookies {
            uiScope.launch {
                safeApiCall(
                    { signInApiService.login(email, encryptedPassword) },
                    { response ->
                        metadataController.fetchMetadata(
                            success = {
                                response.data?.accessToken?.let { token ->
                                    prefs.putString(KmePrefsKeys.ACCESS_TOKEN, token)
                                }
                                success(response)
                            },
                            error = { error(it) }
                        )
                    },
                    error
                )
            }
        }
    }

    /**
     * Login user by token
     */
    override fun login(
        token: String,
        success: () -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        removeCookies {
            if (token.isNotEmpty()) {
                metadataController.fetchMetadata(
                    success = {
                        prefs.putString(KmePrefsKeys.ACCESS_TOKEN, token)
                        success()
                    },
                    error = { error(it) }
                )
            } else {
                error(
                    KmeApiException.SomethingBadHappenedException(
                        "Invalid token",
                        null
                    )
                )
            }
        }
    }

    /**
     * Reset password for existed user
     */
    override fun resetPassword(
        email: String,
        success: (response: KmeResetPasswordResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        removeCookies {
            resetPassword(email, "", success, error)
        }
    }

    private fun resetPassword(
        email: String,
        captchaToken: String,
        success: (response: KmeResetPasswordResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { signInApiService.resetPassword(email, captchaToken) },
                success,
                { exception ->
                    handleResetPasswordError(exception, success = { token ->
                        resetPassword(email, token, success, error)
                    }, error = { e ->
                        error(e)
                    })
                }
            )
        }
    }

    /**
     * Login user by input data and allow to connect to the room
     */
    override fun guest(
        name: String,
        email: String,
        roomAlias: String,
        success: (response: KmeGuestLoginResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        removeCookies {
            uiScope.launch {
                safeApiCall(
                    { signInApiService.guest(name, email, roomAlias, roomAlias) },
                    { response ->
                        metadataController.fetchMetadata(
                            success = { success(response) },
                            error = { error(it) }
                        )
                    },
                    error
                )
            }
        }
    }

    /**
     * Logout from actual user
     */
    override fun logout(
        success: (response: KmeLogoutResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        uiScope.launch {
            safeApiCall(
                { signInApiService.logout() },
                { response ->
                    userController.clearUserInfo()
                    removeCookies {
                        success(response)
                    }
                },
                error
            )
        }
    }

    private fun handleResetPasswordError(
        exception: KmeApiException,
        success: (token: String) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        if (exception.code == 506) {
            askForCaptcha(success = { token ->
                success(token)
            }, error = { e ->
                error(e)
            })
        } else {
            error(exception)
        }
    }

    private fun askForCaptcha(
        success: (token: String) -> Unit,
        error: (exception: KmeApiException) -> Unit
    ) {
        SafetyNet.getClient(context)
            .verifyWithRecaptcha(CAPTCHA_PUBLIC_SITE_KEY)
            .addOnCompleteListener { response ->
                if (response.isSuccessful) {
                    val token = response.result?.tokenResult
                    if (token?.isNotEmpty() == true) {
                        success(token)
                    }
                } else {
                    error(
                        KmeApiException.SomethingBadHappenedException(
                            response.exception?.message,
                            response.exception?.cause
                        )
                    )
                }
            }
    }

    companion object {
        private const val PASS_ENCRYPT_KEY = "8kjbca328hbvcm,z,123A"
        private const val CAPTCHA_PUBLIC_SITE_KEY = "6Lf8O44aAAAAAFo6UuGYsmmdqm7oCDXSwp1SLD5G"
    }

}
