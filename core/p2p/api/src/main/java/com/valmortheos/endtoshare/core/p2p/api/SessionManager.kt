package com.valmortheos.endtoshare.core.p2p.api

import com.valmortheos.endtoshare.core.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    val currentSession: Flow<Session?>
    suspend fun startSession(session: Session)
    suspend fun endSession()
}
