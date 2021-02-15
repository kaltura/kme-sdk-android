package com.kme.kaltura.kmeapplication.view.fragment.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.viewmodel.content.ActiveContentViewModel
import com.kme.kaltura.kmesdk.content.slides.KmeSlidesView
import com.kme.kaltura.kmesdk.ws.message.module.KmeActiveContentModuleMessage.SetActiveContentPayload
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.fragment_slides_content.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SlidesContentFragment : Fragment() {

    private val activeContentViewModel: ActiveContentViewModel by sharedViewModel()

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
    }

    private val setActiveContentObserver = Observer<SetActiveContentPayload> {
        it.metadata.slides?.let { slides ->
            val config = KmeSlidesView.Config(
                activeContentViewModel.getCookie(),
                activeContentViewModel.getFilesUrl()
            ).apply {
                currentSlide = it.metadata.currentSlide ?: 0
            }

            slidesView.init(config)
            slidesView.setSlides(slides)
        } ?: run {
            Snackbar.make(root, R.string.error_active_content, Snackbar.LENGTH_SHORT).show()
        }
    }

    private val slideChangedObserver = Observer<Int> {
        slidesView.toSlide(it)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SlidesContentFragment()
    }
}
