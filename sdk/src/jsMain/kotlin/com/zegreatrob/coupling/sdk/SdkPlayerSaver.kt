package com.zegreatrob.coupling.sdk

import com.zegreatrob.coupling.json.toJson
import com.zegreatrob.coupling.model.player.PlayerSaver
import com.zegreatrob.coupling.model.player.TribeIdPlayer

interface SdkPlayerSaver : PlayerSaver, AxiosSyntax {
    override suspend fun save(tribeIdPlayer: TribeIdPlayer) {
        val (tribeId, player) = tribeIdPlayer
        axios.postAsync<Unit>("/api/${tribeId.value}/players/", player.toJson()).await()
    }
}