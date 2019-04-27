package io.klbz.curie

import java.util.function.Consumer
import java.util.function.Function

class FirstFocus<F, S> internal constructor(private val pair: Pair<F, S>) {

    fun unfocus() = pair

    fun <FPrime> replace(newFirst: FPrime) = on(pair.replaceFirst(newFirst))

    fun isolate() = pair.isolateFirst()

    fun <FPrime> map(f: Function<in F, FPrime>) = on(pair.mapFirst(f))

    fun withDo(doF: Consumer<F>) = also { pair.withFirstDo(doF) }

    //region Kotlin-specific overloads

    @JvmSynthetic
    fun <FPrime> map(f: (F) -> FPrime) = on(pair.mapFirst(f))

    @JvmSynthetic
    fun withDo(doF: (F) -> kotlin.Unit) = also { pair.withFirstDo(doF) }

    //endregion

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        if (other.javaClass == Pair::class.java) return pair == other

        if (other.javaClass == FirstFocus::class.java) {
            val that = other as FirstFocus<*, *>?
            return pair == that!!.pair
        }

        if (other.javaClass == SecondFocus::class.java) {
            val that = other as SecondFocus<*, *>?
            return pair == that!!.unfocus()
        }

        return false
    }

    override fun hashCode() = pair.hashCode()

    override fun toString() = "FirstFocus{$pair}"

    companion object {
        @JvmStatic
        fun <F, S> on(pair: Pair<F, S>): FirstFocus<F, S> {
            return FirstFocus(pair)
        }
    }
}
