package com.kme.kaltura.kmeapplication.di

import com.kme.kaltura.kmeapplication.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModel = module {

    viewModel { SignInViewModel(get(), androidContext()) }
    viewModel { UserCompaniesViewModel(get(), get()) }
    viewModel { RoomsListViewModel(get()) }
    viewModel { RoomInfoViewModel(get()) }
    viewModel { RoomStateViewModel(get()) }
    viewModel { RoomRecordingViewModel(get()) }
    viewModel { RoomNoteViewModel(get(), get()) }
    viewModel { RoomSettingsViewModel(get()) }
    viewModel { PeerConnectionViewModel(get()) }
    viewModel { ConnectionPreviewViewModel(get()) }
    viewModel { RoomRenderersViewModel(get()) }
    viewModel { ParticipantsViewModel(get(), get()) }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { ConversationsViewModel(get(), get()) }

}
