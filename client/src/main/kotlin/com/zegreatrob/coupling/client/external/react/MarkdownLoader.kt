package com.zegreatrob.coupling.client.external.react

fun loadMarkdown(@Suppress("UNUSED_PARAMETER") name: String): String {
    return js("require('com/zegreatrob/coupling/client/'+ name +'.md')").default.toString()
}
