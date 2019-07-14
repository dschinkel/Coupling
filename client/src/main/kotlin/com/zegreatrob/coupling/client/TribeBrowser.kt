package com.zegreatrob.coupling.client

import com.zegreatrob.coupling.common.entity.tribe.KtTribe
import kotlinx.html.classes
import loadStyles
import react.RProps
import react.dom.*

data class TribeBrowserProps(val tribe: KtTribe, val pathSetter: (String) -> Unit) : RProps

interface TribeBrowserCss {
    val className: String
    val statisticsButton: String
    val tribeSelectButton: String
    val logoutButton: String
}

val tribeBrowserStyles = loadStyles<TribeBrowserCss>("TribeBrowser")

val tribeBrowser = rFunction { props: TribeBrowserProps ->
    val (tribe, pathSetter) = props
    div(classes = tribeBrowserStyles.className) {
        span {
            element(tribeCard, TribeCardProps(tribe = tribe, pathSetter = pathSetter, size = 50))
            h1 { +(tribe.name ?: "") }
        }
        span {
            span {
                a(href = "/${tribe.id.value}/statistics", classes = "large gray button") {
                    attrs { classes += tribeBrowserStyles.statisticsButton }
                    span(classes = "icon-button-text") { +"Statistics" }
                }
                a(href = "/tribes/", classes = "large gray button") {
                    attrs { classes += tribeBrowserStyles.tribeSelectButton }
                    i(classes = "fa fa-arrow-circle-up") {}
                    span(classes = "icon-button-text") { +"Tribe select" }
                }
            }
            span {
                a(href = "/logout", classes = "large red button") {
                    attrs { classes += tribeBrowserStyles.logoutButton }
                    i(classes = "fa fa-sign-out") {}
                    span(classes = "icon-button-text") { +"Sign Out" }
                }
            }
        }
    }
}