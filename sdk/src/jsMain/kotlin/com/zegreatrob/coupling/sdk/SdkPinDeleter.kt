package com.zegreatrob.coupling.sdk

import com.zegreatrob.coupling.model.pin.PinDeleter
import com.zegreatrob.coupling.model.tribe.TribeId
import kotlinx.coroutines.asDeferred
import kotlin.js.Promise

interface SdkPinDeleter : PinDeleter, AxiosSyntax {
    override suspend fun deletePin(tribeId: TribeId, pinId: String): Boolean {
        axios.delete("/api/${tribeId.value}/pins/$pinId")
            .unsafeCast<Promise<Unit>>()
            .asDeferred()
            .await()
        return true
    }
}