package com.zegreatrob.coupling.server.entity.player

import com.zegreatrob.coupling.common.Action
import com.zegreatrob.coupling.common.ActionLoggingSyntax
import com.zegreatrob.coupling.common.entity.player.callsign.FindCallSignAction
import com.zegreatrob.coupling.common.entity.player.callsign.FindCallSignActionDispatcher
import com.zegreatrob.coupling.core.entity.player.Player
import com.zegreatrob.coupling.core.entity.player.callsign.CallSign
import com.zegreatrob.coupling.core.entity.tribe.TribeId

data class PlayersQuery(val tribeId: TribeId) : Action

interface PlayersQueryDispatcher : ActionLoggingSyntax, TribeIdPlayersSyntax, FindCallSignActionDispatcher {
    suspend fun PlayersQuery.perform() = logAsync {
        val players = tribeId.loadPlayers()

        var updatedPlayers = emptyList<Player>()
        players
            .forEachIndexed { index, player ->

                val callSign = findCallSign(updatedPlayers, players, index, player)

                updatedPlayers = updatedPlayers + player.withCallSign(callSign)

            }
        updatedPlayers
    }

    private fun Player.withCallSign(callSign: CallSign) = copy(
        callSignAdjective = callSignAdjective ?: callSign.adjective,
        callSignNoun = callSignNoun ?: callSign.noun
    )

    private fun findCallSign(updatedPlayers: List<Player>, players: List<Player>, index: Int, player: Player) =
        FindCallSignAction(
            players = playersWithNamesSoFar(updatedPlayers, players, index),
            email = player.email ?: player.id ?: ""
        )
            .perform()

    private fun playersWithNamesSoFar(updatedPlayers: List<Player>, players: List<Player>, index: Int) =
        updatedPlayers + players.subList(index, players.lastIndex)
}
