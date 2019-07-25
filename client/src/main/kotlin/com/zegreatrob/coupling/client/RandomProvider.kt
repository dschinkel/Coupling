package com.zegreatrob.coupling.client

import kotlin.random.Random

interface RandomProvider {

    fun nextRandomInt(until: Int) = Random.nextInt(until)

    fun <T> List<T>.random() = nextRandomInt(this.size).let(::get)

}