package com.valmortheos.endtoshare.core.model

data class FileTransfer(
    val id: String,
    val fileName: String,
    val sizeBytes: Long,
    val bytesTransferred: Long,
    val state: TransferState
)

enum class TransferState {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}
