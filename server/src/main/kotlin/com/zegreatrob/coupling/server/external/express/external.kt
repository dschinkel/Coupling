package com.zegreatrob.coupling.server.external.express

@JsModule("express")
@JsNonModule
external fun express(): Express

external interface Express {
    fun use(middlewareFunction: dynamic)
    fun set(key: String, value: dynamic)
    fun get(key: String): dynamic
    fun listen(port: Int, function: () -> Unit)

}
