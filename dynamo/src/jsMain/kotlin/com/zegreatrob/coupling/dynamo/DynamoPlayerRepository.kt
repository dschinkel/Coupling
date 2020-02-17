package com.zegreatrob.coupling.dynamo

import com.zegreatrob.coupling.model.Record
import com.zegreatrob.coupling.model.player.TribeIdPlayer
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.model.user.UserEmailSyntax
import com.zegreatrob.coupling.repository.player.PlayerRepository
import kotlin.js.Json

class DynamoPlayerRepository private constructor(override val userEmail: String) : PlayerRepository, UserEmailSyntax,
    DynamoPlayerJsonMapping {

    companion object : DynamoRepositoryCreatorSyntax<DynamoPlayerRepository>, DynamoDBSyntax by DynamoDbProvider,
        TribeCreateTableParamProvider,
        DynamoItemPutSyntax,
        TribeIdDynamoItemListGetSyntax,
        DynamoItemDeleteSyntax {
        override val construct = ::DynamoPlayerRepository
        override val tableName: String = "PLAYER"
    }

    override suspend fun getPlayers(tribeId: TribeId) = tribeId.scanForItemList().map {
        val player = it.toPlayer()
        it.sdlfkjsd(TribeIdPlayer(tribeId, player))
    }

    private fun <T> Json.sdlfkjsd(data: T) = Record(
        data,
        getDynamoDateTimeValue("timestamp")?.utc!!,
        false,
        getDynamoStringValue("modifyingUserEmail")!!
    )

    override suspend fun save(tribeIdPlayer: TribeIdPlayer) = performPutItem(tribeIdPlayer.toDynamoJson())

    override suspend fun deletePlayer(tribeId: TribeId, playerId: String) = performDelete(playerId, tribeId)

    override suspend fun getDeleted(tribeId: TribeId) = tribeId.scanForDeletedItemList().map { it.toPlayer() }

}
