package com.zegreatrob.coupling.client.player.retired

import com.zegreatrob.coupling.action.Action
import com.zegreatrob.coupling.action.ActionLoggingSyntax
import com.zegreatrob.coupling.client.sdk.GetRetiredPlayerListSyntax
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.KtTribe
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.model.tribe.TribeIdGetSyntax
import kotlinx.coroutines.Deferred

data class RetiredPlayerQuery(val tribeId: TribeId, val playerId: String) : Action

interface RetiredPlayerQueryDispatcher : ActionLoggingSyntax, TribeIdGetSyntax, GetRetiredPlayerListSyntax {
    suspend fun RetiredPlayerQuery.perform() = logAsync {
        tribeId.getData()
            .let { (tribe, players) ->
                Triple(tribe, players, players.first { it.id == playerId })
            }
    }

    private suspend fun TribeId.getData() =
        (loadAsync() to getRetiredPlayerListAsync())
            .await()

    private suspend fun Pair<Deferred<KtTribe?>, Deferred<List<Player>>>.await() =
        first.await() to second.await()
}