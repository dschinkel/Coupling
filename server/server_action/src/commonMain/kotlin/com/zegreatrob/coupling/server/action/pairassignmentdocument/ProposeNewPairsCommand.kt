package com.zegreatrob.coupling.server.action.pairassignmentdocument

import com.zegreatrob.coupling.action.Action
import com.zegreatrob.coupling.action.ActionLoggingSyntax
import com.zegreatrob.coupling.model.pin.Pin
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.repository.await
import com.zegreatrob.coupling.repository.pairassignmentdocument.TribeIdHistorySyntax
import com.zegreatrob.coupling.repository.tribe.TribeIdGetSyntax
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class ProposeNewPairsCommand(
    val tribeId: TribeId,
    val players: List<Player>,
    val pins: List<Pin>
) : Action

interface ProposeNewPairsCommandDispatcher : ActionLoggingSyntax, RunGameActionDispatcher, TribeIdGetSyntax,
    TribeIdHistorySyntax {

    suspend fun ProposeNewPairsCommand.perform() = logAsync {
        loadData()
            .let { (history, tribe) -> RunGameAction(players, pins, history, tribe) }
            .performThis()
    }

    private fun RunGameAction.performThis() = perform()

    private suspend fun ProposeNewPairsCommand.loadData() = coroutineScope {
        await(
            async { tribeId.loadHistory() },
            async { tribeId.get()!! }
        )
    }

}
