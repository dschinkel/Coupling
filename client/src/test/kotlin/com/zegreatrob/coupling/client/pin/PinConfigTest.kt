package com.zegreatrob.coupling.client.pin

import ShallowWrapper
import Spy
import SpyData
import com.zegreatrob.coupling.client.external.react.PropsClassProvider
import com.zegreatrob.coupling.client.external.react.loadStyles
import com.zegreatrob.coupling.client.external.react.provider
import com.zegreatrob.coupling.model.pin.Pin
import com.zegreatrob.coupling.model.tribe.Tribe
import com.zegreatrob.coupling.model.tribe.TribeId
import com.zegreatrob.coupling.repository.pin.PinRepository
import com.zegreatrob.minassert.assertIsEqualTo
import com.zegreatrob.testmints.async.setupAsync
import com.zegreatrob.testmints.async.testAsync
import com.zegreatrob.testmints.setup
import findByClass
import kotlinx.coroutines.withContext
import shallow
import simulateInputChange
import kotlin.js.json
import kotlin.test.Test

class PinConfigTest {

    private val styles = loadStyles<PinConfigStyles>("pin/PinConfig")

    abstract class RendererWithStub : PinConfigRenderer, PropsClassProvider<PinConfigProps> by provider() {
        override val pinRepository: PinRepository get() = throw NotImplementedError("stubbed")
    }

    @Test
    fun whenGivenPinHasNoIdWillNotShowDeleteButton() = setup(object : RendererWithStub() {
        val tribe = Tribe(TribeId(""))
        val pin = Pin(_id = null)

    }) exercise {
        shallow(PinConfigProps(tribe, pin, emptyList(), {}, {}))
    } verify { wrapper ->
        wrapper.findByClass(styles.deleteButton)
            .length
            .assertIsEqualTo(0)
    }

    @Test
    fun whenGivenPinHasIdWillShowDeleteButton() = setup(object : RendererWithStub() {
        val tribe = Tribe(TribeId(""))
        val pin = Pin(_id = "excellent id")
    }) exercise {
        shallow(PinConfigProps(tribe, pin, emptyList(), {}, {}))
    } verify { wrapper ->
        wrapper.findByClass(styles.deleteButton)
            .length
            .assertIsEqualTo(1)
    }

    @Test
    fun whenGivenPinWithSimpleIconWillUseStandardFontAwesomeTag() = setup(object : RendererWithStub() {
        val tribe = Tribe(TribeId(""))
        val pin = Pin(icon = "angry")
    }) exercise {
        shallow(PinConfigProps(tribe, pin, emptyList(), {}, {}))
    } verify { wrapper ->
        wrapper.findByClass(styles.icon)
            .assertIconHasClasses("fa", "fa-angry")
    }

    @Test
    fun whenGivenPinWithAlreadyDecoratedIconWillUseStandardFontAwesomeTag() = setup(object : RendererWithStub() {
        val tribe = Tribe(TribeId(""))
        val pin = Pin(icon = "fa-angry")
    }) exercise {
        shallow(PinConfigProps(tribe, pin, emptyList(), {}, {}))
    } verify { wrapper ->
        wrapper.findByClass(styles.icon)
            .assertIconHasClasses("fa", "fa-angry")
    }

    @Test
    fun whenGivenPinWithFullyDecoratedIconWillUseStandardFontAwesomeTag() = setup(object : RendererWithStub() {
        val tribe = Tribe(TribeId(""))
        val pin = Pin(icon = "far fa-angry")
    }) exercise {
        shallow(PinConfigProps(tribe, pin, emptyList(), {}, {}))
    } verify { wrapper ->
        wrapper.findByClass(styles.icon)
            .assertIconHasClasses("far", "fa-angry")
            .hasClass("fa").assertIsEqualTo(false, "should not have fa")
    }

    private fun ShallowWrapper<dynamic>.assertIconHasClasses(prefixClass: String, iconClass: String) = find<String>("i")
        .apply {
            hasClass(prefixClass).assertIsEqualTo(true, "Did not have class $prefixClass")
            hasClass(iconClass).assertIsEqualTo(true, "Did not have class $iconClass")
        }

    @Test
    fun whenSaveIsPressedWillSavePinWithUpdatedContent() = testAsync {
        withContext(coroutineContext) {
            setupAsync(object : RendererWithStub() {
                override fun buildScope() = this@withContext
                val tribe = Tribe(TribeId("dumb tribe"))
                val pin = Pin(_id = null, name = null)
                val wrapper = shallow(PinConfigProps(tribe, pin, emptyList(), {}, {}))
                val newName = "pin new name"
                val newIcon = "pin new icon"

                val savePinSpy = object : Spy<SavePinCommand, Unit> by SpyData() {}.apply { spyWillReturn(Unit) }

                override suspend fun SavePinCommand.perform() = savePinSpy.spyFunction(this)

            }) {
                wrapper.simulateInputChange("name", newName)
                wrapper.simulateInputChange("icon", newIcon)
                wrapper.update()
            } exerciseAsync {
                wrapper.find<Any>("form")
                    .simulate("submit", json("preventDefault" to {}))
            }
        } verifyAsync {
            savePinSpy.spyReceivedValues
                .assertIsEqualTo(
                    listOf(
                        SavePinCommand(tribe.id, Pin(name = newName, icon = newIcon))
                    )
                )
        }
    }
}