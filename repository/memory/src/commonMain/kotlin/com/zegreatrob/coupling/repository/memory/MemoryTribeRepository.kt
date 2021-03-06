package com.zegreatrob.coupling.repository.memory

import com.soywiz.klock.TimeProvider
import com.zegreatrob.coupling.model.Record
import com.zegreatrob.coupling.model.tribe.Tribe
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.repository.tribe.TribeRepository

class MemoryTribeRepository(override val userEmail: String, override val clock: TimeProvider) : TribeRepository,
    TypeRecordSyntax<Tribe>, RecordSaveSyntax<Tribe> {

    override var records = emptyList<Record<Tribe>>()

    override suspend fun save(tribe: Tribe) = tribe.record().save()

    override suspend fun getTribeRecord(tribeId: TribeId) = tribeId.findTribe()
        ?.let { if (it.isDeleted) null else it }

    override suspend fun getTribes() = recordList()
        .filterNot { it.isDeleted }

    private fun recordList() = records.groupBy { (tribe) -> tribe.id }
        .map { it.value.last() }

    override suspend fun delete(tribeId: TribeId) = tribeId.findTribe()?.data.deleteRecord()

    private fun Tribe?.deleteRecord() = if (this == null) {
        false
    } else {
        deletionRecord().save()
        true
    }

    private fun TribeId.findTribe() = recordList()
        .firstOrNull { it.data.id == this }

}
