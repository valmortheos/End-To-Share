package com.valmortheos.endtoshare.core.p2p.session

import com.valmortheos.endtoshare.core.model.Session
import com.valmortheos.endtoshare.core.p2p.api.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSessionManager @Inject constructor() : SessionManager {
    private val _currentSession = MutableStateFlow<Session?>(null)
    override val currentSession: Flow<Session?> = _currentSession

    override suspend fun startSession(session: Session) {
        _currentSession.value = session
    }

    override suspend fun endSession() {
        _currentSession.value = null
    }
}
