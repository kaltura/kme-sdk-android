package com.kme.kaltura.kmeapplication.view.activity

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.parseRoomAlias
import com.kme.kaltura.kmeapplication.view.activity.RoomsListActivity.Companion.openRoomsListActivity
import com.kme.kaltura.kmeapplication.view.activity.SignInActivity.Companion.openSignInActivity
import com.kme.kaltura.kmesdk.KME
import com.kme.kaltura.kmesdk.rest.KmeApiException
import kotlinx.android.synthetic.main.activity_sign_in.root
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    private val kme: KME by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        animationView.playAnimation()

        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                kme.initSDK(
                    applicationContext,
                    success = {
                        handleDeepLink()
                    },
                    error = {
                        Snackbar.make(root, R.string.error, Snackbar.LENGTH_SHORT).show()
                        GlobalScope.launch(Dispatchers.Main) {
                            delay(200L)
                            if (it is KmeApiException.HttpException && it.errorCode == 401) {
                                openSignInActivity()
                            }
                        }
                    })
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
    }

    private fun handleDeepLink() {
        val roomId = intent?.dataString.parseRoomAlias()

        if (kme.userController.isLoggedIn()) {
            openRoomsListActivity(roomId)
        } else {
            openSignInActivity(roomId)
        }
        finish()
    }

    companion object {
        fun Activity.openSplashActivity() {
            val intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            startActivity(intent)
        }
    }

}
