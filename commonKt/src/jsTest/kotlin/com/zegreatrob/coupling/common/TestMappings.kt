package com.zegreatrob.coupling.common

import com.zegreatrob.coupling.core.json.toPairAssignmentDocument
import com.zegreatrob.coupling.core.json.toPlayer
import com.zegreatrob.coupling.core.json.toTribe
import kotlin.js.Json

actual fun loadJsonTribeSetup(fileResource: String): TribeSetup {
    return loadResource<Any>(fileResource).unsafeCast<Json>().let {
        TribeSetup(
                tribe = it["tribe"].unsafeCast<Json>().toTribe(),
                players = it["players"].unsafeCast<Array<Json>>().map { player -> player.toPlayer() },
                history = it["history"].unsafeCast<Array<Json>>().map { record -> record.toPairAssignmentDocument() }
        )
    }
}
