package com.valmortheos.endtoshare.core.p2p.api

import com.valmortheos.endtoshare.core.model.Device
import kotlinx.coroutines.flow.Flow

interface NfcDiscoveryManager {
    val discoveredDevices: Flow<Device>
    suspend fun startDiscovery()
    suspend fun stopDiscovery()
    suspend fun advertise(device: Device)
    suspend fun stopAdvertising()
}
