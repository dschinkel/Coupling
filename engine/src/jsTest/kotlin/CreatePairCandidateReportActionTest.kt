import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertTrue

class CreatePairCandidateReportActionTest {

    companion object : CreatePairCandidateReportActionDispatcher {

        fun pairAssignmentDocument(pairs: List<CouplingPair>) = PairAssignmentDocument(Date(), pairs, "")
    }

    @Test
    fun shouldReturnNothingWhenNoPartnersAreAvailable() = setup(object {
        val players: List<Player> = emptyList()
        val history: List<PairAssignmentDocument> = emptyList()
    }) exercise {
        CreatePairCandidateReportAction(Player(_id = "player"), history, players)
                .perform()
    } verify {
        assertTrue(it.partners.isEmpty())
    }

    class ShouldDeterminePossiblePartnersForPlayerByChoosingPartner {
        companion object {
            val bruce = Player(_id = "Batman")
            val jezebel = Player(_id = "Jezebel Jett")
            val talia = Player(_id = "Talia")
            val selena = Player(_id = "Catwoman")
            val availableOtherPlayers = listOf(selena, talia, jezebel)

            private fun createPairCandidateReportAction(history: List<PairAssignmentDocument>, availablePlayers: List<Player>) =
                    CreatePairCandidateReportAction(bruce, history, availablePlayers)
        }

        class WhoHasNeverPaired {
            @Test
            fun withNoHistory() = setup(object {
                val history = emptyList<PairAssignmentDocument>()
            }) exercise {
                createPairCandidateReportAction(history, availableOtherPlayers)
                        .perform()
            } verify {
                it.assertIsEqualTo(PairCandidateReport(bruce, availableOtherPlayers, NeverPaired))
            }

            @Test
            fun withHistoryDocumentThatHasNoPairs() = setup(object {
                val history = listOf(pairAssignmentDocument(emptyList()))
            }) exercise {
                createPairCandidateReportAction(history, availableOtherPlayers)
                        .perform()
            } verify {
                it.assertIsEqualTo(PairCandidateReport(bruce, availableOtherPlayers, NeverPaired))
            }

            @Test
            fun withPlentyOfHistory() = setup(object {
                val history = listOf(
                        pairAssignmentDocument(listOf(CouplingPair.Double(bruce, Player(_id = "Batgirl")))),
                        pairAssignmentDocument(listOf(CouplingPair.Double(bruce, Player(_id = "Robin"))))
                )
            }) exercise {
                createPairCandidateReportAction(history, availableOtherPlayers)
                        .perform()
            } verify {
                it.assertIsEqualTo(PairCandidateReport(bruce, availableOtherPlayers, NeverPaired))
            }

            @Test
            fun onlyThePersonYouWereWithLastTime() = setup(object {
                val history = listOf(pairAssignmentDocument(listOf(
                        CouplingPair.Double(bruce, selena)
                )))
            }) exercise {
                CreatePairCandidateReportAction(bruce, history, listOf(selena))
                        .perform()
            } verify {
                it.assertIsEqualTo(PairCandidateReport(bruce, listOf(selena), TimeResultValue(0)))
            }
        }

        class WhoHasNotPairedRecently {
            @Test
            fun whenThereIsClearlySomeoneWhoHasBeenTheLongest() = setup(object {
                val expectedPartner = jezebel
                val history = listOf(
                        pairAssignmentDocument(listOf(CouplingPair.Double(bruce, selena))),
                        pairAssignmentDocument(listOf(CouplingPair.Double(bruce, talia))),
                        pairAssignmentDocument(listOf(CouplingPair.Double(expectedPartner, bruce)))
                )
            }) exercise {
                CreatePairCandidateReportAction(bruce, history, availableOtherPlayers)
                        .perform()
            } verify {
                it.assertIsEqualTo(PairCandidateReport(bruce, listOf(expectedPartner), TimeResultValue(2)))
            }

            @Test
            fun whenThereIsOnePersonWhoHasPairedButNoOneElse() = setup(object {
                val history = listOf(
                        pairAssignmentDocument(listOf(CouplingPair.Double(bruce, selena)))
                )
            }) exercise {
                CreatePairCandidateReportAction(bruce, history, availableOtherPlayers)
                        .perform()
            } verify {
                it.assertIsEqualTo(PairCandidateReport(bruce, listOf(talia, jezebel), NeverPaired))
            }
        }
    }
}
