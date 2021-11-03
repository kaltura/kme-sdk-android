package com.kme.kaltura.kmesdk.ws.message.type.permissions

import com.google.gson.annotations.SerializedName

enum class KmePermissionKey(value: String) {

    @SerializedName("visibility")
    VISIBILITY("visibility"),

    @SerializedName("is_moderator")
    IS_MODERATOR("is_moderator"),

    @SerializedName("qna_chat")
    QNA_CHAT("qna_chat"),

    @SerializedName("public_chat")
    PUBLIC_CHAT("public_chat"),

    @SerializedName("class_mode")
    CLASS_MODE("class_mode"),

    @SerializedName("browser_focus_participant_list")
    BROWSER_FOCUS_PARTICIPANT_LIST("browser_focus_participant_list"),

    @SerializedName("start_private_chat")
    START_PRIVATE_CHAT("start_private_chat"),

    @SerializedName("mute_all_cams")
    MUTE_ALL_CAMS("mute_all_cams"),

    @SerializedName("self_assign")
    SELF_ASSIGN("self_assign"),

    @SerializedName("mute_all_mics")
    MUTE_ALL_MICS("mute_all_mics"),

    @SerializedName("mute_mode")
    MUTE_MODE("mute_mode"),

    @SerializedName("browser_focus_video_overlay")
    BROWSER_FOCUS_VIDEO_OVERLAY("browser_focus_video_overlay"),

    @SerializedName("chatWithModerators")
    CHAT_WITH_MODERATORS("chatWithModerators"),

    @SerializedName("auto_clear_chat_end_of_session")
    AUTO_CLEAR_CHAT_END_OF_SESSION("auto_clear_chat_end_of_session"),

    @SerializedName("allow_participants_move_moderator_objects")
    ALLOW_PARTICIPANTS_MOVE_MODERATOR_OBJECTS("allow_participants_move_moderator_objects")

}