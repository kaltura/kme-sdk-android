package com.kme.kaltura.kmeapplication.view.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kme.kaltura.kmeapplication.util.extensions.hideKeyboard

abstract class KmeActivity : AppCompatActivity() {

    fun setCustomToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    fun toolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun showHomeButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun hideHomeButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
