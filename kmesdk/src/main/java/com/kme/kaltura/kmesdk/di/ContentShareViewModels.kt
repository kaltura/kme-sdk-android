package com.kme.kaltura.kmesdk.di

import android.util.Log
import com.kme.kaltura.kmesdk.content.desktop.KmeDesktopShareViewModel
import com.kme.kaltura.kmesdk.content.playkit.KmeMediaContentViewModel
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesContentViewModel
import com.kme.kaltura.kmesdk.content.whiteboard.KmeWhiteboardContentViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.onClose

/**
 * Kme room content view models
 */
val contentShareViewModels = module {

    scope(named(KmeKoinScope.VIEW_MODELS)) {
        scoped { KmeSlidesContentViewModel() }.onClose { it?.onClosed() }
        scoped { KmeDesktopShareViewModel() }.onClose { it?.onClosed() }
        scoped { KmeWhiteboardContentViewModel() }.onClose { it?.onClosed() }
        scoped { KmeMediaContentViewModel() }.onClose { it?.onClosed() }
    }

}
