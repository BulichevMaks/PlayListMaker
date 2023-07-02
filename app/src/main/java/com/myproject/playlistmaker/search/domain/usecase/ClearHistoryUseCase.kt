package com.myproject.playlistmaker.search.domain.usecase

import com.myproject.playlistmaker.search.domain.api.SearchRepository

class ClearHistoryUseCase(private val searchRepository: SearchRepository) {
    fun execute() {
        searchRepository.clearHistory()
    }
}