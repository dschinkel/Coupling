package com.zegreatrob.coupling.client.player.retired

import com.zegreatrob.coupling.action.Action
import com.zegreatrob.coupling.action.ActionLoggingSyntax
import com.zegreatrob.coupling.model.await
import com.zegreatrob.coupling.model.player.TribeIdRetiredPlayersSyntax
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.model.tribe.TribeIdGetSyntax
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class RetiredPlayerListQuery(val tribeId: TribeId) : Action

interface RetiredPlayerListQueryDispatcher : ActionLoggingSyntax, TribeIdGetSyntax, TribeIdRetiredPlayersSyntax {
    suspend fun RetiredPlayerListQuery.perform() = logAsync { getData(tribeId) }
    private suspend fun getData(tribeId: TribeId) = coroutineScope {
        await(async { tribeId.load() }, async { tribeId.loadRetiredPlayers() })
    }
}