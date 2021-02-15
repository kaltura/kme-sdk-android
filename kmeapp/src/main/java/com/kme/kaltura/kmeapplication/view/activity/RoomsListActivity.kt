package com.kme.kaltura.kmeapplication.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmeapplication.util.extensions.alert
import com.kme.kaltura.kmeapplication.util.extensions.gone
import com.kme.kaltura.kmeapplication.util.extensions.toNonNull
import com.kme.kaltura.kmeapplication.util.extensions.visible
import com.kme.kaltura.kmeapplication.view.activity.RoomActivity.Companion.openRoomActivity
import com.kme.kaltura.kmeapplication.view.activity.SignInActivity.Companion.openSignInActivity
import com.kme.kaltura.kmeapplication.view.adapter.RoomsListAdapter
import com.kme.kaltura.kmeapplication.view.adapter.UserCompaniesAdapter
import com.kme.kaltura.kmeapplication.viewmodel.RoomsListViewModel
import com.kme.kaltura.kmesdk.rest.response.room.KmeBaseRoom
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserCompany
import kotlinx.android.synthetic.main.activity_rooms_list.*
import kotlinx.android.synthetic.main.toolbar_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RoomsListActivity : KmeActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: RoomsListViewModel by viewModel()
    private val roomAlias: String? by lazy { intent?.getStringExtra(ROOM_ALIAS_EXTRA) }
    private val toolbarTitle: String by lazy {
        intent?.getStringExtra(TOOLBAR_TITLE_EXTRA).toNonNull(getString(R.string.app_name))
    }

    private lateinit var roomsAdapter: RoomsListAdapter
    private lateinit var companiesAdapter: UserCompaniesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms_list)

        setupUI()
        setupViewModel()
        handleDeepLinkData()
    }

    private fun setupViewModel() {
        viewModel.fetchUserCompanies()

        viewModel.isLoadingLiveData.observe(this, isLoadingObserver)

        viewModel.roomsListLiveData.observe(this, roomsListObserver)
        viewModel.roomsListErrorLiveData.observe(this, roomsListErrorObserver)

        viewModel.openRoomByLinkLiveData.observe(this, openRoomByLinkObserver)
        viewModel.openRoomByLinkErrorLiveData.observe(this, openRoomByLinkErrorObserver)

        viewModel.selectedCompanyLiveData.observe(this, selectedCompanyObserver)
        viewModel.userCompaniesLiveData.observe(this, companiesListObserver)
        viewModel.userCompaniesErrorLiveData.observe(this, companiesListErrorObserver)
    }

    private fun setupUI() {
        setupToolbar()
        setupRoomLinkContainer()
        setupCompaniesDropDown()

        swipeRefreshLayout.setOnRefreshListener(this)

        roomsAdapter = RoomsListAdapter()
        roomsAdapter.onRoomClick = onRoomClick

        rvRooms.setHasFixedSize(true)
        rvRooms.layoutManager = LinearLayoutManager(this)
        rvRooms.adapter = roomsAdapter
    }

    private fun setupToolbar() {
        setCustomToolbar(toolbar)
        hideHomeButton()
        btnLogout.setOnClickListener {
            alert(R.string.logout, R.string.logout_message) {
                positiveButton(android.R.string.ok) {
                    viewModel.logout()
                    openSignInActivity()
                }

                negativeButton(android.R.string.cancel)
            }.show()
        }
    }

    private fun setupCompaniesDropDown() {
        companiesAdapter = UserCompaniesAdapter(this)
        companiesAdapter.onCompanyClick = onCompanyClick
        companiesDropDownList.setAdapter(companiesAdapter)
    }

    private fun setupRoomLinkContainer() {
        btnOpenLink.setOnClickListener {
            viewModel.openRoomByLink(etRoomLink.text.toString())
        }

        etRoomLink.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_GO) {
                btnOpenLink.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun handleDeepLinkData() {
        roomAlias?.let {
            openRoomActivity(it)
        }
    }

    override fun onRefresh() {
        val selectedCompany = viewModel.selectedCompanyLiveData.value

        selectedCompany?.id?.let {
            viewModel.fetchRoomsList(it)
        } ?: run {
            viewModel.fetchUserCompanies()
        }

        swipeRefreshLayout.isRefreshing = false
    }

    private val onRoomClick: (room: KmeBaseRoom) -> Unit = {
        it.alias?.let { alias -> openRoomActivity(alias) }
    }

    private val onCompanyClick: (room: KmeUserCompany) -> Unit = {
        viewModel.selectCompany(it)
    }

    private val isLoadingObserver = Observer<Boolean> {
        if (it) {
            progressBar.visible()
        } else {
            progressBar.gone()
        }
    }

    private val roomsListObserver = Observer<List<KmeBaseRoom>> {
        roomsAdapter.addData(it)
    }

    private val companiesListObserver = Observer<List<KmeUserCompany>> {
        companiesAdapter.addData(it)
    }

    private val selectedCompanyObserver = Observer<KmeUserCompany> {
        it?.id?.let { id ->
            companiesDropDownList.setText(it.name)
            viewModel.fetchRoomsList(id)
        }
    }

    private val openRoomByLinkObserver = Observer<String> {
        openRoomActivity(it)
    }

    private val openRoomByLinkErrorObserver = Observer<Nothing?> {
        Snackbar.make(root, R.string.not_valid_room_link, Snackbar.LENGTH_SHORT)
            .setTextColor(ContextCompat.getColor(this, R.color.redScale2))
            .show()
    }

    private val roomsListErrorObserver = Observer<String?> {
        Snackbar.make(root, R.string.error, Snackbar.LENGTH_SHORT).show()
    }

    private val companiesListErrorObserver = Observer<String?> {
        Snackbar.make(root, R.string.error, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val ROOM_ALIAS_EXTRA = "ROOM_ALIAS_EXTRA"
        private const val TOOLBAR_TITLE_EXTRA = "TOOLBAR_TITLE_EXTRA"

        fun Context.openRoomsListActivity(roomAlias: String? = null, toolbarTitle: String? = null) {
            val intent = Intent(this, RoomsListActivity::class.java)
            intent.putExtra(TOOLBAR_TITLE_EXTRA, toolbarTitle)
            if (roomAlias != null) {
                intent.putExtra(ROOM_ALIAS_EXTRA, roomAlias)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

}
