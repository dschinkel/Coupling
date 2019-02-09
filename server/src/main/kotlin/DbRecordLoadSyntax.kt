import com.soywiz.klock.internal.toDateTime
import kotlinx.coroutines.await
import kotlin.js.*

interface DbRecordLoadSyntax : JsonTimestampSyntax {

    suspend fun findByQuery(query: Json, collection: dynamic) = rawFindBy(query, collection)
            .await()
            .findLatestOfEachRecord()
            .filter { it["isDeleted"] != true }

    suspend fun findDeletedByQuery(tribeId: TribeId, collection: dynamic) = rawFindBy(
            json("tribe" to tribeId.value), collection
    )
            .await()
            .findLatestOfEachRecord()
            .filter { it["isDeleted"] == true }

    fun rawFindBy(query: Json, collection: dynamic) = collection.find(query).unsafeCast<Promise<Array<Json>>>()

    private fun Array<Json>.dbJsonArrayLoad() = findLatestOfEachRecord()
            .filter { it["isDeleted"] != true }

    private fun Array<Json>.findLatestOfEachRecord() = map { it.applyIdCorrection() }
            .groupBy { it["_id"].toString() }
            .map { it.value.latestByTimestamp() }
            .filterNotNull()

    fun Json.applyIdCorrection() = also {
        this["_id"] = this["id"].unsafeCast<String?>() ?: this["_id"]
    }

    fun List<Json>.latestByTimestamp() = sortedByDescending { it.timeStamp() }.firstOrNull()

    suspend fun getLatestRecordWithId(id: String, collection: dynamic) = getAllRecordsWithId(id, collection)
            .latestByTimestamp()

    private suspend fun getAllRecordsWithId(id: String, collection: dynamic) = listOf(
            rawFindBy(json("id" to id), collection),
            rawFindBy(json("_id" to id), collection)
    )
            .map { it.await().toList() }
            .flatten()
}

interface JsonTimestampSyntax {
    fun Json.timeStamp() = this["timestamp"]?.unsafeCast<Date>()?.toDateTime()
}