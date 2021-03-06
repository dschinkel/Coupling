package com.zegreatrob.coupling.client

import ShallowWrapper
import com.zegreatrob.coupling.client.external.react.EmptyProps
import com.zegreatrob.coupling.client.external.react.PropsClassProvider
import com.zegreatrob.coupling.client.external.react.loadStyles
import com.zegreatrob.coupling.client.external.react.provider
import com.zegreatrob.coupling.client.player.PlayerCardProps
import com.zegreatrob.coupling.client.welcome.WelcomeRenderer
import com.zegreatrob.coupling.client.welcome.WelcomeStyles
import com.zegreatrob.coupling.model.player.Player
import com.zegreatrob.minassert.assertIsEqualTo
import com.zegreatrob.testmints.async.setupAsync
import com.zegreatrob.testmints.async.testAsync
import com.zegreatrob.testmints.setup
import kotlinx.coroutines.withContext
import shallow
import kotlin.test.Test

class WelcomeTest {

    private val styles = loadStyles<WelcomeStyles>("Welcome")

    @Test
    fun doesNotShowInitially() = setup(object : WelcomeRenderer, PropsClassProvider<EmptyProps> by provider() {
    }) exercise {
        shallow(EmptyProps)
    } verify { wrapper ->
        wrapper.find<Any>(".${styles.className}")
            .hasClass(styles.hidden)
            .assertIsEqualTo(true)
    }

    @Test
    fun willShowAfterZeroTimeoutSoThatAnimationWorks() = testAsync {
        withContext(coroutineContext) {
            setupAsync(object : WelcomeRenderer, PropsClassProvider<EmptyProps> by provider() {
                override fun buildScope() = this@withContext
            }) exerciseAsync {
                shallow(EmptyProps)
            }
        } verifyAsync { wrapper ->
            wrapper.update()
                .find<ShallowWrapper<Any>>(".${styles.className}")
                .hasClass(styles.hidden)
                .assertIsEqualTo(false)
        }
    }

    @Test
    fun whenZeroIsRolledWillShowHobbits() = setup(object : WelcomeRenderer,
        PropsClassProvider<EmptyProps> by provider() {
        override fun nextRandomInt(until: Int) = 0
    }) exercise {
        shallow(EmptyProps)
    } verify { wrapper ->
        wrapper.findLeftCardProps()
            .player
            .assertIsEqualTo(
                Player(
                    id = "Frodo",
                    name = "Frodo",
                    imageURL = "/images/icons/players/frodo-icon.png"
                )
            )
        wrapper.findRightCardProps()
            .player
            .assertIsEqualTo(
                Player(
                    id = "Sam",
                    name = "Sam",
                    imageURL = "/images/icons/players/samwise-icon.png"
                )
            )
        wrapper.find<Any>(".${styles.welcomeProverb}")
            .text()
            .assertIsEqualTo("Together, climb mountains.")
    }

    @Test
    fun whenOneIsRolledWillShowTheDynamicDuo() = setup(object : WelcomeRenderer,
        PropsClassProvider<EmptyProps> by provider() {
        override fun nextRandomInt(until: Int) = 1
    }) exercise {
        shallow(EmptyProps)
    } verify { wrapper ->
        wrapper.findLeftCardProps()
            .player
            .assertIsEqualTo(
                Player(
                    id = "Batman",
                    name = "Batman",
                    imageURL = "/images/icons/players/grayson-icon.png"
                )
            )
        wrapper.findRightCardProps()
            .player
            .assertIsEqualTo(
                Player(
                    id = "Robin",
                    name = "Robin",
                    imageURL = "/images/icons/players/wayne-icon.png"
                )
            )
        wrapper.find<Any>(".${styles.welcomeProverb}")
            .text()
            .assertIsEqualTo("Clean up the city, together.")
    }

    @Test
    fun whenTwoIsRolledWillShowTheHeroesOfWWII() = setup(object : WelcomeRenderer,
        PropsClassProvider<EmptyProps> by provider() {
        override fun nextRandomInt(until: Int) = 2
    }) exercise {
        shallow(EmptyProps)
    } verify { wrapper ->
        wrapper.findLeftCardProps()
            .player
            .assertIsEqualTo(
                Player(
                    id = "Rosie",
                    name = "Rosie",
                    imageURL = "/images/icons/players/rosie-icon.png"
                )
            )
        wrapper.findRightCardProps()
            .player
            .assertIsEqualTo(
                Player(
                    id = "Wendy",
                    name = "Wendy",
                    imageURL = "/images/icons/players/wendy-icon.png"
                )
            )
        wrapper.find<Any>(".${styles.welcomeProverb}")
            .text()
            .assertIsEqualTo("Team up. Get things done.")
    }

    private fun ShallowWrapper<dynamic>.findRightCardProps() = find<PlayerCardProps>(".${styles.playerCard}.right")
        .props()

    private fun ShallowWrapper<dynamic>.findLeftCardProps() = find<PlayerCardProps>(".${styles.playerCard}.left")
        .props()

}