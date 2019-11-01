package com.zegreatrob.coupling.server.entity.tribe

import com.zegreatrob.coupling.action.Action
import com.zegreatrob.coupling.action.ActionLoggingSyntax
import com.zegreatrob.coupling.model.player.TribeIdPlayer
import com.zegreatrob.coupling.model.tribe.KtTribe

object TribeListQuery : Action

interface TribeListQueryDispatcher : ActionLoggingSyntax, UserAuthenticatedTribeIdSyntax, UserPlayersSyntax, TribeListSyntax {

    suspend fun TribeListQuery.perform() = logAsync { getTribesAndPlayers().onlyAuthenticatedTribes() }

    private suspend fun getTribesAndPlayers() = getTribesAndPlayersDeferred()
            .let { (tribeDeferred, playerDeferred) -> tribeDeferred.await() to playerDeferred.await() }

    private fun getTribesAndPlayersDeferred() =
            getTribesAsync() to getUserPlayersAsync()

    private fun Pair<List<KtTribe>, List<TribeIdPlayer>>.onlyAuthenticatedTribes() = let { (tribes, players) ->
        tribes.filter(players.authenticatedFilter())
    }

}
