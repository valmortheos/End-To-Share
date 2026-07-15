package com.valmortheos.endtoshare.core.model

data class Session(
    val id: String,
    val device: Device,
    val state: SessionState,
    val pin: String?
)

enum class SessionState {
    DISCOVERED,
    NEGOTIATING,
    CONNECTED,
    DISCONNECTED,
    FAILED
}
