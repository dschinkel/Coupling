import com.soywiz.klock.DateTime
import com.zegreatrob.coupling.logging.initializeLogging
import com.zegreatrob.testmints.MintReporter
import com.zegreatrob.testmints.StandardMints
import mu.KotlinLogging

@JsName("JasmineJsonLoggingReporter")
class JasmineJsonLoggingReporter {
    private val logger by lazy { KotlinLogging.logger("JasmineJsonLoggingReporter") }
    private var lastStart: DateTime? = null

    init {
        initializeLogging(true)

        StandardMints.reporter = object : MintReporter {
            private val logger by lazy { KotlinLogging.logger("testmints") }
            override fun exerciseStart() = logger.info { "exerciseStart" }
            override fun exerciseFinish() = logger.info { "exerciseFinish" }
            override fun verifyStart() = logger.info { "verifyStart" }
            override fun verifyFinish() = logger.info { "verifyFinish" }
        }
    }

    @Suppress("unused")
    @JsName("specStarted")
    fun specStarted(result: dynamic) {
        startTest(result.fullName.unsafeCast<String>())
    }

    @Suppress("unused")
    @JsName("specDone")
    fun specDone(result: dynamic) = endTest(
            result.fullName.unsafeCast<String>(),
            result.status.unsafeCast<String>(),
            result.failedExpectations.unsafeCast<Array<dynamic>>()
    )

    private fun startTest(testName: String) = logger.info { mapOf("type" to "TestStart", "test" to testName) }
            .also { lastStart = DateTime.now() }

    private fun endTest(testName: String, status: String, failed: Array<dynamic>) {
        val duration = lastStart?.let { DateTime.now() - it }
        logger.info {
            mapOf(
                    "type" to "TestEnd",
                    "test" to testName,
                    "status" to status,
                    "duration" to "$duration",
                    "failures" to failed.map { "message: ${it.message} \nstack: ${it.stack}" }.joinToString("\n", "\n")
            )
        }
                .also { lastStart = null }
    }
}