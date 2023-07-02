package com.myproject.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.myproject.playlistmaker.search.data.api.NetworkClient
import java.net.SocketTimeoutException

class RetrofitNetworkClientImpl(
    private val trackService: TrackApi,
    private val context: Context):
    NetworkClient {
    override fun doRequest(dto: Any): Response {

        if (!isConnected()){
            return Response().apply { resultCode = -1 }
        }
        return if (dto is TracksSearchRequest) {
            return try {
                val result = trackService.search(dto.query).execute()
                val body = result.body() ?: Response()
                body.apply { resultCode = result.code() }
            } catch (e: SocketTimeoutException) {
                println("${e.stackTrace}")
                Response()
            }
        } else{
            Response().apply { resultCode = 400 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

}