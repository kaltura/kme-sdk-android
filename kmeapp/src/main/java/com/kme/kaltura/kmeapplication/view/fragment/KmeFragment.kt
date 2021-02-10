package com.kme.kaltura.kmeapplication.view.fragment

import androidx.fragment.app.Fragment
import com.kme.kaltura.kmeapplication.view.activity.KmeActivity

abstract class KmeFragment : Fragment() {

    fun toolbarTitle(title: String) {
        (activity as KmeActivity?)?.toolbarTitle(title)
    }

    fun showHomeButton() {
        (activity as KmeActivity?)?.showHomeButton()
    }

    fun hideHomeButton() {
        (activity as KmeActivity?)?.hideHomeButton()
    }

}