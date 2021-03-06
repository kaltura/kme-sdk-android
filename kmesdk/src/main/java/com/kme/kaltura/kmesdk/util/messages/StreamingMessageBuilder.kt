package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.*

internal fun buildStartPublishingMessage(
    roomId: Long,
    companyId: Long,
    userId: Long,
    sdpType: String,
    sdpDescription: String
): KmeStreamingModuleMessage<StartPublishingPayload> {
    return KmeStreamingModuleMessage<StartPublishingPayload>().apply {
        module = KmeMessageModule.STREAMING
        name = KmeMessageEvent.START_PUBLISHING
        type = KmeMessageEventType.VOID
        payload = StartPublishingPayload(
            roomId,
            userId,
            companyId,
            StreamingPayload.SDP(sdpType, sdpDescription),
            "publisher"
        )
    }
}

internal fun buildStartScreenShareMessage(
    roomId: Long,
    companyId: Long,
    userId: Long,
    sdpType: String,
    sdpDescription: String
): KmeStreamingModuleMessage<StartScreenSharePayload> {
    return KmeStreamingModuleMessage<StartScreenSharePayload>().apply {
        module = KmeMessageModule.STREAMING
        name = KmeMessageEvent.START_PUBLISHING
        type = KmeMessageEventType.VOID
        payload = StartScreenSharePayload(
            roomId,
            userId,
            companyId,
            StreamingPayload.SDP(sdpType, sdpDescription),
            streamType = "publisher",
            isDesktop = true,
            withDataChannel = false
        )
    }
}

internal fun buildAnswerFromViewerMessage(
    roomId: Long,
    companyId: Long,
    userId: Long,
    sdpType: String,
    sdpDescription: String,
    requestedUserIdStream: String,
    mediaServerId: Long,
): KmeStreamingModuleMessage<SdpAnswerToFromViewer> {
    return KmeStreamingModuleMessage<SdpAnswerToFromViewer>().apply {
        module = KmeMessageModule.STREAMING
        name = KmeMessageEvent.FORWARD_SDP_ANSWER_FROM_VIEWER
        type = KmeMessageEventType.VOID
        payload = SdpAnswerToFromViewer(
            roomId,
            userId,
            companyId,
            "videoroom",
            StreamingPayload.SDP(sdpType, sdpDescription),
            requestedUserIdStream,
            mediaServerId,
            "viewer"
        )
    }
}

internal fun buildStartViewingMessage(
    roomId: Long,
    companyId: Long,
    userId: Long,
    requestedUserIdStream: String?
): KmeStreamingModuleMessage<StartViewingPayload> {
    return KmeStreamingModuleMessage<StartViewingPayload>().apply {
        module = KmeMessageModule.STREAMING
        name = KmeMessageEvent.START_VIEWING
        type = KmeMessageEventType.VOID
        payload = StartViewingPayload(
            roomId,
            userId,
            companyId,
            requestedUserIdStream,
            "viewer"
        )
    }
}

internal fun buildGatheringPublishDoneMessage(
    roomId: Long,
    companyId: Long,
    userId: Long,
    mediaServerId: Long,
    isDesktop: Boolean
): KmeStreamingModuleMessage<IceGatheringPublishDonePayload> {
    return KmeStreamingModuleMessage<IceGatheringPublishDonePayload>().apply {
        module = KmeMessageModule.STREAMING
        name = KmeMessageEvent.ICE_GATHERING_DONE
        type = KmeMessageEventType.VOID
        payload = IceGatheringPublishDonePayload(
            roomId,
            userId,
            companyId,
            mediaServerId,
            "videoroom",
            "publisher",
            isDesktop
        )
    }
}

internal fun buildGatheringViewDoneMessage(
    roomId: Long,
    companyId: Long,
    userId: Long,
    requestedUserIdStream: String,
    mediaServerId: Long
): KmeStreamingModuleMessage<IceGatheringViewingDonePayload> {
    return KmeStreamingModuleMessage<IceGatheringViewingDonePayload>().apply {
        module = KmeMessageModule.STREAMING
        name = KmeMessageEvent.ICE_GATHERING_DONE
        type = KmeMessageEventType.VOID
        payload = IceGatheringViewingDonePayload(
            roomId,
            userId,
            companyId,
            mediaServerId,
            "videoroom",
            requestedUserIdStream,
            "viewer"
        )
    }
}

internal fun buildUserSpeakingMessage(
    roomId: Long,
    companyId: Long,
    userId: Long,
    volumeData: String
): KmeStreamingModuleMessage<UserSpeakingPayload> {
    return KmeStreamingModuleMessage<UserSpeakingPayload>().apply {
        module = KmeMessageModule.STREAMING
        name = KmeMessageEvent.USER_SPOKE
        type = KmeMessageEventType.VOID
        payload = UserSpeakingPayload(
            roomId,
            companyId,
            userId,
            volumeData
        )
    }
}
