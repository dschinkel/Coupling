package com.zegreatrob.coupling.server.entity.tribe

import com.zegreatrob.coupling.common.entity.tribe.KtTribe
import com.zegreatrob.coupling.common.entity.tribe.PairingRule
import com.zegreatrob.coupling.common.entity.tribe.PairingRule.Companion.toValue
import com.zegreatrob.coupling.common.entity.tribe.TribeId
import com.zegreatrob.coupling.server.DbRecordLoadSyntax
import com.zegreatrob.coupling.server.DbRecordSaveSyntax
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.js.Json
import kotlin.js.json

interface MongoTribeRepository : TribeRepository, DbRecordSaveSyntax, DbRecordLoadSyntax {

    val jsRepository: dynamic

    override suspend fun save(tribe: KtTribe) = tribe.toDbJson()
            .let {
                it.save(jsRepository.tribesCollection)
            }

    override fun getTribeAsync(tribeId: TribeId): Deferred<KtTribe?> = GlobalScope.async {
        findByQuery(json("id" to tribeId.value), jsRepository.tribesCollection)
                .firstOrNull()
                ?.toTribe()
    }

    override fun getTribesAsync(): Deferred<List<KtTribe>> = GlobalScope.async {
        findByQuery(json(), jsRepository.tribesCollection)
                .map { it.toTribe() }
    }

    private fun KtTribe.toDbJson() = json(
            "id" to id.value,
            "pairingRule" to toValue(pairingRule),
            "name" to name,
            "email" to email,
            "defaultBadgeName" to defaultBadgeName,
            "alternateBadgeName" to alternateBadgeName,
            "badgesEnabled" to badgesEnabled
    )

    private fun Json.toTribe(): KtTribe = KtTribe(
            id = TribeId(this["id"].toString()),
            pairingRule = PairingRule.fromValue(this["pairingRule"] as? Int),
            name = this["name"]?.toString(),
            email = this["email"]?.toString(),
            defaultBadgeName = this["defaultBadgeName"]?.toString(),
            alternateBadgeName = this["alternateBadgeName"]?.toString(),
            badgesEnabled = this["badgesEnabled"]?.unsafeCast<Boolean>() ?: false
    )

}
