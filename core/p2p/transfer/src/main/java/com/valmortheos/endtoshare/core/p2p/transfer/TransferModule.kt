package com.valmortheos.endtoshare.core.p2p.transfer

import com.valmortheos.endtoshare.core.p2p.api.TransferManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TransferModule {
    @Binds
    abstract fun bindTransferManager(impl: DefaultTransferManager): TransferManager
}
