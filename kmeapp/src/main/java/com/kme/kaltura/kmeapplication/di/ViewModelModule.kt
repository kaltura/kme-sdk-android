package com.kme.kaltura.kmeapplication.di

import com.kme.kaltura.kmeapplication.viewmodel.*
import com.kme.kaltura.kmeapplication.viewmodel.content.ActiveContentViewModel
import com.kme.kaltura.kmeapplication.viewmodel.content.DesktopShareViewModel
import com.kme.kaltura.kmeapplication.viewmodel.content.WhiteboardContentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModel = module {

    viewModel { SignInViewModel(get()) }
    viewModel { UserCompaniesViewModel(get(), get()) }
    viewModel { RoomsListViewModel(get()) }
    viewModel { RoomInfoViewModel(get()) }
    viewModel { RoomViewModel(get()) }
    viewModel { RoomRecordingViewModel(get()) }
    viewModel { RoomNoteViewModel(get(), get()) }
    viewModel { RoomSettingsViewModel(get()) }
    viewModel { PeerConnectionViewModel(get()) }
    viewModel { RoomRenderersViewModel(get()) }
    viewModel { ParticipantsViewModel(get(), get()) }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { ConversationsViewModel(get(), get()) }
    viewModel { ActiveContentViewModel(get()) }
    viewModel { DesktopShareViewModel(get()) }
    viewModel { WhiteboardContentViewModel(get()) }

}
