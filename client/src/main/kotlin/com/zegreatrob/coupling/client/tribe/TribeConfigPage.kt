package com.zegreatrob.coupling.client.tribe

import com.zegreatrob.coupling.client.*
import com.zegreatrob.coupling.common.entity.tribe.KtTribe
import com.zegreatrob.coupling.common.entity.tribe.TribeId
import react.RBuilder

object TribeConfigPage : ComponentProvider<PageProps>(), TribeConfigPageBuilder

private val LoadedTribeConfig = dataLoadWrapper(TribeConfig)
private val RBuilder.loadedTribeConfig get() = LoadedTribeConfig.captor(this)

interface TribeConfigPageBuilder : ComponentBuilder<PageProps>, TribeQueryDispatcher {

    override fun build() = reactFunctionComponent<PageProps> { pageProps ->
        loadedTribeConfig(
                dataLoadProps(
                        query = { performCorrectQuery(pageProps.tribeId) },
                        toProps = { _, data -> tribeConfigProps(data, pageProps.pathSetter) }
                )
        )
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
