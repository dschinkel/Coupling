package com.zegreatrob.coupling.client.player

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.TribeId
import kotlinx.html.classes
import react.RBuilder
import react.RProps
import react.dom.button
import react.dom.div
import react.router.dom.routeLink


object PlayerRoster : RComponent<PlayerRosterProps>(provider()), PlayerRosterBuilder

val RBuilder.playerRoster get() = PlayerRoster.render(this)

interface PlayerRosterStyles {
    val className: String
    val addPlayerButton: String
    val header: String
}

data class PlayerRosterProps(
    val label: String? = null,
    val players: List<Player>,
    val tribeId: TribeId,
    val pathSetter: (String) -> Unit,
    val className: String? = null
) : RProps

interface PlayerRosterBuilder : StyledComponentRenderer<PlayerRosterProps, PlayerRosterStyles> {

    override val componentPath: String get() = "player/PlayerRoster"

    override fun StyledRContext<PlayerRosterProps, PlayerRosterStyles>.render() = reactElement {
        div(classes = props.className) {
            attrs { classes += styles.className }
            div {
                div(classes = styles.header) {
                    +(props.label ?: "Players")
                }
                renderPlayers(props)
            }
            routeLink(to = "/${props.tribeId.value}/player/new/") {
                button(classes = "large orange button") {
                    attrs { classes += styles.addPlayerButton }
                    +"Add a new player!"
                }
            }
        }
    }

    private fun RBuilder.renderPlayers(props: PlayerRosterProps) = with(props) {
        players.forEach { player ->
            playerCard(
                PlayerCardProps(tribeId = tribeId, player = player, pathSetter = pathSetter),
                key = player.id
            )
        }
    }
}

val RBuilder.pairAssignments get() = PlayerRoster.render(this)
