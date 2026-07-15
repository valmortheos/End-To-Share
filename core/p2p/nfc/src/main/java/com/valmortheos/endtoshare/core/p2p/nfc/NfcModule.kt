package com.valmortheos.endtoshare.core.p2p.nfc

import com.valmortheos.endtoshare.core.p2p.api.NfcDiscoveryManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NfcModule {
    @Binds
    abstract fun bindNfcDiscoveryManager(impl: DefaultNfcDiscoveryManager): NfcDiscoveryManager
}
