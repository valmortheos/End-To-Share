package com.valmortheos.endtoshare.core.p2p.api

import com.valmortheos.endtoshare.core.model.Device

interface ConnectionNegotiator {
    suspend fun negotiateConnection(device: Device): Result<Transport>
}
