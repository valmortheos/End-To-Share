package com.valmortheos.endtoshare.core.p2p.api

import android.net.Uri

interface FileManager {
    suspend fun getFileDetails(uri: Uri): FileDetails?
    suspend fun createOutputFile(name: String): Uri?
}

data class FileDetails(val name: String, val size: Long)
