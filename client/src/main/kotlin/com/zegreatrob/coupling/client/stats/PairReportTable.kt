package com.zegreatrob.coupling.client.stats

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.client.player.PlayerCardProps
import com.zegreatrob.coupling.client.player.playerCard
import com.zegreatrob.coupling.action.PairReport
import com.zegreatrob.coupling.model.pairassignmentdocument.NeverPaired
import com.zegreatrob.coupling.model.pairassignmentdocument.TimeResult
import com.zegreatrob.coupling.model.pairassignmentdocument.TimeResultValue
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.Tribe
import kotlinx.html.DIV
import react.RBuilder
import react.RProps
import react.ReactElement
import react.dom.RDOMBuilder
import react.dom.div
import react.dom.span

object PairReportTable : RComponent<PairReportTableProps>(provider()), PairReportTableRenderer

val RBuilder.pairReportTable get() = PairReportTable.render(this)

interface PairReportTableRenderer : StyledComponentRenderer<PairReportTableProps, PairReportTableStyles> {

    override val componentPath: String get() = "stats/PairReportTable"

    override fun StyledRContext<PairReportTableProps, PairReportTableStyles>.render(): ReactElement {
        val (tribe, pairReports) = props
        return reactElement {
            div(classes = styles.className) {
                pairReports.mapIndexed { index, pairReport ->
                    pairReport(styles, index, pairReport, tribe)
                }
            }
        }
    }

    private fun RBuilder.pairReport(
        styles: PairReportTableStyles,
        index: Int,
        pairReport: PairReport,
        tribe: Tribe
    ) = div(classes = styles.pairReport) {
        attrs { key = "$index" }
        pairReport.pair.asArray().map { player -> reportPlayerCard(styles, player, tribe) }

        div(classes = styles.pairStatistics) {
            statsHeader { +"Stats" }
            statLabel { +"Spins since last paired:" }
            span(classes = "time-since-last-pairing") {
                +pairReport.timeSinceLastPair.presentationString()
            }
        }
    }

    private fun TimeResult.presentationString() = when (this) {
        is TimeResultValue -> "$time"
        NeverPaired -> "Never Paired"
    }

    private fun RDOMBuilder<DIV>.reportPlayerCard(styles: PairReportTableStyles, player: Player, tribe: Tribe) =
        div(classes = styles.playerCard) {
            attrs { key = player.id ?: "" }
            playerCard(PlayerCardProps(tribe.id, player, size = 50, pathSetter = {}))
        }
}

external interface PairReportTableStyles {
    val className: String
    val pairReport: String
    val playerCard: String
    val pairStatistics: String
}

data class PairReportTableProps(val tribe: Tribe, val pairReports: List<PairReport>) : RProps
