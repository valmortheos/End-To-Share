package com.valmortheos.endtoshare.core.p2p.session

import com.valmortheos.endtoshare.core.p2p.api.SessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
    @Binds
    abstract fun bindSessionManager(impl: DefaultSessionManager): SessionManager
}
