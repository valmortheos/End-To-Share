package com.valmortheos.endtoshare.core.p2p.file

import android.net.Uri
import com.valmortheos.endtoshare.core.p2p.api.FileDetails
import com.valmortheos.endtoshare.core.p2p.api.FileManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultFileManager @Inject constructor() : FileManager {
    override suspend fun getFileDetails(uri: Uri): FileDetails? {
        // Implementation logic
        return null
    }

    override suspend fun createOutputFile(name: String): Uri? {
        // Implementation logic
        return null
    }
}
