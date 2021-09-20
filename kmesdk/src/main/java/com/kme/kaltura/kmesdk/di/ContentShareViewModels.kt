package com.kme.kaltura.kmesdk.di

import com.kme.kaltura.kmesdk.content.desktop.KmeDesktopShareViewModel
import com.kme.kaltura.kmesdk.content.playkit.KmeMediaContentViewModel
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesContentViewModel
import com.kme.kaltura.kmesdk.content.whiteboard.KmeWhiteboardContentViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Kme room content view models
 */
val contentShareViewModels = module {

    scope(named(KmeKoinScope.VIEW_MODELS)) {
        scoped { KmeSlidesContentViewModel() }
        scoped { KmeDesktopShareViewModel() }
        scoped { KmeWhiteboardContentViewModel() }
        scoped { KmeMediaContentViewModel() }
    }

}
