package com.kme.kaltura.kmesdk.rest.service

import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLogoutResponse
import com.kme.kaltura.kmesdk.rest.response.signin.KmeRegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface KmeSignInApiService {

    @FormUrlEncoded
    @POST("signin/register")
    suspend fun register(
        @Field("SignupForm[full_name]") fullName: String,
        @Field("SignupForm[email]") email: String,
        @Field("SignupForm[password]") password: String,
        @Field("SignupForm[forceRegister]") forceRegister: Int,
        @Field("SignupForm[addToMailingList]") addToMailingList: Int
    ): KmeRegisterResponse

    @FormUrlEncoded
    @POST("signin/login")
    suspend fun login(
        @Field("LoginForm[email]") email: String,
        @Field("LoginForm[password]") password: String
    ): KmeLoginResponse

    @FormUrlEncoded
    @POST("signin/guest")
    suspend fun guest(
        @Field("Guest[name]") name: String,
        @Field("Guest[email]") email: String,
        @Field("Guest[room_alias]") roomAlias: String,
        @Field("room_alias") roomAliasField: String
    ): KmeLoginResponse

    @FormUrlEncoded
    @POST("signin/logout")
    suspend fun logout(): KmeLogoutResponse

}
