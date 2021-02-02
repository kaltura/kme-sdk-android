package com.kme.kaltura.kmesdk.controller.room.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.controller.room.IKmeDesktopShareModule
import com.kme.kaltura.kmesdk.controller.room.IKmeWebSocketModule
import com.kme.kaltura.kmesdk.util.messages.buildDesktopShareInitOnRoomInitMessage
import org.koin.core.inject

/**
 * An implementation for desktop share actions
 */
class KmeDesktopShareModuleImpl : KmeController(), IKmeDesktopShareModule {

    private val webSocketModule: IKmeWebSocketModule by inject()

    /**
     * Listen desktop share event if need
     */
    override fun listenDesktopShare(roomId: Long, companyId: Long) {
        webSocketModule.send(buildDesktopShareInitOnRoomInitMessage(roomId, companyId))
    }

}
