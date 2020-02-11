package com.zegreatrob.coupling.sdk

import com.benasher44.uuid.uuid4
import com.soywiz.klock.DateTime
import com.zegreatrob.coupling.model.pairassignmentdocument.*
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.coupling.model.tribe.Tribe
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.repository.pairassignmentdocument.PairAssignmentDocumentRepository
import com.zegreatrob.coupling.repositoryvalidation.PairAssignmentDocumentRepositoryValidator
import com.zegreatrob.coupling.sdk.SdkPlayerRepositoryTest.Companion.catchAxiosError
import com.zegreatrob.minassert.assertIsEqualTo
import com.zegreatrob.testmints.async.setupAsync
import com.zegreatrob.testmints.async.testAsync
import stubTribe
import kotlin.js.Json
import kotlin.test.Test

class SdkPairAssignmentDocumentRepositoryTest : PairAssignmentDocumentRepositoryValidator {

    override suspend fun withRepository(handler: suspend (PairAssignmentDocumentRepository, TribeId) -> Unit) {
        val sdk = authorizedSdk(username = "eT-user-${uuid4()}")
        val tribe = stubTribe()
        sdk.save(tribe)
        handler(sdk, tribe.id)
    }

    @Test
    fun postsThenDeleteThenGetWillNotReturnSavedAssignments() = testAsync {
        val sdk = authorizedSdk(username = "eT-user-${uuid4()}")
        setupAsync(object {
            val tribe = Tribe(TribeId(uuid4().toString()), name = "one")
            val pairAssignments = PairAssignmentDocument(
                id = PairAssignmentDocumentId(monk.id().toString()),
                date = DateTime.now(),
                pairs = listOf(
                    pairOf(Player(name = "Shaggy"), Player(name = "Scooby"))
                ).withPins()
            )
        }) {
            sdk.save(tribe)
            sdk.save(pairAssignments.with(tribe.id))
            sdk.delete(tribe.id, pairAssignments.id!!)
        } exerciseAsync {
            sdk.getPairAssignments(tribe.id)
        } verifyAsync { result ->
            result.assertIsEqualTo(emptyList())
        }
    }

    @Test
    fun deleteAssignmentsThatDontExistWillError() = testAsync {
        val sdk = authorizedSdk(username = "eT-user-${uuid4()}")
        setupAsync(object {
            val tribe = Tribe(TribeId(uuid4().toString()), name = "one")
        }) {
            sdk.save(tribe)
        } exerciseAsync {
            catchAxiosError {
                sdk.delete(
                    tribe.id, PairAssignmentDocumentId(
                        monk.id().toString()
                    )
                )
            }
        } verifyAsync { result ->
            result["status"].assertIsEqualTo(404)
            result["data"].unsafeCast<Json>()["message"]
                .assertIsEqualTo("Pair Assignments could not be deleted.")
        }
    }

    @Test
    fun givenNoAuthGetIsNotAllowed() = testAsync {
        val sdk = authorizedSdk()
        setupAsync(object {}) exerciseAsync {
            sdk.getPairAssignments(TribeId("someoneElseTribe"))
        } verifyAsync { result ->
            result.assertIsEqualTo(emptyList())
        }
    }

}