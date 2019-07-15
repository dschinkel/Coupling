
import com.zegreatrob.coupling.client.Components.serverMessage
import com.zegreatrob.coupling.client.GravatarOptions
import com.zegreatrob.coupling.client.ServerMessageProps
import com.zegreatrob.coupling.client.element
import com.zegreatrob.coupling.client.gravatarImage
import com.zegreatrob.coupling.client.player.*
import com.zegreatrob.coupling.client.tribe.*
import com.zegreatrob.coupling.common.*
import com.zegreatrob.coupling.common.entity.heatmap.CalculateHeatMapCommand
import com.zegreatrob.coupling.common.entity.heatmap.CalculateHeatMapCommandDispatcher
import com.zegreatrob.coupling.common.entity.player.callsign.CallSign
import com.zegreatrob.coupling.common.entity.player.callsign.FindCallSignAction
import com.zegreatrob.coupling.common.entity.player.callsign.FindCallSignActionDispatcher
import com.zegreatrob.coupling.common.entity.tribe.TribeId
import org.w3c.dom.events.Event
import react.buildElements
import kotlin.js.Json
import kotlin.js.json

@Suppress("unused")
@JsName("performComposeStatisticsAction")
fun ComposeStatisticsActionDispatcher.performComposeStatisticsAction(tribe: Json, players: Array<Json>, history: Array<Json>) =
        ComposeStatisticsAction(
                tribe.toTribe(),
                players.map { it.toPlayer() },
                history.map { it.toPairAssignmentDocument() }
        )
                .perform()
                .toJson()

@Suppress("unused")
@JsName("commandDispatcher")
fun commandDispatcher(): CommandDispatcher = object : CommandDispatcher {

    @JsName("performFindCallSignAction")
    fun performFindCallSignAction(players: Array<Json>, player: Json) = FindCallSignAction(
            players.map { it.toPlayer() },
            player.toPlayer().run { email ?: id ?: "" }
    ).perform()
            .toJson()

    @JsName("performCalculateHeatMapCommand")
    fun performCalculateHeatMapCommand(
            players: Array<Json>,
            history: Array<Json>,
            rotationPeriod: Int
    ) = CalculateHeatMapCommand(
            players.map { it.toPlayer() },
            historyFromArray(history),
            rotationPeriod
    ).perform()
            .map { it.toTypedArray() }
            .toTypedArray()

}

private fun CallSign.toJson() = json(
        "adjective" to adjective,
        "noun" to noun
)

interface CommandDispatcher : FindCallSignActionDispatcher, CalculateHeatMapCommandDispatcher

object ReactComponents : PlayerCardRenderer, RetiredPlayersRenderer, PlayerRosterRenderer {

}


@Suppress("unused")
@JsName("GravatarImage")
fun gravatarImageJs(props: dynamic): dynamic = buildElements {
    gravatarImage(
            props.email as String?,
            props.fallback as String?,
            props.className as String?,
            props.alt as String?,
            props.options.unsafeCast<GravatarOptions>()
    )
}

@Suppress("unused")
@JsName("TribeCard")
fun tribeCardJs(props: dynamic): dynamic = buildElements {
    element(tribeCard, TribeCardProps(
            tribe = props.tribe.unsafeCast<Json>().toTribe(),
            pathSetter = props.pathSetter.unsafeCast<Function1<String, Unit>>(),
            size = props.size.unsafeCast<Int?>() ?: 150
    ))
}

@Suppress("unused")
@JsName("TribeList")
fun tribeListJs(props: dynamic): dynamic = buildElements {
    element(tribeList, TribeListProps(
            tribes = props.tribes.unsafeCast<Array<Json>>().map { it.toTribe() },
            pathSetter = props.pathSetter.unsafeCast<Function1<String, Unit>>()
    ))
}

@Suppress("unused")
@JsName("TribeBrowser")
fun tribeBrowserJs(props: dynamic): dynamic = buildElements {
    element(tribeBrowser, TribeBrowserProps(
            tribe = props.tribe.unsafeCast<Json>().toTribe(),
            pathSetter = props.pathSetter.unsafeCast<Function1<String, Unit>>()
    ))
}

@Suppress("unused")
@JsName("PlayerCard")
fun playerCardJs(props: dynamic): dynamic = with(ReactComponents) {
    buildElements {
        playerCard(PlayerCardProps(
                tribeId = TribeId(props.tribeId.unsafeCast<String>()),
                player = props.player.unsafeCast<Json>().toPlayer(),
                className = props.className.unsafeCast<String?>(),
                pathSetter = props.pathSetter.unsafeCast<Function1<String, Unit>>(),
                size = props.size.unsafeCast<Int>(),
                onClick = props.onClick.unsafeCast<Function1<Event, Unit>>(),
                disabled = props.disabled.unsafeCast<Boolean?>() ?: false
        ))
    }
}


@Suppress("unused")
@JsName("PlayerRoster")
fun playerRosterJs(props: dynamic): dynamic = buildElements {
    with(ReactComponents) {
        element(playerRoster,
                PlayerRosterProps(
                        tribeId = props.tribeId.unsafeCast<String>().let(::TribeId),
                        players = props.players.unsafeCast<Array<Json>>().map { it.toPlayer() },
                        label = props.label.unsafeCast<String?>(),
                        className = props.className.unsafeCast<String?>(),
                        pathSetter = props.pathSetter.unsafeCast<Function1<String, Unit>>()
                )
        )
    }
}

@Suppress("unused")
@JsName("RetiredPlayers")
fun retiredPlayersJs(props: dynamic): dynamic = buildElements {
    with(ReactComponents) {
        element(retiredPlayers, RetiredPlayersProps(
                tribe = props.tribe.unsafeCast<Json>().toTribe(),
                retiredPlayers = props.retiredPlayers.unsafeCast<Array<Json>>().map { it.toPlayer() },
                pathSetter = props.pathSetter.unsafeCast<Function1<String, Unit>>()
        ))
    }
}

@Suppress("unused")
@JsName("ServerMessage")
fun serverMessageJs(props: dynamic): dynamic = buildElements {
    element(serverMessage, ServerMessageProps(
            tribeId = props.tribeId.unsafeCast<String>().let(::TribeId),
            useSsl = props.useSsl.unsafeCast<Boolean>()
    ))
}