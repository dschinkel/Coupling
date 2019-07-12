package com.zegreatrob.coupling.client

import com.zegreatrob.coupling.common.entity.player.Player
import com.zegreatrob.coupling.common.entity.tribe.TribeId
import kotlinx.css.*
import kotlinx.html.DIV
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import loadStyles
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import react.RBuilder
import react.RProps
import react.dom.div
import react.dom.img
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv

private external interface Styles {
    var player: String
    var header: String
}

private val styles: Styles = loadStyles("PlayerCard")

data class PlayerCardProps(
        val tribeId: TribeId,
        val player: Player,
        val pathSetter: (String) -> Unit,
        val disabled: Boolean = false,
        val className: String? = null,
        val size: Int = 100,
        val onClick: ((Event) -> Unit) = {}
) : RProps

val playerCard = rFunction { props: PlayerCardProps ->
    with(props) {
        styledDiv {
            attrs {
                classes += setOf(
                        styles.player,
                        "react-player-card",
                        props.className
                ).filterNotNull()
                playerCardStyle(size)
                onClickFunction = onClick
            }
            playerGravatarImage(player, size)
            playerCardHeader(
                    tribeId = tribeId,
                    player = player,
                    size = size,
                    disabled = disabled,
                    pathSetter = pathSetter
            )
        }
    }
}

private fun StyledDOMBuilder<DIV>.playerCardStyle(size: Int) {
    css {
        width = size.px
        height = (size * 1.4).px
        padding(all = (size * 0.06).px)
        borderWidth = (size * 0.01).px
    }
}

fun RBuilder.playerGravatarImage(player: Player, size: Int) = if (player.imageURL != null) {
    img(src = player.imageURL, classes = "player-icon", alt = "icon") {
        attrs {
            width = size.toString()
            height = size.toString()
        }
    }
} else {
    val email = player.email ?: player.name ?: ""
    gravatarImage(
            email = email,
            className = "player-icon",
            alt = "icon",
            options = object : GravatarOptions {
                override val size = size
                override val default = "retro"
            }
    )
}

fun RBuilder.playerCardHeader(
        tribeId: TribeId,
        player: Player,
        size: Int,
        disabled: Boolean,
        pathSetter: (String) -> Unit
) {
    val playerNameRef = useRef(null)
    useLayoutEffect { playerNameRef.current?.fitPlayerName(size) }

    styledDiv {
        attrs {
            classes = setOf("player-card-header", styles.header)
            onClickFunction = handleNameClick(tribeId, player, disabled, pathSetter)
        }
        css {
            margin(top = (size * 0.02).px)
        }
        div {
            attrs { ref = playerNameRef }
            +(if (player.id == null) "NEW:" else "")
            +(if (player.name.isNullOrBlank()) "Unknown" else player.name!!)
        }
    }
}

private fun handleNameClick(
        tribeId: TribeId,
        player: Player,
        disabled: Boolean,
        pathSetter: (String) -> Unit) = { event: Event ->
    if (!disabled) {
        event.stopPropagation()

        pathSetter("/${tribeId.value}/player/${player.id}/")
    }
}

private fun Node.fitPlayerName(size: Int) {
    val maxFontHeight = (size * 0.31)
    val minFontHeight = (size * 0.16)
    fitHeaderNode(maxFontHeight, minFontHeight)
}
