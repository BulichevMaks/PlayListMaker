package com.myproject.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.myproject.playlistmaker.search.data.api.NetworkClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

class RetrofitNetworkClientImpl(private val context: Context):
    NetworkClient {
    private val baseUrl = "https://itunes.apple.com"
    private val interceptor = HttpLoggingInterceptor()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor)
        .build()
    private val retrofit = Retrofit.Builder().client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApi::class.java)

    override fun doRequest(dto: Any): Response {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
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