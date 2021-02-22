package com.kme.kaltura.kmeapplication.view.fragment.content

import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.viewmodel.content.ActiveContentViewModel
import com.kme.kaltura.kmeapplication.viewmodel.content.WhiteboardContentViewModel
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesView
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.fragment_slides_content.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SlidesContentFragment : Fragment() {

    private val activeContentViewModel: ActiveContentViewModel by sharedViewModel()
    private val whiteboardViewModel: WhiteboardContentViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_slides_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
        activeContentViewModel.setActiveContentLiveData.observe(
            viewLifecycleOwner,
            setActiveContentObserver
        )
        activeContentViewModel.slideChangedLiveData.observe(
            viewLifecycleOwner,
            slideChangedObserver
        )
        whiteboardViewModel.whiteboardPageLiveData.observe(
            viewLifecycleOwner,
            whiteboardPageDataObserver
        )
        whiteboardViewModel.receiveDrawingLiveData.observe(
            viewLifecycleOwner,
            receiveDrawingObserver
        )
        whiteboardViewModel.receiveLaserPositionLiveData.observe(
            viewLifecycleOwner,
            receivedLaserPositionObserver
        )
        whiteboardViewModel.hideLaserLiveData.observe(
            viewLifecycleOwner,
            hideLaserObserver
        )
        whiteboardViewModel.deleteDrawingLiveData.observe(
            viewLifecycleOwner,
            deleteDrawingObserver
        )
        whiteboardViewModel.whiteboardClearedLiveData.observe(
            viewLifecycleOwner,
            whiteboardPageClearedObserver
        )
        whiteboardViewModel.backgroundChangedLiveData.observe(
            viewLifecycleOwner,
            backgroundTypeChangedObserver
        )
        whiteboardViewModel.setActivePageLiveData.observe(
            viewLifecycleOwner,
            setActivePageObserver
        )
    }

    private val setActiveContentObserver = Observer<SetActiveContentPayload> {
        it?.let { payload ->
            val config = KmeSlidesView.Config(
                payload,
                activeContentViewModel.getCookie(),
                activeContentViewModel.getFilesUrl()
            ).apply {
                currentSlide = payload.metadata.currentSlide ?: 0
            }

            slidesView.init(config)
        } ?: run {
            Snackbar.make(root, R.string.error_active_content, Snackbar.LENGTH_SHORT).show()
        }
    }

    private val slideChangedObserver = Observer<Int> {
        slidesView.toSlide(it)
    }

    private val whiteboardPageDataObserver = Observer<List<WhiteboardPayload.Drawing>> {
        slidesView.setDrawings(it)
    }

    private val whiteboardPageClearedObserver = Observer<Nothing> {
        slidesView.removeDrawings()
    }

    private val receiveDrawingObserver = Observer<WhiteboardPayload.Drawing> {
        slidesView.addDrawing(it)
    }

    private val receivedLaserPositionObserver = Observer<PointF> {
        slidesView.updateLaserPosition(it)
    }

    private val hideLaserObserver = Observer<Nothing> {
        slidesView.hideLaser()
    }

    private val deleteDrawingObserver = Observer<String> {
        slidesView.removeDrawing(it)
    }

    private val backgroundTypeChangedObserver = Observer<KmeWhiteboardBackgroundType> {
        slidesView.updateBackground(it)
    }

    private val setActivePageObserver = Observer<String> {
        slidesView.setActivePage(it)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SlidesContentFragment()
    }
}
