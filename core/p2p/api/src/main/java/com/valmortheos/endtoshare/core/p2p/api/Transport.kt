package com.valmortheos.endtoshare.core.p2p.api

import kotlinx.coroutines.flow.Flow
import com.valmortheos.endtoshare.core.model.Device

interface Transport {
    val isAvailable: Flow<Boolean>
    suspend fun connect(device: Device): Result<Unit>
    suspend fun disconnect()
    fun getSupportedDevices(): Flow<List<Device>>
}
