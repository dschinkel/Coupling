package com.zegreatrob.coupling.client.external.reactmarkdown

import react.RClass
import react.RProps

@JsModule("react-markdown")
external val reactMarkdown: RClass<ReactMarkdownProps>

external interface ReactMarkdownProps : RProps {
    var source: String
}
