package com.myproject.playlistmaker.search.ui.viewmodel

import android.provider.Settings.Global.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.myproject.playlistmaker.App
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.search.di.Creator
import com.myproject.playlistmaker.search.domain.usecase.*

class SearchViewModelFactory : ViewModelProvider.Factory {

    private val searchRepository by lazy {
        Creator.getSearchRepository()
    }

    private val itemClickUseCase by lazy {
        ItemClickUseCase(searchRepository)
    }
    private val searchTracksUseCase by lazy {
        SearchTracksUseCase(searchRepository)
    }
    private val itemHistoryClickUseCase by lazy {
        ItemHistoryClickUseCase(searchRepository)
    }
    private val showHistoryUseCase by lazy {
        ShowHistoryUseCase(searchRepository)
    }
    private val stopActivityUseCase by lazy {
        StopActivityUseCase(searchRepository)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras,): T {
        val application = checkNotNull(extras[APPLICATION_KEY])
        return SearchViewModel(
            itemClickUseCase,
            searchTracksUseCase,
            itemHistoryClickUseCase,
            showHistoryUseCase,
            stopActivityUseCase,
            (application as App),
        ) as T
    }
}