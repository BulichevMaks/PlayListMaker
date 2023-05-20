package com.myproject.playlistmaker.data.datasource.network

import com.myproject.playlistmaker.data.datasource.api.TracksNetworkStorage
import com.myproject.playlistmaker.data.datasource.models.TrackDataSource
import com.myproject.playlistmaker.data.datasource.models.TracksResultDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TracksNetworkNetworkDataSourceImpl : TracksNetworkStorage {

    private val baseUrl = "https://itunes.apple.com"
    private val interceptor = HttpLoggingInterceptor()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor)
        .build()
    private val retrofit = Retrofit.Builder().client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackService = retrofit.create(TrackApi::class.java)

   // var result: ArrayList<TrackDataSource> = ArrayList()

    override fun getTracksByName(trackName: String, callback: (TracksResultDataSource) -> Unit) {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val tracks: ArrayList<TrackDataSource> = ArrayList()

        trackService.search(trackName).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                when (response.code()) {
                    200 -> {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            callback(TracksResultDataSource.Success(tracks))
                        } else {
                            callback(TracksResultDataSource.Error(-1))
                        }
                    }
                    404 -> callback(TracksResultDataSource.Error(404))
                    401 -> callback(TracksResultDataSource.Error(401))
                    else -> callback(TracksResultDataSource.Error(-1))
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                callback(TracksResultDataSource.Error(500))
            }
        })
    }
}

