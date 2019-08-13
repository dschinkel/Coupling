package com.zegreatrob.coupling.client.tribe

import com.zegreatrob.coupling.client.external.react.ComponentBuilder
import com.zegreatrob.coupling.client.external.react.ComponentProvider
import com.zegreatrob.coupling.client.external.react.buildByPls
import com.zegreatrob.coupling.client.external.react.reactElement
import com.zegreatrob.coupling.client.routing.PageProps
import com.zegreatrob.coupling.client.routing.dataLoadProps
import com.zegreatrob.coupling.client.routing.dataLoadWrapper
import com.zegreatrob.coupling.common.entity.tribe.KtTribe
import com.zegreatrob.coupling.common.entity.tribe.TribeId
import react.RBuilder

object TribeConfigPage : ComponentProvider<PageProps>(), TribeConfigPageBuilder

private val LoadedTribeConfig = dataLoadWrapper(TribeConfig)
private val RBuilder.loadedTribeConfig get() = LoadedTribeConfig.captor(this)

interface TribeConfigPageBuilder : ComponentBuilder<PageProps>, TribeQueryDispatcher {

    override fun build() = buildByPls {
        reactElement {
            loadedTribeConfig(
                dataLoadProps(
                    query = { performCorrectQuery(props.tribeId) },
                    toProps = { _, data -> tribeConfigProps(data, props.pathSetter) }
                )
            )
        }
    }

    private suspend fun performCorrectQuery(tribeId: TribeId?) = if (tribeId != null)
        TribeQuery(tribeId).perform()
    else
        newTribe()

    private fun newTribe() = KtTribe(
        id = TribeId(""),
        name = "New Tribe",
        defaultBadgeName = "Default",
        alternateBadgeName = "Alternate"
    )

    private fun tribeConfigProps(tribe: KtTribe, pathSetter: (String) -> Unit) = TribeConfigProps(
        tribe = tribe,
        pathSetter = pathSetter
    )
}
