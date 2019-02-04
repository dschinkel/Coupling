import kotlin.test.Test

class DeletePlayerCommandTest {
    @Test
    fun willUseRepositoryToRemove() = testAsync {
        setupAsync(object : DeletePlayerCommandDispatcher {
            val playerId = "ThatGuyGetHim"
            override val repository = PlayersRepositorySpy().apply { whenever(playerId, Unit) }
        }) exerciseAsync {
            DeletePlayerCommand(playerId)
                    .perform()
        } verifyAsync { result -> result.assertIsEqualTo(playerId) }
    }

    class PlayersRepositorySpy : PlayersRepository, Spy<String, Unit> by SpyData() {
        override suspend fun delete(playerId: String) = spyFunction(playerId)

        override fun getPlayersAsync(tribeId: String) = cancel()
        override suspend fun save(player: Player) = cancel()
    }
}