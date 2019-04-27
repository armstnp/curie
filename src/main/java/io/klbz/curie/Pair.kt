package io.klbz.curie

import java.util.*
import java.util.function.*
import java.util.function.Function

class Pair<F, S> private constructor(private val first: F, private val second: S) {

    fun <FPrime> replaceFirst(newFirst: FPrime) = of(newFirst, second)

    fun <SPrime> replaceSecond(newSecond: SPrime) = of(first, newSecond)

    fun focusFirst() = FirstFocus.on(this)

    fun focusSecond() = SecondFocus.on(this)

    fun isolateFirst() = first

    fun isolateSecond() = second

    fun <FPrime> mapFirst(f: Function<in F, FPrime>) = of(f.apply(first), second)

    fun <SPrime> mapSecond(f: Function<in S, SPrime>) = of(first, f.apply(second))

    fun <T> collapse(f: BiFunction<in F, in S, T>) = f.apply(first, second)

    fun satisfies(p: BiPredicate<in F, in S>): Boolean = p.test(first, second)

    fun withFirstDo(doF: Consumer<F>) = also { doF.accept(first) }

    fun withSecondDo(doF: Consumer<S>) = also { doF.accept(second) }

    fun withBothDo(doF: BiConsumer<F, S>) = also { doF.accept(first, second) }

    //region Kotlin-specific overloads

    @JvmSynthetic
    fun <FPrime> mapFirst(f: (F) -> FPrime) = of(f(first), second)

    @JvmSynthetic
    fun <SPrime> mapSecond(f: (S) -> SPrime) = of(first, f(second))

    @JvmSynthetic
    fun <T> collapse(f: (F, S) -> T) = f(first, second)

    @JvmSynthetic
    fun satisfies(p: (F, S) -> Boolean): Boolean = p(first, second)

    @JvmSynthetic
    fun withFirstDo(doF: (F) -> kotlin.Unit) = also { doF(first) }

    @JvmSynthetic
    fun withSecondDo(doF: (S) -> kotlin.Unit) = also { doF(second) }

    @JvmSynthetic
    fun withBothDo(doF: (F, S) -> kotlin.Unit) = also { doF(first, second) }

    //endregion

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        if (other.javaClass == FirstFocus::class.java) {
            val that = other as FirstFocus<*, *>?
            return this == that!!.unfocus()
        }

        if (other.javaClass == SecondFocus::class.java) {
            val that = other as SecondFocus<*, *>?
            return this == that!!.unfocus()
        }

        if (javaClass != other.javaClass) return false

        val pair = other as Pair<*, *>?
        return first == pair!!.first && second == pair.second
    }

    override fun hashCode(): Int = Objects.hash(first, second)

    override fun toString() = "Pair{$first,$second}"

    class PairOfAndBuilder<F> internal constructor(private val first: F) {
        fun <S> and(second: S) = of(first, second)
    }

    class PairBuilderMissingSecond<F> internal constructor(private val first: F) {
        fun <S> second(second: S) = of(first, second)
    }

    class PairBuilderMissingFirst<S> internal constructor(private val second: S) {
        fun <F> first(first: F) = of(first, second)
    }

    companion object {
        @JvmStatic
        fun <F, S> of(first: F, second: S) = Pair(first, second)

        @JvmStatic
        fun <F> of(first: F) = PairOfAndBuilder(first)

        @JvmStatic
        fun <F> first(first: F) = PairBuilderMissingSecond(first)

        @JvmStatic
        fun <S> second(second: S) = PairBuilderMissingFirst(second)
    }
}
