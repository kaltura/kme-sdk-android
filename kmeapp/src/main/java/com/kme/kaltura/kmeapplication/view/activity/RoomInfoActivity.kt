package com.kme.kaltura.kmeapplication.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.toNonNull
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmeapplication.view.activity.RoomActivity.Companion.openRoomActivity
import com.kme.kaltura.kmeapplication.viewmodel.RoomInfoViewModel
import com.kme.kaltura.kmesdk.rest.response.room.KmeBaseRoom
import kotlinx.android.synthetic.main.activity_room_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RoomInfoActivity : KmeActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: RoomInfoViewModel by viewModel()
    private val roomAlias: String by lazy { intent?.getStringExtra(ROOM_ALIAS_EXTRA).toNonNull() }
    private var toolbarTitle: String? = null

    private var room: KmeBaseRoom? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_info)

        setupUI()
        setupViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_room_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_join_room -> {
                room?.let {
                    openRoomActivity(
                        it.companyId ?: 0,
                        it.id ?: 0,
                        it.alias.toNonNull()
                    )
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun setupViewModel() {
        viewModel.fetchRoomInfo(roomAlias)

        viewModel.isLoadingLiveData.observe(this, isLoadingObserver)
        viewModel.roomInfoLiveData.observe(this, roomObserver)
        viewModel.roomInfoErrorLiveData.observe(this, roomInfoErrorObserver)
    }

    private fun setupUI() {
        toolbarTitle = intent?.getStringExtra(TOOLBAR_TITLE_EXTRA)

        setupToolbar()
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setupToolbar() {
        toolbarTitle(toolbarTitle ?: getString(R.string.app_name))
        showHomeButton()
    }

    override fun onRefresh() {
        viewModel.fetchRoomInfo(roomAlias)
        swipeRefreshLayout.isRefreshing = false
    }

    private val isLoadingObserver = Observer<Boolean> {
        if (it) {
            progressBar.visible()
        } else {
            progressBar.gone()
        }
    }

    private val roomObserver = Observer<KmeBaseRoom> {
        room = it
        etRoomName.setText(it.name)
        etRoomSummary.setText(it.summary)
        etRoomDescription.setText(it.description)
        if (toolbarTitle == null) {
            toolbarTitle = it.name
            setupToolbar()
        }
    }

    private val roomInfoErrorObserver = Observer<String?> {
        Snackbar.make(swipeRefreshLayout, R.string.error, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val ROOM_ALIAS_EXTRA = "ROOM_ALIAS_EXTRA"
        private const val TOOLBAR_TITLE_EXTRA = "TOOLBAR_TITLE_EXTRA"

        fun Context.openRoomInfoActivity(roomAlias: String, toolbarTitle: String? = null) {
            val intent = Intent(this, RoomInfoActivity::class.java)
            intent.putExtra(ROOM_ALIAS_EXTRA, roomAlias)
            intent.putExtra(TOOLBAR_TITLE_EXTRA, toolbarTitle)
            startActivity(intent)
        }
    }

}
