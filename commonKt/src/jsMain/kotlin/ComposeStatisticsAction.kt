data class ComposeStatisticsAction(
        val tribe: KtTribe,
        val players: List<Player>,
        val history: List<PairAssignmentDocument>
)

interface ComposeStatisticsActionDispatcher : PairingTimeCalculationSyntax {
    fun ComposeStatisticsAction.perform() = StatisticsReport(
            spinsUntilFullRotation = calculateFullRotation(),
            pairReports = pairReports(),
            medianSpinDuration = 0
    )

    private fun ComposeStatisticsAction.pairReports() = allPairCombinations()
            .map {
                PairReport(
                        it,
                        calculateTimeSinceLastPartnership(it, history)
                )
            }
            .sortedWith(PairReportComparator)


    private fun ComposeStatisticsAction.allPairCombinations() =
            players.mapIndexed { index, player -> players.sliceFrom(index + 1).toPairsWith(player) }
                    .flatten()

    private fun List<Player>.sliceFrom(startIndex: Int) = slice(startIndex..lastIndex)

    private fun List<Player>.toPairsWith(player: Player) =
            map { otherPlayer -> CouplingPair.Double(player, otherPlayer) }

    private fun ComposeStatisticsAction.calculateFullRotation() = players.size.ifEvenSubtractOne()

    private fun Int.ifEvenSubtractOne() = if (this % 2 == 0) {
        this - 1
    } else {
        this
    }
}

object PairReportComparator : Comparator<PairReport> {

    override fun compare(a: PairReport, b: PairReport) =
            a.timeSinceLastPair.compareTo(b.timeSinceLastPair)

    private fun TimeResult.compareTo(other: TimeResult) = TimeResultComparator.compare(this, other)
}

object TimeResultComparator : Comparator<TimeResult> {
    override fun compare(a: TimeResult, b: TimeResult) = when (a) {
        b -> 0
        is NeverPaired -> -1
        is TimeResultValue -> when (b) {
            is NeverPaired -> 1
            is TimeResultValue -> b.time.compareTo(a.time)
        }
    }
}

data class StatisticsReport(
        val spinsUntilFullRotation: Int,
        val pairReports: List<PairReport>,
        val medianSpinDuration: Int
)

data class PairReport(val pair: CouplingPair, val timeSinceLastPair: TimeResult)