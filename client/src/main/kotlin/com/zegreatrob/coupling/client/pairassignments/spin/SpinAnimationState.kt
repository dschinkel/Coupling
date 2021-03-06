package com.zegreatrob.coupling.client.pairassignments.spin

import com.zegreatrob.coupling.model.pairassignmentdocument.PairAssignmentDocument
import com.zegreatrob.coupling.model.pairassignmentdocument.orderedPairedPlayers
import com.zegreatrob.coupling.model.pairassignmentdocument.pairOf
import com.zegreatrob.coupling.model.pairassignmentdocument.withPins
import com.zegreatrob.coupling.model.player.Player

sealed class SpinAnimationState {
    abstract fun next(pairAssignments: PairAssignmentDocument): SpinAnimationState?
    abstract fun stateData(players: List<Player>, pairAssignments: PairAssignmentDocument): SpinStateData
    open fun duration(pairAssignments: PairAssignmentDocument): Int = 200

    companion object {
        fun sequence(pairAssignments: PairAssignmentDocument) =
            generateSequence<Pair<SpinAnimationState, Int>>(Start to 0) { (state, time) ->
                state.next(pairAssignments)
                    ?.let {
                        it to time + state.duration(pairAssignments)
                    }
            }
    }
}

object Start : SpinAnimationState() {
    override fun toString() = "Start"
    override fun next(pairAssignments: PairAssignmentDocument): SpinAnimationState {
        val orderedPairedPlayers = pairAssignments.orderedPairedPlayers()
        val firstPlayer = orderedPairedPlayers.first()
        return if (orderedPairedPlayers.count() == 1) {
            ShowPlayer(firstPlayer)
        } else {
            Shuffle(firstPlayer, 0)
        }
    }

    override fun stateData(players: List<Player>, pairAssignments: PairAssignmentDocument) = SpinStateData(
        rosterPlayers = players,
        revealedPairs = makePlaceholderPlayers(
            pairAssignments
        ).toSimulatedPairs(),
        shownPlayer = null
    )
}

object End : SpinAnimationState() {
    override fun toString() = "End"
    override fun next(pairAssignments: PairAssignmentDocument): SpinAnimationState? = null
    override fun stateData(players: List<Player>, pairAssignments: PairAssignmentDocument) = SpinStateData(
        rosterPlayers = emptyList(),
        revealedPairs = emptyList(),
        shownPlayer = null
    )
}

data class ShowPlayer(val player: Player) : SpinAnimationState() {
    override fun duration(pairAssignments: PairAssignmentDocument) = 500
    override fun next(pairAssignments: PairAssignmentDocument) =
        AssignedPlayer(player)

    override fun stateData(players: List<Player>, pairAssignments: PairAssignmentDocument): SpinStateData {
        fun ifEmptyAddPlaceholder(rosterPlayers: List<Player>) = if (rosterPlayers.isEmpty())
            makePlaceholderPlayers(pairAssignments)
        else
            rosterPlayers

        val presentedPlayers = pairAssignments.previouslyPresentedPlayers(player)

        return SpinStateData(
            rosterPlayers = (players - presentedPlayers - player).let(::ifEmptyAddPlaceholder),
            revealedPairs = pairAssignments.revealedPairs(presentedPlayers),
            shownPlayer = player
        )
    }
}

data class Shuffle(val target: Player, val step: Int) : SpinAnimationState() {

    private val fullShuffles = 2
    private val shuffleTotalDuration = 1000

    override fun next(pairAssignments: PairAssignmentDocument): SpinAnimationState {
        val numberOfPlayersShuffling = numberOfPlayersShuffling(pairAssignments)
        val hasShuffledEnough = step / numberOfPlayersShuffling >= fullShuffles
        return if (numberOfPlayersShuffling == 1 || hasShuffledEnough) {
            ShowPlayer(target)
        } else {
            Shuffle(target, step + 1)
        }
    }

    private fun numberOfPlayersShuffling(pairAssignments: PairAssignmentDocument): Int {
        val orderedPairedPlayers = pairAssignments.orderedPairedPlayers()

        val indexOfTarget = orderedPairedPlayers.indexOf(target)

        return orderedPairedPlayers.count() - indexOfTarget
    }

    override fun duration(pairAssignments: PairAssignmentDocument) =
        shuffleTotalDuration / (numberOfPlayersShuffling(pairAssignments) * fullShuffles)

    override fun stateData(players: List<Player>, pairAssignments: PairAssignmentDocument): SpinStateData {
        fun rotateList(rosterPlayers: List<Player>): List<Player> {
            val peopleToRotate = step % rosterPlayers.size
            return rosterPlayers.takeLast(rosterPlayers.size - peopleToRotate) + rosterPlayers.take(peopleToRotate)
        }

        val presentedPlayers = pairAssignments.previouslyPresentedPlayers(target)
        return SpinStateData(
            rosterPlayers = (players - presentedPlayers).let(::rotateList),
            revealedPairs = pairAssignments.revealedPairs(presentedPlayers),
            shownPlayer = null
        )

    }

}

data class AssignedPlayer(val player: Player) : SpinAnimationState() {
    override fun next(pairAssignments: PairAssignmentDocument): SpinAnimationState {
        val orderedPlayers = pairAssignments.pairs.flatMap { it.players }.map { it.player }
        val playerIndex = orderedPlayers.indexOf(player)
        val nextPlayer = orderedPlayers.getOrNull(playerIndex + 1)
        return nextPlayer?.let { Shuffle(it, 0) } ?: End
    }

    override fun stateData(players: List<Player>, pairAssignments: PairAssignmentDocument): SpinStateData {
        val presentedPlayers = pairAssignments.previouslyPresentedPlayers(player) + player
        return SpinStateData(
            rosterPlayers = players - presentedPlayers,
            revealedPairs = pairAssignments.revealedPairs(presentedPlayers),
            shownPlayer = null
        )
    }

}

private fun PairAssignmentDocument.previouslyPresentedPlayers(player: Player) = orderedPairedPlayers()
    .takeWhile { it != player }
    .toList()

private fun PairAssignmentDocument.revealedPairs(presentedPlayers: List<Player>) =
    presentedPlayers
        .let {
            it + makePlaceholderPlayers(
                it,
                this
            )
        }.toSimulatedPairs()

private fun List<Player>.toSimulatedPairs() = chunked(2)
    .map {
        if (it.size > 1) pairOf(
            it[0],
            it[1]
        ) else pairOf(it[0])
    }
    .map { it.withPins(emptyList()) }

private fun makePlaceholderPlayers(it: List<Player>, document: PairAssignmentDocument) = infinitePlaceholders()
    .take(document.orderedPairedPlayers().count() - it.size)
    .toList()

private fun makePlaceholderPlayers(pairAssignmentDocument: PairAssignmentDocument) = infinitePlaceholders()
    .take(pairAssignmentDocument.orderedPairedPlayers().count())
    .toList()

private fun infinitePlaceholders() = generateSequence { placeholderPlayer }