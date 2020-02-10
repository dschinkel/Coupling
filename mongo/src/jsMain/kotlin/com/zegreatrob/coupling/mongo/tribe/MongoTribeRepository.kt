package com.zegreatrob.coupling.mongo.tribe

import com.zegreatrob.coupling.model.tribe.PairingRule
import com.zegreatrob.coupling.model.tribe.PairingRule.Companion.toValue
import com.zegreatrob.coupling.model.tribe.Tribe
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.mongo.DbRecordDeleteSyntax
import com.zegreatrob.coupling.mongo.DbRecordLoadSyntax
import com.zegreatrob.coupling.mongo.DbRecordSaveSyntax
import com.zegreatrob.coupling.repository.tribe.TribeRepository
import kotlin.js.Json
import kotlin.js.json

interface MongoTribeRepository : TribeRepository, DbRecordSaveSyntax, DbRecordLoadSyntax, DbRecordDeleteSyntax {

    val jsRepository: dynamic

    val tribesCollection: dynamic get() = jsRepository.tribesCollection

    override suspend fun save(tribe: Tribe) = tribe.toDbJson()
        .save(tribesCollection)

    override suspend fun delete(tribeId: TribeId) = deleteEntity(
        id = tribeId.value,
        collection = tribesCollection,
        entityName = "Tribe",
        toDomain = { toTribe() },
        toDbJson = { toDbJson() },
        usesRawId = false
    )

    override suspend fun getTribe(tribeId: TribeId): Tribe? = findByQuery(
        json("id" to tribeId.value),
        tribesCollection
    )
        .firstOrNull()
        ?.toTribe()

    override suspend fun getTribes(): List<Tribe> = findByQuery(json(), tribesCollection)
        .map { it.toTribe() }

    private fun Tribe.toDbJson() = json(
        "id" to id.value,
        "pairingRule" to toValue(pairingRule),
        "name" to name,
        "email" to email,
        "defaultBadgeName" to defaultBadgeName,
        "alternateBadgeName" to alternateBadgeName,
        "badgesEnabled" to badgesEnabled,
        "callSignsEnabled" to callSignsEnabled,
        "animationsEnabled" to animationEnabled,
        "animationSpeed" to animationSpeed
    )

    private fun Json.toTribe(): Tribe =
        Tribe(
            id = TribeId(this["id"].toString()),
            pairingRule = PairingRule.fromValue(this["pairingRule"] as? Int),
            name = this["name"]?.toString(),
            email = this["email"]?.toString(),
            defaultBadgeName = this["defaultBadgeName"]?.toString(),
            alternateBadgeName = this["alternateBadgeName"]?.toString(),
            badgesEnabled = this["badgesEnabled"]?.unsafeCast<Boolean>() ?: false,
            callSignsEnabled = this["callSignsEnabled"]?.unsafeCast<Boolean>() ?: false,
            animationEnabled = this["animationsEnabled"]?.unsafeCast<Boolean>() ?: true,
            animationSpeed = this["animationSpeed"]?.unsafeCast<Double>() ?: 1.0
        )

}

