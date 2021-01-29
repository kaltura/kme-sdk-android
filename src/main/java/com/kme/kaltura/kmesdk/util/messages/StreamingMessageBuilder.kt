package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeStreamingModuleMessage.*

internal fun buildStartPublishingMessage(
    roomId: Long,
    userId: Long,
    companyId: Long,
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

internal fun buildAnswerFromViewerMessage(
    roomId: Long,
    userId: Long,
    companyId: Long,
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
    userId: Long,
    companyId: Long,
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
    userId: Long,
    companyId: Long,
    mediaServerId: Long
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
            "publisher"
        )
    }
}

internal fun buildGatheringViewDoneMessage(
    roomId: Long,
    userId: Long,
    companyId: Long,
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
