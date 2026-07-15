package com.valmortheos.endtoshare.core.p2p.transfer

import com.valmortheos.endtoshare.core.model.FileTransfer
import com.valmortheos.endtoshare.core.p2p.api.TransferManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTransferManager @Inject constructor() : TransferManager {
    private val _transfers = MutableStateFlow<List<FileTransfer>>(emptyList())

    override fun getTransfers(): Flow<List<FileTransfer>> = _transfers

    override suspend fun sendFile(transfer: FileTransfer, stream: InputStream): Result<Unit> {
        // TODO: Implement streaming chunk loop logic
        return Result.success(Unit)
    }

    override suspend fun receiveFile(transferId: String, stream: OutputStream): Result<Unit> {
        // TODO: Implement stream receiving logic
        return Result.success(Unit)
    }

    override suspend fun cancelTransfer(transferId: String) {
        // TODO: Cancel transfer
    }
}
