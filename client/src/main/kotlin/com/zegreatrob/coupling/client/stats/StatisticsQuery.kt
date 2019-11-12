package com.zegreatrob.coupling.client.stats

import com.zegreatrob.coupling.action.*
import com.zegreatrob.coupling.action.entity.heatmap.CalculateHeatMapAction
import com.zegreatrob.coupling.action.entity.heatmap.CalculateHeatMapActionDispatcher
import com.zegreatrob.coupling.model.await
import com.zegreatrob.coupling.model.pairassignmentdocument.PairAssignmentDocument
import com.zegreatrob.coupling.model.pairassignmentdocument.TribeIdHistorySyntax
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.player.TribeIdPlayersSyntax
import com.zegreatrob.coupling.model.tribe.KtTribe
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.model.tribe.TribeIdGetSyntax
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class StatisticsQuery(val tribeId: TribeId) : Action

data class StatisticQueryResults(
    val tribe: KtTribe,
    val players: List<Player>,
    val history: List<PairAssignmentDocument>,
    val report: StatisticsReport,
    val heatmapData: List<List<Double?>>
)

interface StatisticsQueryDispatcher : ActionLoggingSyntax, TribeIdGetSyntax, TribeIdPlayersSyntax,
    TribeIdHistorySyntax,
    ComposeStatisticsActionDispatcher,
    CalculateHeatMapActionDispatcher {

    suspend fun StatisticsQuery.perform() = logAsync {
        val (tribe, players, history) = tribeId.getData()

        val (report, heatmapData) = calculateStats(tribe, players, history)

        StatisticQueryResults(tribe, players, history, report, heatmapData)
    }

    private suspend fun TribeId.getData() = coroutineScope {
        await(
            async { load()!! },
            async { loadPlayers() },
            async { getHistory() }
        )
    }

    private fun calculateStats(
        tribe: KtTribe,
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

