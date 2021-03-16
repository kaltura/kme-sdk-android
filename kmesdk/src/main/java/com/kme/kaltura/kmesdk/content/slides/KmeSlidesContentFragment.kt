package com.kme.kaltura.kmesdk.content.slides

import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.kme.kaltura.kmesdk.R
import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.content.whiteboard.KmeWhiteboardContentViewModel
import com.kme.kaltura.kmesdk.databinding.FragmentSlidesContentBinding
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import com.kme.kaltura.kmesdk.ws.message.module.KmeWhiteboardModuleMessage.WhiteboardPayload
import com.kme.kaltura.kmesdk.ws.message.type.KmeUserType
import com.kme.kaltura.kmesdk.ws.message.type.KmeWhiteboardBackgroundType
import org.koin.core.inject

class KmeSlidesContentFragment : KmeContentView() {

    private val slidesContentViewModel: KmeSlidesContentViewModel by inject()
    private val whiteboardViewModel: KmeWhiteboardContentViewModel by inject()

    private var _binding: FragmentSlidesContentBinding? = null
    private val binding get() = _binding!!

    private val content: SetActiveContentPayload? by lazy { arguments?.getParcelable(CONTENT_PAYLOAD) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSlidesContentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        content?.let {
            setContentPayload(it)
        } ?: run {
            Snackbar.make(binding.root, R.string.error_active_content, Snackbar.LENGTH_SHORT).show()
        }
        setupViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewModel() {
        slidesContentViewModel.slideChangedLiveData.observe(
            viewLifecycleOwner,
            slideChangedObserver
        )
        slidesContentViewModel.annotationStateChangedLiveData.observe(
            viewLifecycleOwner,
            annotationStateChangedObserver
        )
        slidesContentViewModel.youModeratorLiveData.observe(
            viewLifecycleOwner,
            showSlidesPreviewObserver
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

    private fun setContentPayload(contentPayload: SetActiveContentPayload) {
        contentPayload.let { payload ->
            val config = KmeSlidesView.Config(
                payload,
                slidesContentViewModel.getCookie(),
                slidesContentViewModel.getFilesUrl()
            ).apply {
                isAnnotationsEnabled = slidesContentViewModel.isAnnotationsEnabled()
                currentSlide = payload.metadata.currentSlide ?: 0
                showPreview = slidesContentViewModel.userType() != KmeUserType.GUEST
            }
            binding.slidesView.init(config)
        }
    }

    private val slideChangedObserver = Observer<Int> {
        binding.slidesView.toSlide(it)
    }

    private val annotationStateChangedObserver = Observer<Boolean> {
        binding.slidesView.enableAnnotations(it)
    }

    private val showSlidesPreviewObserver = Observer<Boolean> {
        if (it) {
            binding.slidesView.showPreview()
        } else {
            binding.slidesView.hidePreview()
        }
    }

    private val whiteboardPageDataObserver = Observer<List<WhiteboardPayload.Drawing>> {
        binding.slidesView.setDrawings(it)
    }

    private val whiteboardPageClearedObserver = Observer<Nothing> {
        binding.slidesView.removeDrawings()
    }

    private val receiveDrawingObserver = Observer<WhiteboardPayload.Drawing> {
        binding.slidesView.addDrawing(it)
    }

    private val receivedLaserPositionObserver = Observer<PointF> {
        binding.slidesView.updateLaserPosition(it)
    }

    private val hideLaserObserver = Observer<Nothing> {
        binding.slidesView.hideLaser()
    }

    private val deleteDrawingObserver = Observer<String> {
        binding.slidesView.removeDrawing(it)
    }

    private val backgroundTypeChangedObserver = Observer<KmeWhiteboardBackgroundType> {
        binding.slidesView.updateBackground(it)
    }

    private val setActivePageObserver = Observer<String> {
        binding.slidesView.setActivePage(it)
    }

    companion object {
        const val CONTENT_PAYLOAD = "CONTENT_PAYLOAD"

        @JvmStatic
        fun newInstance(
            payload: SetActiveContentPayload
        ) = KmeSlidesContentFragment().apply {
            arguments = Bundle().apply {
                putParcelable(CONTENT_PAYLOAD, payload)
            }
        }
    }

}
