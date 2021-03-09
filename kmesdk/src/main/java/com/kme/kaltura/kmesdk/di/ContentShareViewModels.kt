package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.content.desktop.KmeDesktopShareViewModel
import com.kme.kaltura.kmesdk.content.playkit.KmeMediaContentViewModel
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesContentViewModel
import com.kme.kaltura.kmesdk.content.whiteboard.KmeWhiteboardContentViewModel
import org.koin.dsl.module

/**
 * Kme room content view models
 */
val contentShareViewModels = module {

    single { KmeSlidesContentViewModel(get(), get(), get(), get()) }
    single { KmeDesktopShareViewModel(get()) }
    single { KmeWhiteboardContentViewModel(get()) }
    single { KmeMediaContentViewModel(get(), get(), get()) }

}
