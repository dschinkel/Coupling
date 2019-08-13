package com.zegreatrob.coupling.client.external.react

import com.zegreatrob.coupling.client.loadStyles
import kotlinext.js.jsObject
import kotlinx.coroutines.*
import org.w3c.dom.Node
import react.*
import kotlin.reflect.KClass

@JsModule("react")
private external val React: dynamic

@JsModule("core-js/features/object/assign")

external fun <T, R : T> objectAssign(dest: R, vararg src: T): R

fun <T> useRef(default: T?) = React.useRef(default).unsafeCast<RReadableRef<T>>()

fun useLayoutEffect(callback: () -> Unit) {
    React.useLayoutEffect {
        callback()
        undefined
    }
}

fun useEffect(callback: () -> Unit) {
    React.useEffect {
        callback()
        undefined
    }
}

fun useEffectWithCleanup(dependencies: Array<Any>? = null, callback: () -> () -> Unit) {
    React.useEffect({ return@useEffect callback() }, dependencies)
}

fun <T> useState(default: T): StateValueContent<T> {
    val stateArray = React.useState(default)
    return StateValueContent(
        value = stateArray[0].unsafeCast<T>(),
        setter = stateArray[1].unsafeCast<(T) -> Unit>()
    )
}

fun <T> useStateWithSetterFunction(default: T): StateValueContentWithSetterFunction<T> {
    val stateArray = React.useState(default)
    return StateValueContentWithSetterFunction(
        value = stateArray[0].unsafeCast<T>(),
        setter = stateArray[1].unsafeCast<((T) -> T) -> Unit>()
    )
}

data class StateValueContentWithSetterFunction<T>(val value: T, val setter: ((T) -> T) -> Unit)

fun <T> useState(default: () -> T): StateValueContent<T> {
    val stateArray = React.useState(default)
    return StateValueContent(
        value = stateArray[0].unsafeCast<T>(),
        setter = stateArray[1].unsafeCast<(T) -> Unit>()
    )
}

fun <T> RBuilder.consumer(type: RConsumer<T>, children: RBuilder.(T) -> ReactElement) = child(
    React.createElement(type, jsObject {}) { value: T ->
        buildElement {
            children(value)
        }
    }
        .unsafeCast<ReactElement>()
)

interface RFunction<P : RProps> : RClass<P>

data class StateValueContent<T>(val value: T, val setter: (T) -> Unit)

fun <P : RProps> RBuilder.element(
    clazz: RClass<P>,
    props: P,
    key: String? = null,
    ref: RReadableRef<Node>? = null,
    handler: RHandler<P> = {}
): ReactElement {
    key?.let { props.key = it }
    ref?.let { props.ref = ref }
    return child(
        type = clazz,
        props = props,
        handler = handler
    )
}

inline fun <reified P : RProps> reactFunctionComponent(noinline builder: (props: P) -> ReactElement) =
    ReactFunctionComponent(P::class, builder)

class ReactFunctionComponent<P : RProps>(
    private val clazz: KClass<P>,
    private val builder: (props: P) -> ReactElement
) {
    val rFunction by kotlin.lazy {
        { props: P ->
            @Suppress("UNUSED_VARIABLE") val jsClass = clazz.js.unsafeCast<P>()
            builder(
                if (props::class.js == jsClass) {
                    props
                } else {
                    val newProps = js("new jsClass()")
                    objectAssign(newProps, props)
                    newProps.unsafeCast<P>()
                }
            )
        }.unsafeCast<RFunction<P>>()
    }

}

fun reactElement(handler: RBuilder.() -> Unit): ReactElement = buildElement(handler)!!

fun <P : RProps> RBuilder.component(
    component: ReactFunctionComponent<P>,
    props: P,
    key: String? = null,
    ref: RReadableRef<Node>? = null,
    handler: RHandler<P> = {}
) =
    element(component.rFunction, props, key, ref, handler)

inline fun <reified P : RProps, S> styledComponent(
    styleName: String,
    crossinline builder: PropsStylesBuilder<P, S>.() -> ReactElement
): ReactFunctionComponent<P> {
    val styles = loadStyles<S>(styleName)

    return reactFunctionComponent { props: P ->
        PropsStylesBuilder(props, styles)
            .handle(builder)
    }
}

class PropsBuilder<P>(
    val props: P
) {
    inline fun handle(builder: PropsBuilder<P>.() -> ReactElement) = builder()
}

class PropsStylesBuilder<P, S>(
    val props: P,
    val styles: S
) {
    inline fun handle(builder: PropsStylesBuilder<P, S>.() -> ReactElement) = builder()
}

class ScopedPropsStylesBuilder<P, S>(
    val props: P,
    val styles: S,
    val scope: CoroutineScope
) {
    inline fun handle(builder: ScopedPropsStylesBuilder<P, S>.() -> ReactElement) = builder()
}

inline fun <reified P : RProps, S> ScopeProvider.styledComponent(
    styleName: String,
    crossinline builder: ScopedPropsStylesBuilder<P, S>.() -> ReactElement
): ReactFunctionComponent<P> {
    val styles = loadStyles<S>(styleName)

    return reactFunctionComponent { props: P ->
        val (scope) = useState { buildScope() + CoroutineName(styleName) }
        useEffectWithCleanup(arrayOf()) {
            { scope.cancel() }
        }
        ScopedPropsStylesBuilder(props, styles, scope)
            .handle(builder)
    }
}

object EmptyProps : RProps

external interface SimpleStyle {
    val className: String
}

interface ScopeProvider {
    fun buildScope(): CoroutineScope = MainScope()
}