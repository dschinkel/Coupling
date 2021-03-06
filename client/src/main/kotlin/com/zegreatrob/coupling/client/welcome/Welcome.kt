package com.zegreatrob.coupling.client.welcome

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.client.fitty.fitty
import com.zegreatrob.coupling.client.player.PlayerCardProps
import com.zegreatrob.coupling.client.player.playerCard
import com.zegreatrob.coupling.model.pairassignmentdocument.CouplingPair
import com.zegreatrob.coupling.model.pairassignmentdocument.pairOf
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.TribeId
import kotlinx.coroutines.launch
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import org.w3c.dom.Node
import react.RBuilder
import react.ReactElement
import react.dom.a
import react.dom.div
import react.dom.span

object Welcome : RComponent<EmptyProps>(provider()), WelcomeRenderer

external interface WelcomeStyles {
    val className: String
    val hidden: String
    val welcome: String
    val welcomeProverb: String
    val welcomeTitle: String
    val welcomePair: String
    val playerCard: String
    val enterButtonContainer: String
    val enterButton: String
}

val welcomeTribeId = TribeId("welcome")

private val candidates = listOf(
    WelcomeCardSet(
        left = Card(name = "Frodo", imagePath = "frodo-icon.png"),
        right = Card(name = "Sam", imagePath = "samwise-icon.png"),
        proverb = "Together, climb mountains."
    ),
    WelcomeCardSet(
        left = Card(name = "Batman", imagePath = "grayson-icon.png"),
        right = Card(name = "Robin", imagePath = "wayne-icon.png"),
        proverb = "Clean up the city, together."
    ),
    WelcomeCardSet(
        left = Card(name = "Rosie", imagePath = "rosie-icon.png"),
        right = Card(name = "Wendy", imagePath = "wendy-icon.png"),
        proverb = "Team up. Get things done."
    )
)

private data class WelcomeCardSet(val left: Card, val right: Card, val proverb: String)

private data class Card(val name: String, val imagePath: String)

typealias WelcomeContext = ScopedStyledRContext<EmptyProps, WelcomeStyles>

interface WelcomeRenderer : ScopedStyledComponentRenderer<EmptyProps, WelcomeStyles>, RandomProvider,
    LoginChooserRenderer {

    override val componentPath get() = "Welcome"

    override fun WelcomeContext.render(): ReactElement {
        val (show, setShow) = useState(false)

        if (!show) {
            scope.launch { setShow(true) }
        }

        val hiddenTag = if (show) "" else styles.hidden

        return reactElement {
            div(classes = styles.className) {
                attrs { classes += hiddenTag }
                div {
                    welcomeSplash(hiddenTag)()
                }
                div {
                    comeOnIn(hiddenTag)()
                }
            }
        }
    }

    private fun WelcomeContext.welcomeSplash(hiddenTag: String): RBuilder.() -> ReactElement {
        val (pairAndProverb) = useState { choosePairAndProverb() }

        val (pair, proverb) = pairAndProverb

        return {
            span(classes = styles.welcome) {
                welcomeTitle()()
                div {
                    welcomePair(pair)()
                }
                div(classes = styles.welcomeProverb) {
                    attrs { classes += hiddenTag }
                    +proverb
                }
            }
        }
    }

    private fun choosePairAndProverb() = chooseWelcomeCardSet().toPairAndProverb()

    private fun WelcomeCardSet.toPairAndProverb() = pairOf(
        left.toPlayer(),
        right.toPlayer()
    ) to proverb

    private fun chooseWelcomeCardSet() = candidates.random()

    private fun Card.toPlayer() = Player(
        id = name,
        name = name,
        imageURL = "/images/icons/players/$imagePath"
    )

    private fun WelcomeContext.welcomeTitle(): RBuilder.() -> ReactElement {
        val welcomeTitleRef = useRef<Node>(null)

        useLayoutEffect {
            welcomeTitleRef.current?.fitty(maxFontHeight = 75.0, minFontHeight = 5.0, multiLine = false)
        }
        return {
            div(classes = styles.welcomeTitle) {
                attrs { ref = welcomeTitleRef }
                +"Coupling!"
            }
        }
    }

    private fun WelcomeContext.welcomePair(pair: CouplingPair.Double): RBuilder.() -> ReactElement = {
        div(classes = styles.welcomePair) {
            playerCard(
                PlayerCardProps(
                    tribeId = welcomeTribeId,
                    player = pair.player1,
                    className = "left ${styles.playerCard}",
                    size = 100,
                    headerDisabled = true
                )
            )
            playerCard(
                PlayerCardProps(
                    tribeId = welcomeTribeId,
                    player = pair.player2,
                    className = "right ${styles.playerCard}",
                    size = 100,
                    headerDisabled = true
                )
            )
        }
    }

    private fun WelcomeContext.comeOnIn(hiddenTag: String): RBuilder.() -> ReactElement {
        val (showLoginChooser, setShowLoginChooser) = useState(false)

        return {
            div(classes = styles.enterButtonContainer) {
                if (showLoginChooser) {
                    loginChooser()
                } else {
                    a(classes = "enter-button super pink button") {
                        attrs {
                            classes += styles.enterButton
                            classes += hiddenTag
                            onClickFunction = { setShowLoginChooser(true) }
                            target = "_self"
                        }
                        +"Come on in!"
                    }
                }
            }
        }
    }
}

