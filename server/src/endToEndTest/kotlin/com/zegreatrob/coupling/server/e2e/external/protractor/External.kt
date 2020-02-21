@file:JsModule("protractor")

package com.zegreatrob.coupling.server.e2e.external.protractor

import kotlin.js.Promise

external val browser: Browser

external interface Browser {

    val baseUrl: String

    fun get(url: String): Promise<Unit>
    fun wait(condition: () -> Promise<Boolean>, timeout: Int): Promise<Unit>
    fun switchTo(): SwitchTo

}

external interface SwitchTo {
    fun alert(): Promise<ProtractorAlert>
}

external interface ProtractorAlert {
    fun accept(): Promise<Unit>
}

external fun element(by: ProtractorBy): ElementSelector

external interface ElementSelector {
    fun clear(): Promise<Unit>
    fun sendKeys(value: String): Promise<Unit>
    fun click(): Promise<Unit>
    fun isPresent(): Promise<Boolean>
    fun all(by: ProtractorBy): ElementSelector

    fun <T> map(transform: (ElementSelector) -> Promise<T>): Promise<Array<T>>
    fun getText(): Promise<String>
    fun getAttribute(name: String): Promise<String>
}

external object By {
    fun id(id: String): ProtractorBy
    fun className(className: String): ProtractorBy
}