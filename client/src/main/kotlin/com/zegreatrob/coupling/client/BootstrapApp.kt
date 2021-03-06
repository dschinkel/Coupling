package com.zegreatrob.coupling.client

import com.zegreatrob.coupling.client.routing.CouplingRouter
import com.zegreatrob.coupling.client.routing.CouplingRouterProps
import com.zegreatrob.coupling.client.user.GoogleSignIn
import com.zegreatrob.coupling.logging.initializeJasmineLogging
import com.zegreatrob.coupling.sdk.Sdk
import com.zegreatrob.coupling.sdk.SdkSingleton
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.get
import react.createElement
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

object App : GoogleSignIn, Sdk by SdkSingleton {

    fun bootstrapApp() {
        initializeJasmineLogging(developmentMode = false)
        MainScope().launch {
            val isSignedIn = checkForSignedIn()
            val animationsDisabled = window.sessionStorage.getItem("animationDisabled") == "true"

            render(
                createElement(CouplingRouter.component.rFunction, CouplingRouterProps(isSignedIn, animationsDisabled)),
                document.getElementsByClassName("view-container")[0]
            )
        }
    }
}
