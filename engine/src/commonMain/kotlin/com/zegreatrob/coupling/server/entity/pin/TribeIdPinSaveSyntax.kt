package com.zegreatrob.coupling.server.entity.pin

import com.zegreatrob.coupling.common.entity.pin.TribeIdPin

interface TribeIdPinSaveSyntax {

    val pinRepository: PinSaver

    suspend fun TribeIdPin.save() = pinRepository.save(this)

}