package com.kme.kaltura.kmesdk.ws.message

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
enum class KmeMessageEvent(
    @SerializedName("name") val moduleName: String
) {

    @SerializedName("combinedEvent", alternate = ["combinedevent"])
    COMBINED_EVENT("combinedEvent"),

    /*
    * RoomInit
    * */

    @SerializedName("joinRoom", alternate = ["joinroom"])
    JOIN_ROOM("joinRoom"),

    @SerializedName("instructorIsOffLine", alternate = ["instructorisoffline"])
    INSTRUCTOR_IS_OFFLINE("instructorIsOffLine"),

    @SerializedName("awaitInstructorApproval", alternate = ["awaitinstructorapproval"])
    AWAIT_INSTRUCTOR_APPROVAL("awaitInstructorApproval"),

    @SerializedName("userApprovedByInstructor", alternate = ["userapprovedbyinstructor"])
    USER_APPROVED_BY_INSTRUCTOR("userApprovedByInstructor"),

    @SerializedName("userRejectedByInstructor", alternate = ["userrejectedbyinstructor"])
    USER_REJECTED_BY_INSTRUCTOR("userRejectedByInstructor"),

    @SerializedName("anyInstructorsConnectedToRoom", alternate = ["anyinstructorsconnectedtoroom"])
    ANY_INSTRUCTORS_IS_CONNECTED_TO_ROOM("anyInstructorsConnectedToRoom"),

    @SerializedName("joinedRoom", alternate = ["joinedroom"])
    JOINED_ROOM("joinedRoom"),

    @SerializedName("closeWebSocket", alternate = ["closewebsocket"])
    CLOSE_WEB_SOCKET("closeWebSocket"),

    @SerializedName("roomState", alternate = ["roomstate"])
    ROOM_STATE("roomState"),

    @SerializedName("newUserJoined", alternate = ["newuserjoined"])
    NEW_USER_JOINED("newUserJoined"),

    @SerializedName("roomAvailableForParticipant", alternate = ["roomavailableforparticipant"])
    ROOM_AVAILABLE_FOR_PARTICIPANT("roomAvailableForParticipant"),

    @SerializedName("roomParticipantsLimitReached", alternate = ["roomparticipantslimitreached"])
    ROOM_PARTICIPANT_LIMIT_REACHED("roomParticipantsLimitReached"),

    /*
    * Banners
    * */

    @SerializedName("roomHasPassword", alternate = ["roomhaspassword"])
    ROOM_HAS_PASSWORD("roomHasPassword"),

    @SerializedName("sendRoomPassword", alternate = ["sendroompassword"])
    SEND_ROOM_PASSWORD("sendRoomPassword"),

    @SerializedName("roomPasswordStatusReceived", alternate = ["roompasswordstatusreceived"])
    ROOM_PASSWORD_STATUS_RECEIVED("roomPasswordStatusReceived"),


    /*
    * Room Participants
    * */

    @SerializedName("userMediaStateInit", alternate = ["usermediastateinit"])
    USER_MEDIA_STATE_INIT("userMediaStateInit"),

    @SerializedName("userMediaStateChanged", alternate = ["usermediastatechanged"])
    USER_MEDIA_STATE_CHANGED("userMediaStateChanged"),

    @SerializedName("changeUserFocusEvent", alternate = ["changeuserfocusevent"])
    CHANGE_USER_FOCUS_EVENT("changeUserFocusEvent"),

    @SerializedName("setParticipantModerator", alternate = ["setparticipantmoderator"])
    SET_PARTICIPANT_MODERATOR("setParticipantModerator"),

    @SerializedName("userHandRaised", alternate = ["userhandraised"])
    USER_HAND_RAISED("userHandRaised"),

    @SerializedName("makeAllUsersHandPut", alternate = ["makeallusershandput"])
    MAKE_ALL_USERS_HAND_PUT("makeAllUsersHandPut"),

    /*
    * Streaming
    * */

    @SerializedName("startPublishing", alternate = ["startpublishing"])
    START_PUBLISHING("startPublishing"),

    @SerializedName("startViewing", alternate = ["startviewing"])
    START_VIEWING("startViewing"),

    @SerializedName("userStartedToPublish", alternate = ["userstartedtopublish"])
    USER_STARTED_TO_PUBLISH("userStartedToPublish"),

    @SerializedName("sdpAnswerToPublisher", alternate = ["sdpanswertopublisher"])
    SDP_ANSWER_TO_PUBLISHER("sdpAnswerToPublisher"),

    @SerializedName("sdpOfferForViewer", alternate = ["sdpofferforviewer"])
    SDP_OFFER_FOR_VIEWER("sdpOfferForViewer"),

    @SerializedName("forwardSdpAnswerFromViewer", alternate = ["forwardsdpanswerfromviewer"])
    FORWARD_SDP_ANSWER_FROM_VIEWER("forwardSdpAnswerFromViewer"),

    @SerializedName("iceGatheringDone", alternate = ["icegatheringdone"])
    ICE_GATHERING_DONE("iceGatheringDone"),

    @SerializedName("userDisconnected", alternate = ["userdisconnected"])
    USER_DISCONNECTED("userDisconnected"),

    /*
    * Chat
    * */

    @SerializedName("loadConversations", alternate = ["loadconversations"])
    LOAD_CONVERSATIONS("loadConversations"),

    @SerializedName("receiveSystemConversations", alternate = ["receivesystemconversations"])
    RECEIVE_CONVERSATIONS("receiveSystemConversations"),

    @SerializedName("loadMessages", alternate = ["loadmessages"])
    LOAD_MESSAGES("loadMessages"),

    @SerializedName("receiveMessage", alternate = ["receivemessage"])
    RECEIVE_MESSAGE("receiveMessage"),

    @SerializedName("sendMessage", alternate = ["sendmessage"])
    SEND_MESSAGE("sendMessage"),

    @SerializedName("deleteMessage", alternate = ["deletemessage"])
    DELETE_MESSAGE("deleteMessage"),

    @SerializedName("deletedMessage", alternate = ["deletedmessage"])
    DELETED_MESSAGE("deletedMessage"),

    @SerializedName("createDMConversation", alternate = ["createdmconversation"])
    CREATE_DM_CONVERSATION("createDMConversation"),

    @SerializedName("createdDMConversation", alternate = ["createddmconversation"])
    CREATED_DM_CONVERSATION("createdDMConversation"),

    @SerializedName("getDMConversation", alternate = ["getdmconversation"])
    GET_CONVERSATION("getDMConversation"),

    @SerializedName("gotDMConversation", alternate = ["gotdmconversation"])
    GOT_CONVERSATION("gotDMConversation"),

    /*
    * Room Notes
    * */

    @SerializedName("sendCreatedNote", alternate = ["sendcreatednote"])
    ROOM_NOTE_CREATED("sendCreatedNote"),

    @SerializedName("sendRename", alternate = ["sendrename"])
    ROOM_NOTE_RENAMED("sendRename"),

    @SerializedName("broadcastNoteToAll", alternate = ["broadcastnotetoall"])
    BROADCAST_ROOM_NOTE_TO_ALL("broadcastNoteToAll"),

    @SerializedName("subscribeToNote", alternate = ["subscribetonote"])
    ROOM_NOTE_SUBSCRIBE("subscribeToNote"),

    @SerializedName("sendNotesToListeners", alternate = ["sendNotesToListeners"])
    ROOM_NOTE_SEND_TO_LISTENERS("sendNotesToListeners"),

    @SerializedName("sendDeletedNote", alternate = ["senddeletedmote"])
    ROOM_NOTE_DELETED("sendDeletedNote"),

    /*
    * Room Settings
    * */

    @SerializedName("moduleDefaultSettingsChanged", alternate = ["moduledefaultsettingschanged"])
    ROOM_DEFAULT_SETTINGS_CHANGED("moduleDefaultSettingsChanged"),

    @SerializedName("roomSettingsChanged", alternate = ["roomsettingschanged"])
    ROOM_SETTINGS_CHANGED("roomSettingsChanged"),

    @SerializedName("forceSessionEnd", alternate = ["forcesessionend"])
    FORCE_SESSION_END("forceSessionEnd"),

    @SerializedName("userLeaveSession", alternate = ["userleavesession"])
    USER_LEAVE_SESSION("userLeaveSession"),

    /*
    * Active Content
    * */

    @SerializedName("initActiveContent", alternate = ["initactivecontent"])
    INIT_ACTIVE_CONTENT("initActiveContent"),

    @SerializedName("setActiveContent", alternate = ["setactivecontent"])
    SET_ACTIVE_CONTENT("setActiveContent"),

    /*
    * Slides Player
    * */

    @SerializedName("slideChanged", alternate = ["slideChanged"])
    SLIDE_CHANGED("slideChanged"),

    /*
    * Video
    * */

    @SerializedName("playerPlaying", alternate = ["playerplaying"])
    PLAYER_PLAYING("playerPlaying"),

    @SerializedName("playerPaused", alternate = ["playerpaused"])
    PLAYER_PAUSED("playerPaused"),

    @SerializedName("playerShouldSeekTo", alternate = ["playershouldseekto"])
    PLAYER_SEEK_TO("playerShouldSeekTo"),

    @SerializedName("syncPlayerState", alternate = ["syncplayerstate"])
    SYNC_PLAYER_STATE("syncPlayerState"),


    /*
    * Recording
    * */

    @SerializedName("startRecording", alternate = ["startrecording"])
    START_RECORDING("startRecording"),

    @SerializedName("initiated", alternate = ["initiated"])
    RECORDING_INITIATED("initiated"),

    @SerializedName("receivedStartRecording", alternate = ["receivedstartrecording"])
    RECORDING_STARTING("receivedStartRecording"),

    @SerializedName("stopRecording", alternate = ["stoprecording"])
    STOP_RECORDING("stopRecording"),

    @SerializedName("recording", alternate = ["recording"])
    RECORDING_STARTED("recording"),

    @SerializedName("receivedStopRecording", alternate = ["receivedstoprecording"])
    RECORDING_STOPPED("receivedStopRecording"),

    @SerializedName("recordingCompleted", alternate = ["recordingcompleted"])
    RECORDING_COMPLETED("recordingCompleted"),

    @SerializedName("conversionCompleted", alternate = ["conversioncompleted"])
    RECORDING_CONVERSION_COMPLETED("conversionCompleted"),

    @SerializedName("uploadCompleted", alternate = ["uploadcompleted"])
    RECORDING_UPLOAD_COMPLETED("uploadCompleted"),

    @SerializedName("recordingStatus", alternate = ["recordingstatus"])
    RECORDING_STATUS("recordingStatus"),

    @SerializedName("recorderFailed", alternate = ["recorderfailed"])
    RECORDING_FAILED("recorderFailed"),

    /*
    * Whiteboard
    * */
    @SerializedName("whiteboardPageData", alternate = ["whiteboardpagedata"])
    WHITEBOARD_PAGE_DATA("whiteboardPageData"),

    @SerializedName("whiteboardPageCleared", alternate = ["whiteboardpagecleared"])
    WHITEBOARD_PAGE_CLEARED("whiteboardPageCleared"),

    @SerializedName("whiteboardAllPagesCleared", alternate = ["whiteboardallpagescleared"])
    WHITEBOARD_ALL_PAGES_CLEARED("whiteboardAllPagesCleared"),

    @SerializedName("receiveLaserPosition", alternate = ["receivelaserposition"])
    RECEIVE_LASER_POSITION("receiveLaserPosition"),

    @SerializedName("laserDeactivatedForUser", alternate = ["laserdeactivatedforuser"])
    LASER_DEACTIVATED("laserDeactivatedForUser"),

    @SerializedName("receiveTransformation", alternate = ["receivetransformation"])
    RECEIVE_TRANSFORMATION("receiveTransformation"),

    @SerializedName("receiveDrawing", alternate = ["receivedrawing"])
    RECEIVE_DRAWING("receiveDrawing"),

    @SerializedName("drawingDeleted", alternate = ["drawingdeleted"])
    DELETE_DRAWING("drawingDeleted"),

    @SerializedName("whiteboardBackgroundTypeChanged", alternate = ["whiteboardbackgroundtypechanged"])
    WHITEBOARD_BACKGROUND_TYPE_CHANGED("whiteboardBackgroundTypeChanged"),

    @SerializedName("whiteboardActivePageSet", alternate = ["whiteboardActivePageSet"])
    WHITEBOARD_SET_ACTIVE_PAGE("whiteboardActivePageSet"),

    @SerializedName("whiteboardPageCreated", alternate = ["whiteboardpagecreated"])
    WHITEBOARD_PAGE_CREATED("whiteboardPageCreated"),

    /*
    * Share Desktop
    * */

    @SerializedName("initDesktopShareOnRoomInit", alternate = ["initdesktopsshareonroominit"])
    DESKTOP_SHARE_INIT_ON_ROOM_INIT("initDesktopShareOnRoomInit"),

    @SerializedName("desktopShareStateUpdated", alternate = ["desktopsharestateupdated"])
    DESKTOP_SHARE_STATE_UPDATED("desktopShareStateUpdated"),

    @SerializedName("desktopShareQualityUpdated", alternate = ["desktopsharequalityupdated"])
    DESKTOP_SHARE_QUALITY_UPDATED("desktopShareQualityUpdated");

    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        return moduleName.toLowerCase()
    }

}
