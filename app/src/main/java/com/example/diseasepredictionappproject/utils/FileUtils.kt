package com.example.diseasepredictionappproject.utils

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }
        inputStream?.close()
        return tempFile
    }
}