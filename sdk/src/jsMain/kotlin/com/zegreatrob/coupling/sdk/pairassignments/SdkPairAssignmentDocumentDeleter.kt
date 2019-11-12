package com.zegreatrob.coupling.sdk.pairassignments

import com.zegreatrob.coupling.model.pairassignmentdocument.PairAssignmentDocumentDeleter
import com.zegreatrob.coupling.model.pairassignmentdocument.PairAssignmentDocumentId
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.sdk.AxiosSyntax
import kotlinx.coroutines.asDeferred
import kotlin.js.Promise

interface SdkPairAssignmentDocumentDeleter : PairAssignmentDocumentDeleter, AxiosSyntax {
    override suspend fun delete(
        tribeId: TribeId,
        pairAssignmentDocumentId: PairAssignmentDocumentId
    ): Boolean {
        axios.delete("/api/${tribeId.value}/history/${pairAssignmentDocumentId.value}")
            .unsafeCast<Promise<Unit>>()
            .asDeferred()
            .await()
        return true
    }
}