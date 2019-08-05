package com.zegreatrob.coupling.client.pairassignments

import com.zegreatrob.coupling.client.external.axios.axios
import com.zegreatrob.coupling.client.external.axios.getList
import com.zegreatrob.coupling.common.entity.pairassignmentdocument.PairAssignmentDocument
import com.zegreatrob.coupling.common.entity.tribe.TribeId
import com.zegreatrob.coupling.common.toPairAssignmentDocument
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asDeferred
import kotlin.js.Json

interface GetPairAssignmentListSyntax {

    fun getPairAssignmentListAsync(tribeId: TribeId): Deferred<List<PairAssignmentDocument>> = axios.getList("/api/${tribeId.value}/history")
            .then { it.map(Json::toPairAssignmentDocument) }
            .asDeferred()

}