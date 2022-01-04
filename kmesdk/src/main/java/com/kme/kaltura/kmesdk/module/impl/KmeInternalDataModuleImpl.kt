package com.kme.kaltura.kmesdk.module.impl

import com.kme.kaltura.kmesdk.controller.impl.KmeController
import com.kme.kaltura.kmesdk.module.internal.IKmeInternalDataModule

/**
 * An implementation for room actions
 */
class KmeInternalDataModuleImpl : KmeController(), IKmeInternalDataModule {

    override var mainRoomId: Long = 0
    override var mainRoomAlias: String = ""
    override var breakoutRoomId: Long = 0
    override var companyId: Long = 0

}
