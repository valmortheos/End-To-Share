package com.valmortheos.endtoshare.core.p2p.security

import com.valmortheos.endtoshare.core.p2p.api.SecurityManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSecurityManager @Inject constructor() : SecurityManager {
    override suspend fun generatePin(): String {
        return (100000..999999).random().toString()
    }

    override suspend fun verifyPin(pin: String): Boolean {
        // Implementation
        return true
    }
}
