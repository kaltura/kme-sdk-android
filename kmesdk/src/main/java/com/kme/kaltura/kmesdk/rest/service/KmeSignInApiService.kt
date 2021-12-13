package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.room.KmeJoinRoomResponse
import com.kme.kaltura.kmesdk.rest.response.signin.*
import retrofit2.http.*

/**
 * An interface for signIn/signUp API calls
 */
interface KmeSignInApiService {

    /**
     * Registers new user by input data
     *
     * @param fullName name of a user
     * @param email email of a user
     * @param password password of a user
     * @param forceRegister
     * @param addToMailingList add email to subscriptions
     * @return [KmeRegisterResponse] object in success case
     */
    @FormUrlEncoded
    @POST("signin/register")
    suspend fun register(
        @Field("SignupForm[full_name]") fullName: String,
        @Field("SignupForm[email]") email: String,
        @Field("SignupForm[password]") password: String,
        @Field("SignupForm[forceRegister]") forceRegister: Int,
        @Field("SignupForm[addToMailingList]") addToMailingList: Int
    ): KmeRegisterResponse

    /**
     * Login user by input data
     *
     * @param email email of a user
     * @param password password of a user
     * @return [KmeLoginResponse] object in success case
     */
    @FormUrlEncoded
    @POST("signin/login")
    suspend fun login(
        @Field("LoginForm[email]") email: String,
        @Field("LoginForm[password]") password: String
    ): KmeLoginResponse

    /**
     * Handling cookies for login via deep linking
     *
     * @param hash identifier for a user
     * @param mobile platform indicator
     * @return [KmeJoinRoomResponse] object in success case
     */
    @GET("room/join")
    suspend fun login(
        @Query("hash") hash: String,
        @Query("for_mobile_app") mobile: Int
    ): KmeJoinRoomResponse

    /**
     * Reset password for existed user
     *
     * @param email email of a user
     * @param captchaToken
     * @return [KmeResetPasswordResponse] object in success case
     */
    @FormUrlEncoded
    @POST("signin/requestPasswordReset")
    suspend fun resetPassword(
        @Field("PasswordResetRequestForm[email]") email: String,
        @Field("PasswordResetRequestForm[captchaToken]") captchaToken: String
    ): KmeResetPasswordResponse

    /**
     * Login user by input data and allow to connect to the room
     *
     * @param name name of a user
     * @param email email of a user
     * @param roomAlias alias of a room
     * @param roomAliasField alias of a room
     * @return [KmeGuestLoginResponse] object in success case
     */
    @FormUrlEncoded
    @POST("signin/guest")
    suspend fun guest(
        @Field("Guest[name]") name: String,
        @Field("Guest[email]") email: String,
        @Field("Guest[room_alias]") roomAlias: String,
        @Field("room_alias") roomAliasField: String
    ): KmeGuestLoginResponse

    /**
     * Logout from actual user
     *
     * @return [KmeLogoutResponse] object in success case
     */
    @POST("signin/logout")
    suspend fun logout(): KmeLogoutResponse

}
