package com.zegreatrob.coupling.sdk

import com.zegreatrob.coupling.sdk.external.axios.Axios
import com.zegreatrob.coupling.sdk.external.axios.axios
import com.zegreatrob.minassert.assertIsEqualTo
import kotlinx.coroutines.await
import kotlin.js.json

external val process: dynamic

@JsModule("axios-cookiejar-support")
external val axiosCookiejarSupport: dynamic

@JsModule("tough-cookie")
external val toughCookie: dynamic

@JsModule("monk")
external val monk: dynamic

private val configPort = process.env.PORT
private val host = "http://localhost:${configPort}"
private const val userEmail = "test@test.tes"

suspend fun authorizedAxios(username: String = userEmail): Axios {
    axiosCookiejarSupport.default(axios.default)
    @Suppress("UNUSED_VARIABLE") val jarType = toughCookie.CookieJar
    val cookieJar = js("new jarType")
    val hostAxios = axios.create(
        json(
            "baseURL" to host,
            "jar" to cookieJar,
            "withCredentials" to true
        )
    )

    hostAxios.get(
        "/test-login?username=${username}&password=pw",
        json("maxRedirects" to 0, "validateStatus" to { true })
    )
        .await()
        .status
        .assertIsEqualTo(302)

    return hostAxios
}

suspend fun authorizedSdk(username: String = userEmail) = AuthorizedSdk(authorizedAxios(username))

class AuthorizedSdk(override val axios: Axios) : Sdk {
    override val tribeRepository get() = this
    override val playerRepository get() = this
    override val pinRepository get() = this
    override val pairAssignmentDocumentRepository get() = this
    override val sdk get() = this
}