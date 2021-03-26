package com.kme.kaltura.kmesdk.controller

import com.kme.kaltura.kmesdk.rest.KmeApiException
import com.kme.kaltura.kmesdk.rest.response.signin.*

/**
 * An interface for signIn/signUp
 */
interface IKmeSignInController {

    /**
     * Registers new user by input data
     *
     * @param fullName name of a user
     * @param email email of a user
     * @param password password of a user
     * @param forceRegister
     * @param addToMailingList add email to subscriptions
     * @param success function to handle success result. Contains [KmeRegisterResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun register(
        fullName: String,
        email: String,
        password: String,
        forceRegister: Int,
        addToMailingList: Int,
        success: (response: KmeRegisterResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Login user by input data
     *
     * @param email email of a user
     * @param password password of a user
     * @param success function to handle success result. Contains [KmeLoginResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun login(
        email: String,
        password: String,
        success: (response: KmeLoginResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Reset password for existed user
     *
     * @param email email of a user
     * @param success function to handle success result. Contains [KmeResetPasswordResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun resetPassword(
        email: String,
        success: (response: KmeResetPasswordResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Login user by input data and allow to connect to the room
     *
     * @param name name of a user
     * @param email email of a user
     * @param roomAlias alias of a room
     * @param success function to handle success result. Contains [KmeGuestLoginResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun guest(
        name: String,
        email: String,
        roomAlias: String,
        success: (response: KmeGuestLoginResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

    /**
     * Logout from actual user
     *
     * @param success function to handle success result. Contains [KmeLogoutResponse] object
     * @param error function to handle error result. Contains [KmeApiException] object
     */
    fun logout(
        success: (response: KmeLogoutResponse) -> Unit,
        error: (exception: KmeApiException) -> Unit
    )

}
