package com.zegreatrob.coupling.client.stats

import com.zegreatrob.coupling.action.*
import com.zegreatrob.coupling.action.entity.heatmap.CalculateHeatMapAction
import com.zegreatrob.coupling.action.entity.heatmap.CalculateHeatMapActionDispatcher
import com.zegreatrob.coupling.model.pairassignmentdocument.PairAssignmentDocument
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.Tribe
import com.zegreatrob.coupling.model.tribe.TribeId

data class StatisticsQuery(val tribeId: TribeId) : Action

data class StatisticQueryResults(
    val tribe: Tribe,
    val players: List<Player>,
    val history: List<PairAssignmentDocument>,
    val report: StatisticsReport,
    val heatmapData: List<List<Double?>>
)

interface StatisticsQueryDispatcher : ActionLoggingSyntax,
    ComposeStatisticsActionDispatcher,
    CalculateHeatMapActionDispatcher,
    TribeIdLoadAllSyntax {

    suspend fun StatisticsQuery.perform() = logAsync {
        val (tribe, players, history) = tribeId.loadAll()

        val (report, heatmapData) = calculateStats(tribe, players, history)

        StatisticQueryResults(tribe, players, history, report, heatmapData)
    }

    private fun calculateStats(
        tribe: Tribe,
        players: List<Player>,
        history: List<PairAssignmentDocument>
    ) = ComposeStatisticsAction(tribe, players, history).perform() to
            CalculateHeatMapAction(
                players,
                history,
                ComposeStatisticsAction(tribe, players, history).perform().spinsUntilFullRotation
            )
                .perform()
}

