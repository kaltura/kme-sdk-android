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

    @SerializedName("getModuleState", alternate = ["getmodulestate"])
    GET_MODULE_STATE("getModuleState"),

    @SerializedName("moduleState", alternate = ["modulestate"])
    MODULE_STATE("moduleState"),

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

    @SerializedName("termsNeeded", alternate = ["termsneeded"])
    TERMS_NEEDED("termsNeeded"),

    @SerializedName("setTermsAgreement", alternate = ["settermsagreement"])
    SET_TERMS_AGREEMENT("setTermsAgreement"),

    @SerializedName("termsAgreed", alternate = ["termsagreed"])
    TERMS_AGREED("termsAgreed"),

    @SerializedName("termsRejected", alternate = ["termsrejected"])
    TERMS_REJECTED("termsRejected"),

    /*
    * Room Participants
    * */

    @SerializedName("userMediaStateInit", alternate = ["usermediastateinit"])
    USER_MEDIA_STATE_INIT("userMediaStateInit"),

    @SerializedName("userMediaStateChanged", alternate = ["usermediastatechanged"])
    USER_MEDIA_STATE_CHANGED("userMediaStateChanged"),

    @SerializedName("participantMuted", alternate = ["participantmuted"])
    PARTICIPANT_MUTED("participantMuted"),

    @SerializedName("allParticipantsMuted", alternate = ["allparticipantsmuted"])
    ALL_PARTICIPANTS_MUTED("allParticipantsMuted"),

    @SerializedName("changeUserFocusEvent", alternate = ["changeuserfocusevent"])
    CHANGE_USER_FOCUS_EVENT("changeUserFocusEvent"),

    @SerializedName("setParticipantModerator", alternate = ["setparticipantmoderator"])
    SET_PARTICIPANT_MODERATOR("setParticipantModerator"),

    @SerializedName("userHandRaised", alternate = ["userhandraised"])
    USER_HAND_RAISED("userHandRaised"),

    @SerializedName("makeAllUsersHandPut", alternate = ["makeallusershandput"])
    MAKE_ALL_USERS_HAND_PUT("makeAllUsersHandPut"),

    @SerializedName("removeUser", alternate = ["removeuser"])
    REMOVE_USER("removeUser"),

    @SerializedName("userRemoved", alternate = ["userremoved"])
    USER_REMOVED("userRemoved"),

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

    @SerializedName("userSpoke", alternate = ["userspoke"])
    USER_SPOKE("userSpoke"),

    @SerializedName("userSpeaking", alternate = ["userspeaking"])
    USER_SPEAKING("userSpeaking"),

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

    @SerializedName("clearedAllMessages", alternate = ["clearedallmessages"])
    CLEARED_ALL_MESSAGES("clearedAllMessages"),

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

    @SerializedName("changeParticipantPermissions", alternate = ["changeparticipantpermissions"])
    CHANGE_PARTICIPANT_PERMISSIONS("changeParticipantPermissions"),

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

    @SerializedName("annotationsStateChanged", alternate = ["annotationsstatechanged"])
    ANNOTATIONS_STATE_CHANGED("annotationsStateChanged"),

    /*
    * Video
    * */

    @SerializedName("playerPlaying", alternate = ["playerplaying"])
    PLAYER_PLAYING("playerPlaying"),

    @SerializedName("playerPaused", alternate = ["playerpaused"])
    PLAYER_PAUSED("playerPaused"),

    @SerializedName("playerShouldSeekTo", alternate = ["playershouldseekto"])
    PLAYER_SEEK_TO("playerShouldSeekTo"),

    @SerializedName("getInnerPlayerState", alternate = ["getinnerplayerstate"])
    GET_PLAYER_STATE("getInnerPlayerState"),

    @SerializedName("syncPlayerState", alternate = ["syncplayerstate"])
    SYNC_PLAYER_STATE("syncPlayerState"),

    /*
    * Recording
    * */

    @SerializedName("startRecording", alternate = ["startrecording"])
    RECORDING_ACTION_START("startRecording"),

    @SerializedName("stopRecording", alternate = ["stoprecording"])
    RECORDING_ACTION_STOP("stopRecording"),

    @SerializedName("initiated", alternate = ["initiated"])
    RECORDING_INITIATED("initiated"),

    @SerializedName("receivedStartRecording", alternate = ["receivedstartrecording"])
    RECORDING_RECEIVED_START("receivedStartRecording"),

    @SerializedName("recording", alternate = ["recording"])
    RECORDING_STARTED("recording"),

    @SerializedName("receivedStopRecording", alternate = ["receivedstoprecording"])
    RECORDING_RECEIVED_STOP("receivedStopRecording"),

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
    * Quick Poll
    * */

    @SerializedName("pollStarted", alternate = ["pollstarted"])
    QUICK_POLL_STARTED("pollStarted"),

    @SerializedName("pollEnded", alternate = ["pollended"])
    QUICK_POLL_ENDED("pollEnded"),

    @SerializedName("passEventToMs", alternate = ["passeventtoms"])
    QUICK_POLL_SEND_ANSWER("passEventToMs"),

    @SerializedName("userAnsweredPoll", alternate = ["useransweredpoll"])
    QUICK_POLL_USER_ANSWERED("userAnsweredPoll"),

    @SerializedName("pollAnswers", alternate = ["pollanswers"])
    QUICK_POLL_ANSWERS("pollAnswers"),

    /*
    * Share Desktop
    * */

    @SerializedName("initDesktopShareOnRoomInit", alternate = ["initdesktopsshareonroominit"])
    DESKTOP_SHARE_INIT_ON_ROOM_INIT("initDesktopShareOnRoomInit"),

    @SerializedName("desktopShareStateUpdated", alternate = ["desktopsharestateupdated"])
    DESKTOP_SHARE_STATE_UPDATED("desktopShareStateUpdated"),

    @SerializedName("desktopShareQualityUpdated", alternate = ["desktopsharequalityupdated"])
    DESKTOP_SHARE_QUALITY_UPDATED("desktopShareQualityUpdated"),

    @SerializedName("updateDesktopShareState", alternate = ["updatedesktopsharestate"])
    UPDATE_DESKTOP_SHARE_STATE("updateDesktopShareState"),

    @SerializedName("stopDesktopShare", alternate = ["stopdesktopshare"])
    STOP_DESKTOP_SHARE("stopDesktopShare"),

    /*
    * Breakout
    * */

    @SerializedName("passEventToMs", alternate = ["passeventtoms"])
    BREAKOUT_PASS_TO_MS("passEventToMs"),

    @SerializedName("startBreakoutSucceeded", alternate = ["startbreakoutsucceeded"])
    BREAKOUT_START_SUCCESS("startBreakoutSucceeded"),

    @SerializedName("stopBreakoutSucceeded", alternate = ["stopbreakoutsucceeded"])
    BREAKOUT_STOP_SUCCESS("stopBreakoutSucceeded"),

    @SerializedName("addBreakoutRoomSucceeded", alternate = ["addbreakoutroomsucceeded"])
    BREAKOUT_ADD_ROOM_SUCCESS("addBreakoutRoomSucceeded"),

    @SerializedName("deleteBreakoutRoomSucceeded", alternate = ["deletebreakoutroomducceeded"])
    BREAKOUT_DELETE_ROOM_SUCCESS("deleteBreakoutRoomSucceeded"),

    @SerializedName("changeBreakoutRoomNameSucceeded", alternate = ["changebreakoutroomnamesucceeded"])
    BREAKOUT_CHANGE_ROOM_NAME_SUCCESS("changeBreakoutRoomNameSucceeded"),

    @SerializedName("assignParticipants", alternate = ["assignparticipants"])
    BREAKOUT_ASSIGN_PARTICIPANTS("assignParticipants"),

    @SerializedName("assignParticipantsSucceeded", alternate = ["assignparticipantssucceeded"])
    BREAKOUT_ASSIGN_PARTICIPANTS_SUCCESS("assignParticipantsSucceeded"),

    @SerializedName("moveParticipantsToNextRoomsSucceeded", alternate = ["moveparticipantstonextroomssucceeded"])
    BREAKOUT_MOVE_TO_NEXT_ROOM("moveParticipantsToNextRoomsSucceeded"),

    @SerializedName("reshuffleAssignmentsSucceeded", alternate = ["reshuffleassignmentssucceeded"])
    BREAKOUT_RESHUFFLE_ASSIGNMENTS_SUCCESS("reshuffleAssignmentsSucceeded"),

    @SerializedName("clearAssignmentsSucceeded", alternate = ["clearassignmentssucceeded"])
    BREAKOUT_CLEAR_ASSIGNMENTS_SUCCESS("clearAssignmentsSucceeded"),

    @SerializedName("moderatorJoinedBreakoutRoomSucceeded", alternate = ["moderatorjoinedbreakoutroomsucceeded"])
    BREAKOUT_MODERATOR_JOINED_SUCCESS("moderatorJoinedBreakoutRoomSucceeded"),

    @SerializedName("userJoinedBreakoutRoom", alternate = ["userjoinedbreakoutroom"])
    BREAKOUT_USER_JOINED("userJoinedBreakoutRoom"),

    @SerializedName("userJoinedBreakoutRoomSucceeded", alternate = ["userjoinedbreakoutroomsucceeded"])
    BREAKOUT_USER_JOINED_SUCCESS("userJoinedBreakoutRoomSucceeded"),

    @SerializedName("extendTimeLimitSucceeded", alternate = ["extendtimelimitsucceeded"])
    BREAKOUT_EXTEND_TIME_LIMIT_SUCCESS("extendTimeLimitSucceeded"),

    @SerializedName("callToInstructor", alternate = ["calltoinstructor"])
    BREAKOUT_CALL_TO_INSTRUCTOR("callToInstructor"),

    @SerializedName("callToInstructorSucceeded", alternate = ["calltoinstructorsucceeded"])
    BREAKOUT_CALL_TO_INSTRUCTOR_SUCCESS("callToInstructorSucceeded"),

    @SerializedName("breakoutMessage", alternate = ["breakoutmessage"])
    BREAKOUT_INSTRUCTOR_MESSAGE("breakoutMessage"),

    /*
    * Large room
    * */

    @SerializedName("largeRoomModeInitiating", alternate = ["largeroommodeinitiating"])
    XL_ROOM_MODE_INIT("largeRoomModeInitiating"),

    @SerializedName("largeRoomModeReady", alternate = ["largeroommodeready"])
    XL_ROOM_MODE_READY("largeRoomModeReady"),

    @SerializedName("largeRoomModeActive", alternate = ["largeroommodeactive"])
    XL_ROOM_MODE_ACTIVE("largeRoomModeActive"),

    @SerializedName("largeRoomModeFinished", alternate = ["largeroommodefinished"])
    XL_ROOM_MODE_FINISHED("largeRoomModeFinished"),

    @SerializedName("largeRoomModeAborted", alternate = ["largeroommodeaborted"])
    XL_ROOM_MODE_ABORTED("largeRoomModeAborted"),

    @SerializedName("largeRoomModeFailed", alternate = ["largeroommodefailed"])
    XL_ROOM_MODE_FAILED("largeRoomModeFailed"),

    @SerializedName("largeRoomModeNotActive", alternate = ["largeroommodenotactive"])
    XL_ROOM_MODE_NOT_ACTIVE("largeRoomModeNotActive");

    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        return moduleName.lowercase()
    }

}
