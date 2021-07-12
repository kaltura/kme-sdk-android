package com.kme.kaltura.kmesdk.content.desktop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.controller.room.IKmeSettingsModule
import com.kme.kaltura.kmesdk.databinding.FragmentDesktopShareContentBinding
import com.kme.kaltura.kmesdk.gone
import com.kme.kaltura.kmesdk.setVisibility
import com.kme.kaltura.kmesdk.util.livedata.ConsumableValue
import com.kme.kaltura.kmesdk.visible
import com.kme.kaltura.kmesdk.webrtc.view.KmeSurfaceRendererView
import org.koin.android.ext.android.inject

/**
 * Implementation for desktop shared content
 */
internal class KmeDesktopShareFragment : KmeContentView() {

    private val viewModel: KmeDesktopShareViewModel by inject()
    private val settingsModule: IKmeSettingsModule by inject()

    private var _binding: FragmentDesktopShareContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDesktopShareContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            setupViewModel()
        }
        setupAdminControls()
    }

    private fun setupAdminControls() = with(binding) {
        startShare.setOnClickListener {
            viewModel.askForScreenSharePermission()
        }

        stopShare.setOnClickListener {
            viewModel.stopScreenShare()
            publisherGroup.visible()
            stopShare.gone()
        }

        closeView.setOnClickListener {
            viewModel.stopScreenShare()
            viewModel.setConferenceView()
        }
    }

    private fun setupViewModel() {
        viewModel.isAdminLiveData.observe(
            viewLifecycleOwner,
            adminObserver
        )
        viewModel.isDesktopShareActiveLiveData.observe(
            viewLifecycleOwner,
            desktopShareActiveObserver
        )
        viewModel.isDesktopShareAvailableLiveData.observe(
            viewLifecycleOwner,
            desktopShareAvailableObserver
        )
        viewModel.desktopShareHDQualityLiveData.observe(
            viewLifecycleOwner,
            desktopShareHDQualityObserver
        )
        viewModel.listenDesktopShare()

        val isActive = viewModel.isDesktopShareActiveLiveData.value?.first ?: false
        val isYour = viewModel.isDesktopShareActiveLiveData.value?.second ?: false
        if (isActive) {
            if (isYour) {
                viewModel.changeScreenShareRenderer(binding.desktopShareRenderer)
            } else {
                viewModel.changeViewerRenderer(binding.desktopShareRenderer)
            }
        }

        settingsModule.moderatorStateLiveData.observe(
            viewLifecycleOwner,
            moderatorStateObserver
        )
    }

    private val adminObserver = Observer<Boolean> { isAdmin ->
        updateRoleDependUI(isAdmin)
    }

    private val desktopShareActiveObserver = Observer<Pair<Boolean, Boolean>> { result ->
        val isAdmin = viewModel.isAdminLiveData.value == true
        val isActive = result.first
        val isYour = result.second

        updateRoleDependUI(isAdmin, isActive, isYour)
    }

    private fun updateRoleDependUI(
        isAdmin: Boolean,
        isActive: Boolean = false,
        isYour: Boolean = false
    ) {
        if (isAdmin) {
            showPublisherUI(isActive, isYour)
        } else {
            showViewerUI(isActive)
        }
    }

    private fun showPublisherUI(
        isActive: Boolean,
        isYour: Boolean
    ) = with(binding) {
        if (isActive) {
            viewerGroup.gone()
            publisherGroup.gone()
            desktopShareRenderer.visible()
            stopShare.setVisibility(isYour)
        } else {
            publisherGroup.visible()
            viewerGroup.gone()
            stopShare.gone()
            desktopShareRenderer.gone()
        }
        closeView.visible()
    }

    private fun showViewerUI(isActive: Boolean) = with(binding) {
        if (isActive) {
            closeView.gone()
            desktopShareRenderer.visible()
        } else {
            viewerGroup.visible()
            publisherGroup.gone()
            desktopShareRenderer.gone()
        }
        closeView.gone()
    }

    private val desktopShareAvailableObserver = Observer<ConsumableValue<Boolean>> {
        it.consume {
            binding.desktopShareRenderer.visible()
            viewModel.startView(binding.desktopShareRenderer)
        }
    }

    private val desktopShareHDQualityObserver = Observer<Boolean> { isHD ->

    }

    private val moderatorStateObserver = Observer<Boolean> { isModerator ->
        val result = viewModel.isDesktopShareActiveLiveData.value
        val isActive = result?.first ?: false
        val isYour = result?.second ?: false
        updateRoleDependUI(isModerator, isActive, isYour)

        if (!isModerator) {
            viewModel.stopScreenShare()
        }
    }

    fun onGetRenderer(callback: (view: KmeSurfaceRendererView) -> Unit) {
        callback.invoke(binding.desktopShareRenderer)
    }

    fun onScreenSharePermission(approved: Boolean) = with(binding) {
        stopShare.setVisibility(approved)
        desktopShareRenderer.setVisibility(approved)
        publisherGroup.gone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearRenderer(binding.desktopShareRenderer)
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = KmeDesktopShareFragment()
    }

}
