package com.zegreatrob.coupling.client.player.retired

import com.zegreatrob.coupling.action.Action
import com.zegreatrob.coupling.action.ActionLoggingSyntax
import com.zegreatrob.coupling.repository.await
import com.zegreatrob.coupling.repository.player.TribeIdRetiredPlayersSyntax
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.repository.tribe.TribeIdGetSyntax
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class RetiredPlayerQuery(val tribeId: TribeId, val playerId: String) : Action

interface RetiredPlayerQueryDispatcher : ActionLoggingSyntax,
    TribeIdGetSyntax,
    TribeIdRetiredPlayersSyntax {
    suspend fun RetiredPlayerQuery.perform() = logAsync {
        tribeId.getData()
            .let { (tribe, players) ->
                Triple(tribe, players, players.first { it.id == playerId })
            }
    }

    private suspend fun TribeId.getData() = coroutineScope {
        await(async { get() }, async { loadRetiredPlayers() })
    }
}