package com.myproject.playlistmaker.medialibrary.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.myproject.playlistmaker.medialibrary.data.api.InternalStorageClient
import java.io.File
import java.io.FileOutputStream


class InternalStorageClientImpl(val context: Context):InternalStorageClient {

    override fun saveImageToPrivateStorage(uri: Uri, context: Context): Uri? {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return Uri.fromFile(file)
    }
}