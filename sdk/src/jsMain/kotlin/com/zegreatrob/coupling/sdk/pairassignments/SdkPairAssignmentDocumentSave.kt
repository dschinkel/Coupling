package com.zegreatrob.coupling.sdk.pairassignments

import com.zegreatrob.coupling.json.toJson
import com.zegreatrob.coupling.model.pairassignmentdocument.TribeIdPairAssignmentDocument
import com.zegreatrob.coupling.repository.pairassignmentdocument.PairAssignmentDocumentSave
import com.zegreatrob.coupling.sdk.AxiosSyntax

interface SdkPairAssignmentDocumentSave : PairAssignmentDocumentSave, AxiosSyntax {
    override suspend fun save(tribeIdPairAssignmentDocument: TribeIdPairAssignmentDocument) {
        val (tribeId, pairAssignmentDocument) = tribeIdPairAssignmentDocument
        axios.postAsync<Unit>("/api/tribes/${tribeId.value}/history", pairAssignmentDocument.toJson())
            .await()
    }
}