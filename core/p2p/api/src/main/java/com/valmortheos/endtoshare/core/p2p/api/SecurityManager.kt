package com.valmortheos.endtoshare.core.p2p.api

interface SecurityManager {
    suspend fun generatePin(): String
    suspend fun verifyPin(pin: String): Boolean
}
