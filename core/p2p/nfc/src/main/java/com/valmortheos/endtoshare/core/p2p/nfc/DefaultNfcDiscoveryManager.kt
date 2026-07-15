package com.valmortheos.endtoshare.core.p2p.nfc

import com.valmortheos.endtoshare.core.model.Device
import com.valmortheos.endtoshare.core.p2p.api.NfcDiscoveryManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNfcDiscoveryManager @Inject constructor() : NfcDiscoveryManager {
    private val _discoveredDevices = MutableSharedFlow<Device>()
    override val discoveredDevices: Flow<Device> = _discoveredDevices

    override suspend fun startDiscovery() {
        // TODO: Implement HCE / Reader mode
    }

    override suspend fun stopDiscovery() {
        // TODO: Stop NFC
    }

    override suspend fun advertise(device: Device) {
        // TODO: Implement HCE broadcasting
    }

    override suspend fun stopAdvertising() {
        // TODO: Stop HCE
    }
}
