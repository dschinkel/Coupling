package com.zegreatrob.coupling.model.player.callsign

interface PredictableWordPicker {

    fun List<String>.pickForGiven(givenString: String) = givenString.map { it.toInt() }
        .sum()
        .let { sumOfCharacterValues -> sumOfCharacterValues % size }
        .let { index -> this[index] }

}