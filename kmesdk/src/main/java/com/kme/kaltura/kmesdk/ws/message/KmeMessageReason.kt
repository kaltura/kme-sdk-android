package com.kme.kaltura.kmesdk.ws.message

import com.google.gson.annotations.SerializedName

enum class KmeMessageReason(
    @SerializedName("reason") val reason: String
) {

    @SerializedName("DUPLICATED_TAB", alternate = ["duplicated_tab"])
    DUPLICATED_TAB("DUPLICATED_TAB"),

    @SerializedName("session_timeout", alternate = ["SESSION_TIMEOUT"])
    SESSION_TIMEOUT("session_timeout"),

    @SerializedName("removed_user", alternate = ["REMOVED_USER"])
    REMOVED_USER("removed_user"),

    @SerializedName("user_leave_session")
    USER_LEAVE_SESSION("user_leave_session"),

    @SerializedName("instructor_ended_session")
    INSTRUCTOR_ENDED_SESSION("instructor_ended_session"),

    @SerializedName("host_ended_session")
    HOST_ENDED_SESSION("host_ended_session")

}