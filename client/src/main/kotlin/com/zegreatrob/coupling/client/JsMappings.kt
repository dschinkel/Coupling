package com.zegreatrob.coupling.action

import com.zegreatrob.coupling.model.pairassignmentdocument.NeverPaired
import com.zegreatrob.coupling.model.pairassignmentdocument.TimeResultValue
import com.zegreatrob.coupling.json.toJson
import kotlin.js.json

fun StatisticsReport.toJson() = json(
    "spinsUntilFullRotation" to spinsUntilFullRotation,
    "pairReports" to pairReports.map { it.toJson() }.toTypedArray(),
    "medianSpinDuration" to medianSpinDuration?.millisecondsInt
)

fun PairReport.toJson() = json(
    "pair" to pair.asArray().map { it.toJson() }.toTypedArray(),
    "timeSinceLastPaired" to when (timeSinceLastPair) {
        is TimeResultValue -> (timeSinceLastPair as TimeResultValue).time
        NeverPaired -> "NeverPaired"
    }
)
