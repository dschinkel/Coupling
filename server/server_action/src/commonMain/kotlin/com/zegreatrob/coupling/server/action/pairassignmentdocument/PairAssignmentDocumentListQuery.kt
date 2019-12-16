package com.zegreatrob.coupling.server.action.pairassignmentdocument

import com.zegreatrob.coupling.action.Action
import com.zegreatrob.coupling.action.ActionLoggingSyntax
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.repository.pairassignmentdocument.PairAssignmentDocumentGetter

data class PairAssignmentDocumentListQuery(val tribeId: TribeId) : Action

interface PairAssignmentDocumentListQueryDispatcher : ActionLoggingSyntax, TribeIdPairAssignmentDocumentGetSyntax {
    suspend fun PairAssignmentDocumentListQuery.perform() = logAsync { tribeId.loadPairAssignmentDocumentList() }
}

interface TribeIdPairAssignmentDocumentGetSyntax {
    val pairAssignmentDocumentRepository: PairAssignmentDocumentGetter
    suspend fun TribeId.loadPairAssignmentDocumentList() = pairAssignmentDocumentRepository.getPairAssignments(this)
}
