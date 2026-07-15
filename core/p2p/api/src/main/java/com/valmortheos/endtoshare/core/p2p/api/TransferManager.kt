package com.valmortheos.endtoshare.core.p2p.api

import com.valmortheos.endtoshare.core.model.FileTransfer
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.io.OutputStream

interface TransferManager {
    fun getTransfers(): Flow<List<FileTransfer>>
    suspend fun sendFile(transfer: FileTransfer, stream: InputStream): Result<Unit>
    suspend fun receiveFile(transferId: String, stream: OutputStream): Result<Unit>
    suspend fun cancelTransfer(transferId: String)
}
