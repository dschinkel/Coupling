package com.zegreatrob.coupling.server.entity.tribe

import com.zegreatrob.coupling.common.entity.tribe.TribeId

interface TribeIdGetSyntax {
    val tribeRepository: TribeGet
    fun TribeId.loadAsync() = tribeRepository.getTribeAsync(this)
}