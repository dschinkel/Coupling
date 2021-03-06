package com.zegreatrob.coupling.client.pairassignments

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.client.pairassignments.DraggableThing.draggableThing
import com.zegreatrob.coupling.client.player.PlayerCardProps
import com.zegreatrob.coupling.client.player.playerCard
import com.zegreatrob.coupling.model.pairassignmentdocument.PinnedPlayer
import com.zegreatrob.coupling.model.tribe.Tribe
import react.RBuilder
import react.RProps
import react.ReactElement

object DraggablePlayer : RComponent<DraggablePlayerProps>(provider()), DraggablePlayerBuilder

val RBuilder.draggablePlayer get() = DraggablePlayer.render(this)

data class DraggablePlayerProps(
    val pinnedPlayer: PinnedPlayer,
    val tribe: Tribe,
    val zoomOnHover: Boolean,
    val onPlayerDrop: (String) -> Unit
) : RProps

external interface DraggablePlayerStyles {
    val className: String
    val hoverZoom: String
    val onDragHover: String
}

const val playerDragItemType = "PLAYER"

interface DraggablePlayerBuilder : StyledComponentRenderer<DraggablePlayerProps, DraggablePlayerStyles> {

    override val componentPath: String get() = "pairassignments/DraggablePlayer"

    override fun StyledRContext<DraggablePlayerProps, DraggablePlayerStyles>.render(): ReactElement = with(props) {
        reactElement {
            draggableThing(playerDragItemType, pinnedPlayer.player.id!!, onPlayerDrop) { isOver: Boolean ->
                playerCard(
                    PlayerCardProps(
                        tribeId = tribe.id,
                        player = pinnedPlayer.player,
                        pathSetter = {},
                        headerDisabled = false,
                        className = playerCardClassName(isOver, styles, zoomOnHover)
                    ),
                    key = pinnedPlayer.player.id
                )
            }
        }
    }

    private fun playerCardClassName(
        isOver: Boolean,
        styles: DraggablePlayerStyles,
        zoomOnHover: Boolean
    ) = mapOf(
        styles.hoverZoom to zoomOnHover,
        styles.onDragHover to isOver
    )
        .filterValues { it }
        .keys
        .plus(styles.className)
        .joinToString(" ")
}
