package com.zegreatrob.coupling.client.pairassignments.spin

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.client.external.reactfliptoolkit.flipped
import com.zegreatrob.coupling.client.pairassignments.AssignedPair.assignedPair
import com.zegreatrob.coupling.client.pairassignments.PairAssignmentsHeader.pairAssignmentsHeader
import com.zegreatrob.coupling.client.player.PlayerCardProps
import com.zegreatrob.coupling.client.player.playerCard
import com.zegreatrob.coupling.model.pairassignmentdocument.PinnedCouplingPair
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.Tribe
import com.zegreatrob.coupling.model.tribe.TribeId
import kotlinx.css.Display
import kotlinx.css.Visibility
import kotlinx.css.display
import kotlinx.css.visibility
import react.RBuilder
import react.RProps
import react.ReactElement
import react.dom.div
import react.dom.key
import styled.css
import styled.styledDiv

data class SpinAnimationPanelProps(
    val tribe: Tribe,
    val rosteredPairAssignments: RosteredPairAssignments,
    val state: SpinAnimationState
) : RProps

val placeholderPlayer = Player("?", name = "Next...", callSignAdjective = "--------", callSignNoun = "--------")

data class SpinStateData(
    val rosterPlayers: List<Player>,
    val revealedPairs: List<PinnedCouplingPair>,
    val shownPlayer: Player?
)

object SpinAnimationPanel : FRComponent<SpinAnimationPanelProps>(provider()) {

    private val styles = useStyles("pairassignments/SpinAnimation")

    fun RBuilder.spinAnimation(
        tribe: Tribe,
        rosteredPairAssignments: RosteredPairAssignments,
        state: SpinAnimationState
    ) = child(
        SpinAnimationPanel.component.rFunction, SpinAnimationPanelProps(tribe, rosteredPairAssignments, state)
    )

    override fun render(props: SpinAnimationPanelProps): ReactElement {
        val state = props.state
        val tribe = props.tribe
        val pairAssignments = props.rosteredPairAssignments.pairAssignments
        val players = props.rosteredPairAssignments.selectedPlayers

        val (rosterPlayers, revealedPairs, shownPlayer) = state.stateData(players, pairAssignments)

        return reactElement {
            div(classes = styles.className) {
                pairAssignmentsHeader(pairAssignments)
                assignedPairs(tribe, revealedPairs)
                playerSpotlight(shownPlayer)
                playerRoster(rosterPlayers)
            }
        }
    }

    private fun RBuilder.playerSpotlight(shownPlayer: Player?) = div(classes = styles["playerSpotlight"]) {
        if (shownPlayer != null)
            flippedPlayer(shownPlayer)
        else
            placeholderPlayerCard()
    }

    private fun RBuilder.placeholderPlayerCard() = styledDiv {
        css { visibility = Visibility.hidden; display = Display.inlineBlock }
        flippedPlayer(placeholderPlayer)
    }

    private fun RBuilder.flippedPlayer(player: Player, key: String? = null) = flipped(player.id ?: "") {
        styledDiv {
            attrs { this.key = key ?: "" }
            css { display = Display.inlineBlock }
            playerCard(PlayerCardProps(TribeId(""), player))
        }
    }

    private fun RBuilder.assignedPairs(tribe: Tribe, revealedPairs: List<PinnedCouplingPair>) =
        div(classes = styles["pairAssignments"]) {
            revealedPairs.mapIndexed { index, it ->
                assignedPair(
                    tribe,
                    it,
                    { _, _, _ -> },
                    { _, _ -> },
                    false,
                    {},
                    key = "$index"
                )
            }
        }

    private fun RBuilder.playerRoster(players: List<Player>) = div(classes = styles["playerRoster"]) {
        players.map {
            if (it == placeholderPlayer) {
                placeholderPlayerCard()
            } else {
                flippedPlayer(it, key = it.id)
            }
        }
    }
}
