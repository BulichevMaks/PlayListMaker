package com.myproject.playlistmaker.search.data.api

import com.myproject.playlistmaker.search.data.network.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}