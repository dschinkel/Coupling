package com.zegreatrob.coupling.client.pairassignments

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.client.external.reactfliptoolkit.flipped
import com.zegreatrob.coupling.client.external.reactfliptoolkit.flipper
import com.zegreatrob.coupling.client.pairassignments.AssignedPair.assignedPair
import com.zegreatrob.coupling.client.pairassignments.list.dateText
import com.zegreatrob.coupling.client.player.PlayerCardProps
import com.zegreatrob.coupling.client.player.playerCard
import com.zegreatrob.coupling.model.pairassignmentdocument.PairAssignmentDocument
import com.zegreatrob.coupling.model.pairassignmentdocument.PinnedCouplingPair
import com.zegreatrob.coupling.model.pairassignmentdocument.PinnedPlayer
import com.zegreatrob.coupling.model.pin.Pin
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.Tribe
import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RProps
import react.dom.a
import react.dom.button
import react.dom.div
import styled.css
import styled.styledDiv

data class CurrentPairAssignmentsPanelProps(
    val tribe: Tribe,
    val players: List<Player>,
    val pairAssignments: PairAssignmentDocument?,
    val onPlayerSwap: (String, PinnedPlayer, PinnedCouplingPair) -> Unit,
    val onPinDrop: (Pin, PinnedCouplingPair) -> Unit,
    val onSave: () -> Unit,
    val pathSetter: (String) -> Unit
) : RProps

object CurrentPairAssignmentsPanel : FRComponent<CurrentPairAssignmentsPanelProps>(provider()) {

    private val styles = useStyles("pairassignments/CurrentPairAssignmentsPanel")

    fun RBuilder.currentPairAssignments(
        tribe: Tribe,
        players: List<Player>,
        pairAssignments: PairAssignmentDocument?,
        onPlayerSwap: (String, PinnedPlayer, PinnedCouplingPair) -> Unit,
        onPinDrop: (Pin, PinnedCouplingPair) -> Unit,
        onSave: () -> Unit,
        pathSetter: (String) -> Unit
    ) = child(
        CurrentPairAssignmentsPanel.component.rFunction,
        CurrentPairAssignmentsPanelProps(tribe, players,pairAssignments, onPlayerSwap, onPinDrop, onSave, pathSetter)
    )

    override fun render(props: CurrentPairAssignmentsPanelProps) = with(props) {
        val (animateState, setAnimateState) = useState(false)

        reactElement {
            div(classes = styles.className) {
                if (pairAssignments == null) {
                    noPairsHeader()
                } else {
                    dateHeader(pairAssignments)
                    pairAssignmentList(
                        tribe,
                        players,
                        pairAssignments,
                        onPlayerSwap,
                        onPinDrop,
                        pathSetter,
                        animateState,
                        setAnimateState
                    )
                    saveButtonSection(pairAssignments, onSave)
                }
            }
        }
    }

    private fun RBuilder.noPairsHeader() = div(classes = styles["noPairsNotice"]) { +"No pair assignments yet!" }

    private fun RBuilder.dateHeader(pairAssignments: PairAssignmentDocument) = div {
        div {
            div(classes = styles["pairAssignmentsHeader"]) {
                +"Couples for ${pairAssignments.dateText()}"
            }
        }
    }

    private fun RBuilder.pairAssignmentList(
        tribe: Tribe,
        players: List<Player>,
        pairAssignments: PairAssignmentDocument,
        onPlayerSwap: (String, PinnedPlayer, PinnedCouplingPair) -> Unit,
        onPinDrop: (Pin, PinnedCouplingPair) -> Unit,
        pathSetter: (String) -> Unit,
        animateState: Boolean,
        setAnimateState: (Boolean) -> Unit
    ) = flipper(flipKey = pairAssignments.flipKey(animateState)) {
        div(classes = styles["pairAssignmentsContent"]) {

            if (animateState) {
                pairAssignments.pairs.mapIndexed { index, pair ->
                    assignedPair(tribe, pair, onPlayerSwap, onPinDrop, pairAssignments, pathSetter, key = "$index")
                }
            } else {
                players.map {
                    flipped(flipId = it.id ?: "") {
                        styledDiv {
                            css { display = Display.inlineBlock }
                            playerCard(PlayerCardProps(tribe.id, it, headerDisabled = true), key = it.id)
                        }
                    }
                }

            }

            button { attrs { onClickFunction = { setAnimateState(!animateState) } }; +"animate" }
        }
    }

    private fun PairAssignmentDocument.flipKey(animateState: Boolean) = pairs.joinToString(",") {
        "$animateState" +
                it.pins.joinToString(",") { pin -> pin._id ?: "" } +
                " " +
                it.players.joinToString { player -> player.player.id ?: "" }
    }


    private fun RBuilder.saveButtonSection(pairAssignments: PairAssignmentDocument, onSave: () -> Unit) = div {
        if (pairAssignments.isNotSaved()) {
            saveButton(onSave)
        }
    }

    private fun PairAssignmentDocument.isNotSaved() = id == null

    private fun RBuilder.saveButton(onSave: () -> Unit) = a(classes = "super green button") {
        attrs {
            classes += styles["saveButton"]
            onClickFunction = { onSave() }
        }
        +"Save!"
    }

}
