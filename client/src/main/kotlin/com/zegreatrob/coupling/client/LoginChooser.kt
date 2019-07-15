package com.zegreatrob.coupling.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.html.js.onClickFunction
import loadStyles
import react.RProps
import react.buildElement
import react.dom.div
import kotlin.browser.window

interface LoginChooserCss {
    val className: String
}

//@JsModule("GoogleSignIn")
//@JsNonModule
//private external val GoogleSignIn: dynamic

interface LoginChooserRenderer : ReactComponentRenderer, GoogleSignIn {

    companion object {
        private val styles = loadStyles<LoginChooserCss>("LoginChooser")
    }

    val loginChooser
        get() = {
            buildElement {
                div(classes = styles.className) {
                    div {
                        div(classes = "google-login super white button") {
                            attrs { onClickFunction = { GlobalScope.promise { signIn() } } }
                            +"Google"
                        }
                    }
                    div {
                        div(classes = "ms-login super blue button") {
                            attrs { onClickFunction = { window.location.pathname = "/microsoft-login" } }
                            +"Microsoft"
                        }
                    }
                }
            }
        }.unsafeCast<RFunction<RProps>>()

}
