package com.myproject.playlistmaker.medialibrary.data.api

import android.content.Context
import android.net.Uri

interface InternalStorageClient {
     fun saveImageToPrivateStorage(uri: Uri, context: Context): Uri?
}