package com.zegreatrob.coupling.client

import com.zegreatrob.coupling.common.entity.player.Player
import com.zegreatrob.coupling.common.entity.tribe.KtTribe
import loadStyles
import react.RBuilder
import react.RProps
import react.dom.div

data class RetiredPlayersProps(
        val tribe: KtTribe,
        val retiredPlayers: List<Player>,
        val pathSetter: (String) -> Unit
) : RProps

interface RetiredPlayersCss {
    val className: String
    val header: String
}

private val styles = loadStyles<RetiredPlayersCss>("RetiredPlayers")


interface RetiredPlayersRenderer : PlayerCardRenderer, ReactComponentRenderer {

    val RBuilder.retiredPlayers
        get() = rFunction { (tribe, players, pathSetter): RetiredPlayersProps ->
            div(classes = styles.className) {
                element(tribeBrowser, TribeBrowserProps(tribe, pathSetter))
                div(classes = styles.header) { +"Retired Players" }
                div {
                    players.forEach { player ->
                        element(
                                playerCard,
                                PlayerCardProps(
                                        tribe.id,
                                        player,
                                        pathSetter,
                                        true,
                                        className = "disabled"
                                ),
                                key = player.id
                        )
                    }
                }
            }
        }
}


