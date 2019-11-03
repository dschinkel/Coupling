package com.zegreatrob.coupling.model.pairassignmentdocument

import com.zegreatrob.coupling.model.tribe.TribeId

interface TribeIdHistorySyntax {
    val pairAssignmentDocumentRepository: PairAssignmentDocumentGetter
    fun TribeId.getHistoryAsync() = pairAssignmentDocumentRepository.getPairAssignmentsAsync(this)
}