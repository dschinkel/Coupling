package com.zegreatrob.coupling.server.action.pairassignmentdocument

import com.zegreatrob.coupling.model.pairassignmentdocument.NeverPaired
import com.zegreatrob.coupling.model.pairassignmentdocument.TimeResultValue

data class NextPlayerAction(val gameSpin: GameSpin)

interface NextPlayerActionDispatcher {

    val actionDispatcher: CreatePairCandidateReportsActionDispatcher

    private fun CreatePairCandidateReportsAction.performThis() = with(actionDispatcher) { perform() }

    fun NextPlayerAction.perform() = createPairCandidateReports()
        .fold<PairCandidateReport, PairCandidateReport?>(null) { reportWithLongestTime, report ->
            when {
                reportWithLongestTime == null -> report
                reportWithLongestTime.timeResult == report.timeResult -> withFewestPartners(
                    report,
                    reportWithLongestTime
                )
                report.timeResult is NeverPaired -> report
                timeSinceLastPairedIsLonger(report, reportWithLongestTime) -> report
                else -> reportWithLongestTime
            }
        }

    fun NextPlayerAction.createPairCandidateReports() = CreatePairCandidateReportsAction(gameSpin)
        .performThis()

    private fun withFewestPartners(report: PairCandidateReport, reportWithLongestTime: PairCandidateReport) =
        when {
            report.partners.size < reportWithLongestTime.partners.size -> report
            else -> reportWithLongestTime
        }

    private fun timeSinceLastPairedIsLonger(
        report: PairCandidateReport,
        reportWithLongestTime: PairCandidateReport
    ): Boolean {
        return if (report.timeResult is TimeResultValue && reportWithLongestTime.timeResult is TimeResultValue) {
            report.timeResult.time > reportWithLongestTime.timeResult.time
        } else {
            false
        }
    }
}
