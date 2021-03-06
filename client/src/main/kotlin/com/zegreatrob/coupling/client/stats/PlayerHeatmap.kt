package com.zegreatrob.coupling.client.stats

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.client.player.PlayerCardProps
import com.zegreatrob.coupling.client.player.playerCard
import com.zegreatrob.coupling.client.stats.heatmap.HeatmapProps
import com.zegreatrob.coupling.client.stats.heatmap.heatmap
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.Tribe
import kotlinx.html.DIV
import react.RBuilder
import react.RProps
import react.ReactElement
import react.dom.RDOMBuilder
import react.dom.div

object PlayerHeatmap : RComponent<PlayerHeatmapProps>(provider()), PlayerHeatmapBuilder

val RBuilder.playerHeatmap get() = PlayerHeatmap.render(this)

data class PlayerHeatmapProps(
    val tribe: Tribe,
    val players: List<Player>,
    val heatmapData: List<List<Double?>>
) : RProps

external interface PlayerHeatmapStyles {
    val rightSection: String
    val heatmapPlayersTopRow: String
    val spacer: String
    val playerCard: String
    val heatmapPlayersSideRow: String
    val heatmap: String
}

interface PlayerHeatmapBuilder : StyledComponentRenderer<PlayerHeatmapProps, PlayerHeatmapStyles> {

    override val componentPath get() = "stats/PlayerHeatmap"

    override fun StyledRContext<PlayerHeatmapProps, PlayerHeatmapStyles>.render() = reactElement {
        div(classes = styles.rightSection) {
            div(classes = styles.heatmapPlayersTopRow) {
                div(classes = styles.spacer) {}
                props.players.map { player ->
                    keyedPlayerCard(styles, player, props.tribe)
                }
            }
            div(classes = styles.heatmapPlayersSideRow) {
                props.players.map { player ->
                    keyedPlayerCard(styles, player, props.tribe)
                }
            }
            heatmap(HeatmapProps(props.heatmapData, styles.heatmap))
        }
    }

    private fun RDOMBuilder<DIV>.keyedPlayerCard(
        styles: PlayerHeatmapStyles,
        player: Player,
        tribe: Tribe
    ): ReactElement {
        return div(classes = styles.playerCard) {
            attrs { key = player.id ?: "" }
            playerCard(PlayerCardProps(tribe.id, player, size = 50, pathSetter = {}))
        }
    }

}
