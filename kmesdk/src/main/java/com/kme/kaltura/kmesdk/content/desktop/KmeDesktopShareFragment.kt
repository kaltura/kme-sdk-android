package com.kme.kaltura.kmesdk.content.desktop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.module.IKmeSettingsModule
import com.kme.kaltura.kmesdk.databinding.FragmentDesktopShareContentBinding
import com.kme.kaltura.kmesdk.di.scopedInject
import com.kme.kaltura.kmesdk.gone
import com.kme.kaltura.kmesdk.setVisibility
import com.kme.kaltura.kmesdk.visible

/**
 * Implementation for desktop shared content
 */
internal class KmeDesktopShareFragment : KmeContentView() {

    private val settingsModule: IKmeSettingsModule by scopedInject()
    private val viewModel: KmeDesktopShareViewModel by scopedInject()

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

        setupViewModel()
        setupAdminControls()
    }

    private fun setupAdminControls() = with(binding) {
        startShare.setOnClickListener {
            viewModel.askForScreenSharePermission()
        }

        stopShare.setOnClickListener {
            viewModel.stopScreenShare()
//            publisherGroup.visible()
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

        settingsModule.moderatorStateLiveData.observe(
            viewLifecycleOwner,
            moderatorStateObserver
        )
    }

    private val adminObserver = Observer<Boolean> { isAdmin ->
        val state = viewModel.isDesktopShareActiveLiveData.value
        val isActive = state?.first ?: false
        val isYour = state?.second ?: false

        updateRoleDependUI(isAdmin, isActive, isYour)
    }

    private val desktopShareActiveObserver = Observer<Pair<Boolean, Boolean>> { result ->
        val isAdmin = viewModel.isAdminLiveData.value == true
        val isActive = result.first
        val isYour = result.second

        updateRoleDependUI(isAdmin, isActive, isYour)
    }

    private fun updateRoleDependUI(
        isAdmin: Boolean,
        isActive: Boolean,
        isYour: Boolean
    ) {
//        if (isAdmin) {
//            showPublisherUI(isActive, isYour)
//        } else {
            showViewerUI(isActive)
//        }
    }

    private fun showPublisherUI(
        isActive: Boolean,
        isYour: Boolean
    ) = with(binding) {
        if (isActive) {
            viewerGroup.gone()
//            publisherGroup.gone()
            desktopShareRenderer.visible()
            stopShare.setVisibility(isYour)
//            if (isYour) {
//                viewModel.setScreenShareRenderer(desktopShareRenderer)
//            } else {
                viewModel.setViewerRenderer(desktopShareRenderer)
//            }
        } else {
//            publisherGroup.visible()
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
            viewModel.setViewerRenderer(desktopShareRenderer)
        } else {
            viewerGroup.visible()
//            publisherGroup.gone()
            desktopShareRenderer.gone()
        }
        closeView.gone()
    }

    private val desktopShareAvailableObserver = Observer<Boolean> {
        binding.desktopShareRenderer.visible()
        viewModel.setViewerRenderer(binding.desktopShareRenderer)
    }

    private val desktopShareHDQualityObserver = Observer<Boolean> { isHD ->

    }

    private val moderatorStateObserver = Observer<Boolean> { isModerator ->
//        if (viewModel.updateModeratorState(isModerator)) {
//            val state = viewModel.isDesktopShareActiveLiveData.value
//            val isActive = state?.first ?: false
//            val isYour = state?.second ?: false
//            updateRoleDependUI(isModerator, isActive, isYour)
//
//            if (!isModerator) {
//                viewModel.stopScreenShare()
//            }
//        }
    }

    fun onScreenSharePermission(approved: Boolean) = with(binding) {
        stopShare.setVisibility(approved)
        desktopShareRenderer.setVisibility(approved)
//        publisherGroup.gone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingsModule.moderatorStateLiveData.removeObserver(moderatorStateObserver)
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = KmeDesktopShareFragment()
    }

}
