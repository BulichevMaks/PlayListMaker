package com.myproject.playlistmaker.search.domain.madel


sealed class SearchResult<T> (val data: T? = null, val message: String? = null, val code: Int) {
    class Success<T>(data: T, code: Int) : SearchResult<T>(data, code = code)
    class Error<T>(message: String, data: T? = null, code: Int) :
        SearchResult<T>(data, message, code)
}
