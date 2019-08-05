package com.zegreatrob.coupling.client.tribe

import com.zegreatrob.coupling.client.Coupling
import com.zegreatrob.coupling.common.Action
import com.zegreatrob.coupling.common.ActionLoggingSyntax
import com.zegreatrob.coupling.common.entity.tribe.TribeId

data class TribeQuery(val tribeId: TribeId, val coupling: Coupling) : Action

interface TribeQueryDispatcher : ActionLoggingSyntax, GetTribeSyntax {
    suspend fun TribeQuery.perform() = logAsync { coupling.getTribeAsync(tribeId).await() }
}
