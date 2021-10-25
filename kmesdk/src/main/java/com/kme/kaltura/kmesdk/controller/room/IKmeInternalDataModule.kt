package com.kme.kaltura.kmesdk.controller.room

interface IKmeInternalDataModule: IKmeModule {

    var mainRoomId: Long
    var mainRoomAlias: String
    var breakoutRoomId: Long
    var companyId: Long

}
