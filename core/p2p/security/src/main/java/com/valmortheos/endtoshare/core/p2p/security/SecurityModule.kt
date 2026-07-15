package com.valmortheos.endtoshare.core.p2p.security

import com.valmortheos.endtoshare.core.p2p.api.SecurityManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {
    @Binds
    abstract fun bindSecurityManager(impl: DefaultSecurityManager): SecurityManager
}
