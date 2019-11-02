package com.zegreatrob.coupling.client.player

import com.zegreatrob.coupling.action.Action
import com.zegreatrob.coupling.action.ActionLoggingSyntax
import com.zegreatrob.coupling.action.entity.player.callsign.FindCallSignAction
import com.zegreatrob.coupling.action.entity.player.callsign.FindCallSignActionDispatcher
import com.zegreatrob.coupling.client.sdk.GetPlayerListSyntax
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.KtTribe
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.model.tribe.TribeIdGetSyntax
import kotlinx.coroutines.Deferred

data class PlayerQuery(val tribeId: TribeId, val playerId: String?) : Action

interface PlayerQueryDispatcher : ActionLoggingSyntax, TribeIdGetSyntax, GetPlayerListSyntax,
    FindCallSignActionDispatcher {
    suspend fun PlayerQuery.perform() = logAsync {
        tribeId.getData()
            .let { (tribe, players) ->
                Triple(
                    tribe,
                    players,
                    players.findOrDefaultNew(playerId)
                )
            }
    }

    private suspend fun TribeId.getData() = (loadAsync() to getPlayerListAsync())
        .await()

    private suspend fun Pair<Deferred<KtTribe?>, Deferred<List<Player>>>.await() =
        first.await() to second.await()

    private fun List<Player>.findOrDefaultNew(playerId: String?) = firstOrNull { it.id == playerId }
        ?: defaultWithCallSign()

    private fun List<Player>.defaultWithCallSign() = FindCallSignAction(this, "")
        .perform()
        .let { callSign ->
            Player(
                callSignAdjective = callSign.adjective,
                callSignNoun = callSign.noun
            )
        }
}