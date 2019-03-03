package com.zegreatrob.coupling.common.entity.player.callsign

import com.zegreatrob.coupling.common.entity.player.Player

data class FindCallSignCommand(val players: List<Player>, val email: String) {
}

interface FindCallSignCommandDispatcher : PickCallSignActionDispatcher {

    fun FindCallSignCommand.perform() = findOptions()
            .let { (adjectives, nouns) ->
                generateCallSign(adjectives, nouns)
            }

    private fun FindCallSignCommand.generateCallSign(adjectives: Set<String>, nouns: Set<String>) =
            GenerateCallSignAction(adjectives, nouns, email, players)
                    .perform()

    private fun findOptions() = defaultOptions
}