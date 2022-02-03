package com.kme.kaltura.kmesdk.module.internal

import com.kme.kaltura.kmesdk.module.IKmeModule

interface IKmeInternalDataModule: IKmeModule {

    var mainRoomId: Long
    var mainRoomAlias: String
    var breakoutRoomId: Long
    var companyId: Long

    fun clear()

}
