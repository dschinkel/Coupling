package com.zegreatrob.coupling.client.pin

import com.zegreatrob.coupling.client.external.react.*
import com.zegreatrob.coupling.model.pin.Pin
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RHandler
import react.RProps
import react.dom.i
import react.dom.span
import styled.css
import styled.styledDiv

enum class PinButtonScale(val faTag: String, val factor: Double) {
    Normal("fa-3x", 3.0), Large("fa-10x", 10.0), Small("fa-1x", 1.0), ExtraSmall("fa-xs", 0.75)
}

data class PinButtonProps(
    val pin: Pin,
    val scale: PinButtonScale = PinButtonScale.Normal,
    val className: String = "",
    val showTooltip: Boolean = true,
    val onClick: () -> Unit = {}
) : RProps


val RBuilder.lolololButton get() = ::PinButtonProps.extend(::mehmeh).compose(renderMagically(PinButton))

fun <P : RProps> mehmeh(pinButtonProps: P, key: String? = null, handler: RHandler<P> = {}) = Triple(
    pinButtonProps,
    key,
    handler
)

inline fun <reified P : RProps, C : Function<P>> RBuilder.renderMagically(component: RComponent<P>) =
    { (props: P, key: String?): Triple<P, String?, RHandler<P>> ->
        child(component.component.rFunction, props, key)
    }

infix fun <A, B, C, D, E, F, G> ((A, B, C, D, E) -> F).compose(after: (F) -> G) =
    { a: A, b: B, c: C, d: D, e: E -> after(this(a, b, c, d, e)) }

infix fun <A, B, C, D, E, F, G, H, I> ((A, B, C, D, E, F, G) -> H).compose(after: (H) -> I) =
    { a: A, b: B, c: C, d: D, e: E, f: F, g: G -> after(this(a, b, c, d, e, f, g)) }


infix fun <A, B, C, D, E, F, G, H, I> ((A, B, C, D, E) -> F).extend(after: (F, G, H) -> I) =
    { a: A, b: B, c: C, d: D, e: E, g: G, h: H -> after(this(a, b, c, d, e), g, h) }

object PinButton : FRComponent<PinButtonProps>(provider()) {

    fun RBuilder.pinButton(
        pin: Pin,
        scale: PinButtonScale = PinButtonScale.Small,
        className: String = "",
        onClick: () -> Unit = {},
        key: String? = null,
        showTooltip: Boolean = true
    ) = child(
        PinButton(PinButtonProps(pin, scale, className, showTooltip, onClick), key = key)
    )

    override fun render(props: PinButtonProps) = reactElement {
        val (pin, scale) = props
        val styles = useStyles("pin/PinButton")

        val kFunction5 = ::PinButtonProps

        kFunction5(pin)

        PinButtonProps(pin)

        lolololButton(pin)


        styledDiv {
            attrs {
                classes += listOf(props.className, styles.className)
                css { scaledStyles(scale) }
                onClickFunction = { props.onClick() }
            }

            if (props.showTooltip) {
                span(classes = styles["tooltip"]) { +(pin.name ?: "") }
            }
            i(scale.faTag) { attrs { classes += targetIcon(pin) } }
        }
    }

    private fun CSSBuilder.scaledStyles(scale: PinButtonScale) {
        padding((3.2 * scale.factor).px)
        borderWidth = (2 * scale.factor).px
        borderRadius = (12 * scale.factor).px
        lineHeight = LineHeight((4.6 * scale.factor).px.value)
        height = (14 * scale.factor).px
        width = (14 * scale.factor).px
    }

    private fun targetIcon(pin: Pin): String {
        var targetIcon = if (pin.icon.isNullOrEmpty()) "fa-skull" else pin.icon!!
        if (!targetIcon.startsWith("fa")) {
            targetIcon = "fa-$targetIcon"
        }
        var fontAwesomeStyle = "fa"
        val split = targetIcon.split(" ")
        if (split.size > 1) {
            fontAwesomeStyle = ""
        }

        targetIcon = "$fontAwesomeStyle $targetIcon"
        return targetIcon
    }
}