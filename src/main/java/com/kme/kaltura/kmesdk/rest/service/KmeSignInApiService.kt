package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.signin.KmeGuestLoginResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLogoutResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeRegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
    @FormUrlEncoded
    @POST("signin/logout")
    suspend fun logout(): KmeLogoutResponse

}
