package com.zegreatrob.coupling.client.stats

import com.zegreatrob.coupling.client.external.react.*
import react.RBuilder
import react.RProps
import react.dom.div
import react.dom.span

object TeamStatistics : RComponent<TeamStatisticsProps>(provider()), TeamStatisticsBuilder

val RBuilder.teamStatistics get() = TeamStatistics.render(this)

interface TeamStatisticsBuilder : StyledComponentRenderer<TeamStatisticsProps, TeamStatisticsStyles> {

    override val componentPath: String get() = "stats/TeamStatistics"

    override fun StyledRContext<TeamStatisticsProps, TeamStatisticsStyles>.render() = reactElement {
        div(classes = styles.className) {
            statsHeader { +"Team Stats" }
            div {
                statLabel { +"Spins Until Full Rotation:" }
                span(classes = styles.rotationNumber) { +"${props.spinsUntilFullRotation}" }
            }
            div {
                statLabel { +"Number of Active Players:" }
                span(classes = styles.activePlayerCount) { +"${props.activePlayerCount}" }
            }
            div {
                statLabel { +"Median Spin Duration:" }
                span(classes = styles.medianSpinDuration) { +props.medianSpinDuration }
            }
        }
    }

}

external interface TeamStatisticsStyles {
    val className: String
    val rotationNumber: String
    val activePlayerCount: String
    val medianSpinDuration: String
}

data class TeamStatisticsProps(
    val spinsUntilFullRotation: Int,
    val activePlayerCount: Int,
    val medianSpinDuration: String
) : RProps
