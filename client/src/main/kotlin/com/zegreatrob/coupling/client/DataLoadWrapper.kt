package com.zegreatrob.coupling.client

import kotlinx.coroutines.*
import kotlinx.html.classes
import react.RBuilder
import react.RProps
import react.buildElement
import react.dom.div


@JsModule("AnimationContext")
@JsNonModule
private external val animationContextModule: dynamic

val animationContextConsumer = animationContextModule.default.Consumer.unsafeCast<Any>()

inline fun <reified P : RProps> dataLoadWrapper(wrappedComponentProvider: ComponentProvider<P>): ComponentProvider<DataLoadProps<P>> =
        object : ComponentProvider<DataLoadProps<P>>(), ComponentBuilder<DataLoadProps<P>>, ScopeProvider {

            override fun build() = reactFunctionComponent<DataLoadProps<P>> { props ->
                val (data, setData) = useState<P?>(null)

                val (animationState, setAnimationState) = useState(AnimationState.Start)
                val shouldStartAnimation = data != null && animationState === AnimationState.Start

                props.getDataAsync.invokeOnScope(setData)

                componentWithFunctionChildren(animationContextConsumer) { animationsDisabled: Boolean ->
                    buildElement {
                        div {
                            attrs {
                                classes += "view-frame"
                                if (shouldStartAnimation && !animationsDisabled) {
                                    classes += "ng-enter"
                                }
                                this["onAnimationEnd"] = { setAnimationState(AnimationState.Stop) }
                            }
                            if (data != null) {
                                wrappedComponent(data)
                            }
                        }
                    }!!
                }
            }

            private fun (suspend (ReloadFunction) -> P).invokeOnScope(setData: (P?) -> Unit) {
                val (scope) = useState { buildScope() + CoroutineName("Data Load") }
                useEffectWithCleanup(arrayOf()) {
                    { scope.cancel() }
                }

                val (loadingJob, setLoadingJob) = useState<Job?>(null)

                if (loadingJob == null) {
                    val reloadFunction = { setData(null); setLoadingJob(null) }
                    setLoadingJob(
                            scope.launch {
                                setData(this@invokeOnScope.invoke(reloadFunction))
                            }
                    )
                }
            }

            private fun RBuilder.wrappedComponent(props: P) = wrappedComponentProvider.captor(this)(props)

        }

enum class AnimationState {
    Start, Stop
}

typealias ReloadFunction = () -> Unit

data class DataLoadProps<P : RProps>(val getDataAsync: suspend (ReloadFunction) -> P) : RProps