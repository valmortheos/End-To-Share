package com.valmortheos.endtoshare.core.p2p.wifidirect

import com.valmortheos.endtoshare.core.model.Device
import com.valmortheos.endtoshare.core.p2p.api.Transport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiDirectTransport @Inject constructor() : Transport {
    private val _isAvailable = MutableStateFlow(false)
    override val isAvailable: Flow<Boolean> = _isAvailable

    override suspend fun connect(device: Device): Result<Unit> {
        // TODO: Implement connection logic
        return Result.success(Unit)
    }

    override suspend fun disconnect() {
        // TODO: Implement disconnection logic
    }

    override fun getSupportedDevices(): Flow<List<Device>> {
        return MutableStateFlow(emptyList())
    }
}
