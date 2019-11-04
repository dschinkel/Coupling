package com.zegreatrob.coupling.client.pairassignments

import com.zegreatrob.coupling.client.sdk.axios.axios.axios
import com.zegreatrob.coupling.model.pairassignmentdocument.PairAssignmentDocumentId
import com.zegreatrob.coupling.model.tribe.TribeId
import kotlinx.coroutines.asDeferred
import kotlin.js.Promise

interface PairAssignmentDocDeleteSyntax {

    fun deleteAsync(tribeId: TribeId, pairAssignmentDocId: PairAssignmentDocumentId) =
        axios.delete("/api/${tribeId.value}/history/${pairAssignmentDocId.value}")
            .unsafeCast<Promise<Unit>>()
            .asDeferred()

}
