package com.kme.kaltura.kmesdk.controller.room.internal

import com.kme.kaltura.kmesdk.controller.room.IKmeModule

interface IKmeInternalDataModule: IKmeModule {

    var mainRoomId: Long
    var mainRoomAlias: String
    var breakoutRoomId: Long
    var companyId: Long

}
