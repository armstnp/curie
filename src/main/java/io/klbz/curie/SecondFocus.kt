package io.klbz.curie

import java.util.function.Consumer
import java.util.function.Function

class SecondFocus<F, S> private constructor(private val pair: Pair<F, S>) {

    fun unfocus() = pair

    fun <SPrime> replace(newSecond: SPrime) = on(pair.replaceSecond(newSecond))

    fun isolate() = pair.isolateSecond()

    fun <SPrime> map(f: Function<in S, SPrime>) = on(pair.mapSecond(f))

    fun withDo(doF: Consumer<S>) = on(pair.withSecondDo(doF))

    //region Kotlin-specific overloads

    @JvmSynthetic
    fun <SPrime> map(f: (S) -> SPrime) = on(pair.mapSecond(f))

    @JvmSynthetic
    fun withDo(doF: (S) -> kotlin.Unit) = also { pair.withSecondDo(doF) }

    //endregion

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        if (other.javaClass == Pair::class.java) return pair == other

        if (other.javaClass == FirstFocus::class.java) {
            val that = other as FirstFocus<*, *>?
            return pair == that!!.unfocus()
        }

        if (other.javaClass == SecondFocus::class.java) {
            val that = other as SecondFocus<*, *>?
            return pair == that!!.pair
        }

        return false
    }

    override fun hashCode() = pair.hashCode()

    override fun toString() = "SecondFocus{$pair}"

    companion object {
        @JvmStatic
        fun <F, S> on(pair: Pair<F, S>) = SecondFocus(pair)
    }
}
