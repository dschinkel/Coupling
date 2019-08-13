package com.zegreatrob.coupling.client.player.retired

import com.zegreatrob.coupling.client.external.react.ComponentBuilder
import com.zegreatrob.coupling.client.external.react.ComponentProvider
import com.zegreatrob.coupling.client.external.react.buildByPls
import com.zegreatrob.coupling.client.external.react.reactElement
import com.zegreatrob.coupling.client.player.PlayerConfig
import com.zegreatrob.coupling.client.player.PlayerConfigProps
import com.zegreatrob.coupling.client.routing.PageProps
import com.zegreatrob.coupling.client.routing.dataLoadProps
import com.zegreatrob.coupling.client.routing.dataLoadWrapper
import com.zegreatrob.coupling.common.entity.tribe.TribeId
import react.RBuilder

object RetiredPlayerPage : ComponentProvider<PageProps>(), RetiredPlayerPageBuilder

private val LoadedRetiredPlayer = dataLoadWrapper(PlayerConfig)
private val RBuilder.loadedRetiredPlayer get() = LoadedRetiredPlayer.captor(this)

interface RetiredPlayerPageBuilder : ComponentBuilder<PageProps>, RetiredPlayerQueryDispatcher {

    override fun build() = buildByPls {
        val tribeId = props.tribeId
        val playerId = props.playerId

        if (tribeId != null && playerId != null) {
            reactElement {
                loadedRetiredPlayer(
                    dataLoadProps(
                        query = { performRetiredPlayerQuery(tribeId, playerId) },
                        toProps = { reload, (tribe, players, player) ->
                            PlayerConfigProps(
                                tribe = tribe,
                                player = player,
                                players = players,
                                pathSetter = props.pathSetter,
                                reload = reload
                            )
                        }
                    )
                ) {
                    attrs { key = playerId }
                }
            }
        } else throw Exception("WHAT")
    }

    private suspend fun performRetiredPlayerQuery(tribeId: TribeId, playerId: String) =
        RetiredPlayerQuery(tribeId, playerId).perform()
}
