import kotlin.test.Test

class SavePlayerCommandTest {

    @Test
    fun willSaveToRepository() = testAsync {
        setupAsync(object : SavePlayerCommandDispatcher {
            val tribe = TribeId("woo")
            val player = Player(
                    id = "1",
                    badge = 1,
                    name = "Tim",
                    callSignAdjective = "Spicy",
                    callSignNoun = "Meatball",
                    email = "tim@tim.meat",
                    imageURL = "italian.jpg"
            )
            override val playerRepository = PlayerRepositorySpy().apply { whenever(player with tribe, Unit) }
        }) exerciseAsync {
            SavePlayerCommand(player with tribe)
                    .perform()
        } verifyAsync { result ->
            result.assertIsEqualTo(player)
        }
    }

    class PlayerRepositorySpy : PlayerRepository, Spy<TribeIdPlayer, Unit> by SpyData() {
        override fun getPlayersAsync(tribeId: TribeId) = cancel()

        override suspend fun save(tribeIdPlayer: TribeIdPlayer) = spyFunction(tribeIdPlayer)
        override suspend fun delete(playerId: String) = cancel()
    }
}