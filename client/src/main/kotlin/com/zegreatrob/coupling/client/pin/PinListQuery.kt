package com.zegreatrob.coupling.client.pin

import com.zegreatrob.coupling.client.Coupling
import com.zegreatrob.coupling.client.getPinListAsync
import com.zegreatrob.coupling.client.getTribeAsync
import com.zegreatrob.coupling.common.Action
import com.zegreatrob.coupling.common.ActionLoggingSyntax
import com.zegreatrob.coupling.common.entity.pin.Pin
import com.zegreatrob.coupling.common.entity.tribe.KtTribe
import com.zegreatrob.coupling.common.entity.tribe.TribeId
import kotlinx.coroutines.await
import kotlin.js.Promise

data class PinListQuery(val tribeId: TribeId, val coupling: Coupling) : Action

interface PinListQueryDispatcher : ActionLoggingSyntax {
    suspend fun PinListQuery.perform() = logAsync { coupling.getData(tribeId) }

    private suspend fun Coupling.getData(tribeId: TribeId) =
            (getTribeAsync(tribeId) to getPinListAsync(tribeId))
                    .await()

    private suspend fun Pair<Promise<KtTribe>, Promise<List<Pin>>>.await() = first.await() to second.await()

}