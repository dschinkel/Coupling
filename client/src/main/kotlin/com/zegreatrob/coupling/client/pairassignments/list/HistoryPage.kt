package com.zegreatrob.coupling.client.pairassignments.list

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.client.routing.PageProps
import com.zegreatrob.coupling.client.routing.dataLoadProps
import com.zegreatrob.coupling.client.routing.dataLoadWrapper
import react.RBuilder
import react.ReactElement

object HistoryPage : ComponentProvider<PageProps>(provider()), HistoryPageBuilder

private val LoadedPairAssignments = dataLoadWrapper(History)
private val RBuilder.loadedPairAssignments get() = LoadedPairAssignments.render(this)

interface HistoryPageBuilder : SimpleComponentRenderer<PageProps>, HistoryQueryDispatcher {

    override fun RContext<PageProps>.render(): ReactElement {
        val tribeId = props.tribeId

        return if (tribeId != null) {
            reactElement {
                loadedPairAssignments(
                    dataLoadProps(
                        query = { HistoryQuery(tribeId).perform() },
                        toProps = { reload, (tribe, history) ->
                            HistoryProps(tribe, history, reload, props.pathSetter)
                        }
                    )
                )
            }
        } else throw Exception("WHAT")
    }
}
