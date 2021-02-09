package com.kme.kaltura.kmesdk.util.messages

import com.kme.kaltura.kmesdk.ws.message.KmeMessageEvent
import com.kme.kaltura.kmesdk.ws.message.KmeMessageEventType
import com.kme.kaltura.kmesdk.ws.message.KmeMessageModule
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomRecordingMessage
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomRecordingMessage.RecordingStartPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeRoomRecordingMessage.RecordingStopPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeConstraint

internal fun buildStartRoomRecordingMessage(
    roomId: Long,
    companyId: Long,
    timestamp: Long,
    recordingDuration: Long,
    timeZone: Long
): KmeRoomRecordingMessage<RecordingStartPayload> {
    return KmeRoomRecordingMessage<RecordingStartPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        type = KmeMessageEventType.VOID
        module = KmeMessageModule.RECORDING
        name = KmeMessageEvent.START_RECORDING
        payload = RecordingStartPayload(
            recordingDuration,
            timestamp,
            timeZone,
            roomId,
            companyId
        )
    }
}

internal fun buildStopRoomRecordingMessage(
    roomId: Long,
    companyId: Long
): KmeRoomRecordingMessage<RecordingStopPayload> {
    return KmeRoomRecordingMessage<RecordingStopPayload>().apply {
        constraint = listOf(KmeConstraint.INCLUDE_SELF)
        type = KmeMessageEventType.VOID
        module = KmeMessageModule.RECORDING
        name = KmeMessageEvent.STOP_RECORDING
        payload = RecordingStopPayload(roomId, companyId)
    }
}
