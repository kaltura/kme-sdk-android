package com.kme.kaltura.kmeapplication.view.fragment.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmeapplication.viewmodel.content.DesktopShareViewModel
import kotlinx.android.synthetic.main.fragment_desktop_share_content.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DesktopShareFragment : Fragment() {

    private val viewModel: DesktopShareViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_desktop_share_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupViewModel()
    }

    private fun setupUI() {
        desktopShareRenderer.setZOrderMediaOverlay(true)
    }

    private fun setupViewModel() {
        viewModel.isDesktopShareActiveLiveData.observe(viewLifecycleOwner, desktopShareActiveObserver)
        viewModel.desktopShareHDQualityLiveData.observe(viewLifecycleOwner, desktopShareHDQualityObserver)
        viewModel.listenDesktopShare(desktopShareRenderer)
    }

    private val desktopShareActiveObserver = Observer<Boolean> { isActive->
        if (isActive) {
            desktopShareRenderer.visible()
        } else {
            desktopShareRenderer.gone()
        }
    }

    private val desktopShareHDQualityObserver = Observer<Boolean> { isHD->

    }

    companion object {
        @JvmStatic
        fun newInstance() = DesktopShareFragment()
    }

}
