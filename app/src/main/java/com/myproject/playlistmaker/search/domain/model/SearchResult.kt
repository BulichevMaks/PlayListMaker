package com.myproject.playlistmaker.search.domain.model
sealed class SearchResult<T> (val data: T? = null, val message: String? = null) {
    class Success<T>(data: T, message: String?) : SearchResult<T>(data, message = message)
    class Error<T>( data: T? = null, message: String?) : SearchResult<T>(data, message)
}
