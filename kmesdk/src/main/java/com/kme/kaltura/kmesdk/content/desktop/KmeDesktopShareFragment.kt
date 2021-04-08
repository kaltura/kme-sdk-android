package com.kme.kaltura.kmesdk.content.desktop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.kme.kaltura.kmesdk.content.KmeContentView
import com.kme.kaltura.kmesdk.databinding.FragmentDesktopShareContentBinding
import com.kme.kaltura.kmesdk.gone
import com.kme.kaltura.kmesdk.visible
import org.koin.android.ext.android.inject

class KmeDesktopShareFragment : KmeContentView() {

    private val viewModel: KmeDesktopShareViewModel by inject()

    private var _binding: FragmentDesktopShareContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDesktopShareContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
    }

    private fun setupViewModel() {
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
        viewModel.listenDesktopShare(binding.desktopShareRenderer)
    }

    private val desktopShareActiveObserver = Observer<Boolean> { isActive ->
        if (isActive) {
            binding.textGroup.gone()
        } else {
            binding.textGroup.visible()
            binding.desktopShareRenderer.gone()
        }
    }

    private val desktopShareAvailableObserver = Observer<Nothing> {
        binding.desktopShareRenderer.visible()
    }

    private val desktopShareHDQualityObserver = Observer<Boolean> { isHD ->

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = KmeDesktopShareFragment()
    }

}
