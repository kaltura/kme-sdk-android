package com.kme.kaltura.kmeapplication.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kme.kaltura.kmeapplication.R

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    companion object {
        fun Context.openSignUpActivity() {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

}
