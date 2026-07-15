package com.valmortheos.endtoshare.core.p2p.file

import com.valmortheos.endtoshare.core.p2p.api.FileManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FileModule {
    @Binds
    abstract fun bindFileManager(impl: DefaultFileManager): FileManager
}
