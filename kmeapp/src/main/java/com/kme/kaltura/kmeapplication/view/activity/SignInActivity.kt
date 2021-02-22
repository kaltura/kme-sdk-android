package com.kme.kaltura.kmeapplication.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmeapplication.view.activity.RoomActivity.Companion.openRoomActivity
import com.kme.kaltura.kmeapplication.view.activity.RoomsListActivity.Companion.openRoomsListActivity
import com.kme.kaltura.kmeapplication.viewmodel.SignInViewModel
import com.kme.kaltura.kmesdk.rest.response.signin.KmeGuestLoginResponse.KmeGuestLoginData
import com.kme.kaltura.kmesdk.rest.response.signin.KmeLoginResponse.KmeLoginData
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess

class SignInActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by viewModel()
    private val roomAlias: String? by lazy { intent?.getStringExtra(ROOM_ALIAS_EXTRA) }
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var isLoginViewType = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        uiScope.launch {
            delay(100)
            startAnimation()
        }

        setupViewModel()
        setupUI()
    }

    private fun startAnimation() {

        fun scaleView(v: View, startScale: Float, endScale: Float) {
            val anim: Animation = ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim.fillAfter = true
            anim.duration = 500
            v.startAnimation(anim)
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(root)
        constraintSet.setVerticalBias(R.id.animationView, 0.1f)
        val transition = ChangeBounds()
        transition.duration = 500
        transition.interpolator = AccelerateDecelerateInterpolator()
        TransitionManager.beginDelayedTransition(root, transition)
        constraintSet.applyTo(root)
        scaleView(animationView, 1f, 0.9f)

        signInContainer.alpha = 0.0f
        signInContainer.animate().apply {
            interpolator = LinearInterpolator()
            duration = 500
            alpha(1f)
            startDelay = 500
            start()
        }
    }

    private fun setupViewModel() {
        viewModel.isLoadingLiveData.observe(this, isLoadingObserver)

        viewModel.loginResponseLiveData.observe(this, loginObserver)
        viewModel.guestLoginResponseLiveData.observe(this, guestLoginObserver)
        viewModel.loginErrorLiveData.observe(this, loginErrorObserver)
    }

    private fun setupUI() {
        btnLogin.setOnClickListener {
            if (isLoginViewType) {
                viewModel.login(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
            } else {
                viewModel.guest(
                    etName.text.toString(),
                    etEmail.text.toString(),
                    roomAlias.toString()
                )
            }
        }

        if (!roomAlias.isNullOrEmpty()) {
            btnSwitchLogin.visible()
        } else {
            btnSwitchLogin.gone()
        }

        btnSwitchLogin.setOnClickListener {
            if (isLoginViewType) {
                btnSwitchLogin.text = getString(R.string.join_as_user)
                ilName.visible()
                ilPassword.gone()
            } else {
                btnSwitchLogin.text = getString(R.string.join_as_guest)
                ilName.gone()
                ilPassword.visible()
            }

            isLoginViewType = !isLoginViewType
        }
    }

    private val isLoadingObserver = Observer<Boolean> {
        if (it) {
            progressBar.visible()
        } else {
            progressBar.gone()
        }
    }

    private val loginObserver = Observer<KmeLoginData> {
        openRoomsListActivity()
    }

    private val guestLoginObserver = Observer<KmeGuestLoginData> {
        openRoomActivity(roomAlias.toString())
    }

    private val loginErrorObserver = Observer<String?> {
        it?.let {
            Snackbar.make(root, it, Snackbar.LENGTH_SHORT).show()
        } ?: run {
            Snackbar.make(root, R.string.error, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ROOM_ALIAS_EXTRA = "ROOM_ALIAS_EXTRA"

        fun Activity.openSignInActivity(roomAlias: String? = null) {
            val intent = Intent(this, SignInActivity::class.java)
            if (roomAlias != null) {
                intent.putExtra(ROOM_ALIAS_EXTRA, roomAlias)
            }
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_NO_ANIMATION
            )
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onBackPressed() {
        finish()
        exitProcess(0)
    }
}
