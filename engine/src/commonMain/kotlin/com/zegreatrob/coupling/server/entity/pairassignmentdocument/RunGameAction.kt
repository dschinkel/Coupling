package com.zegreatrob.coupling.server.entity.pairassignmentdocument

import com.soywiz.klock.DateTime
import com.zegreatrob.coupling.common.entity.pairassignmentdocument.PairAssignmentDocument
import com.zegreatrob.coupling.common.entity.pairassignmentdocument.PinnedCouplingPair
import com.zegreatrob.coupling.common.entity.pin.Pin
import com.zegreatrob.coupling.common.entity.player.Player
import com.zegreatrob.coupling.common.entity.tribe.KtTribe

data class RunGameAction(
        val players: List<Player>,
        val pins: List<Pin>,
        val history: List<PairAssignmentDocument>,
        val tribe: KtTribe
)

interface RunGameActionDispatcher : Clock, PinAssignmentSyntax {

    val actionDispatcher: FindNewPairsActionDispatcher

    private fun FindNewPairsAction.performThis() = with(actionDispatcher) { perform() }

    fun RunGameAction.perform() = findNewPairs()
            .assign(pins)
            .let { pairAssignments -> pairAssignmentDocument(pairAssignments) }

    private fun RunGameAction.findNewPairs() = findNewPairsAction()
            .performThis()

    private fun RunGameAction.findNewPairsAction() = FindNewPairsAction(Game(
            history,
            players,
            tribe.pairingRule
    ))

    private fun pairAssignmentDocument(pairAssignments: List<PinnedCouplingPair>) =
            PairAssignmentDocument(
                    currentDate(),
                    pairAssignments
            )
}

interface Clock {
    fun currentDate() = DateTime.now()
}
