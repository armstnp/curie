package io.klbz.curie

import io.klbz.curie.Maybe.Companion.just
import io.klbz.curie.Maybe.Companion.none
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

/**
 * Satisfaction is an intermediate structure that represents the result of applying a predicate.
 * It is designed to be almost wholly transient, to be piped to concrete values using any of a variety of
 * transparent transformations.
 *
 * A Satisfaction carries the value that either satisfied or dissatisfied some predicate. This value is applied to most
 * transformation functions.
 */
sealed class Satisfaction<T> {

    abstract val isSatisfied: Boolean

    abstract fun preserve(): Maybe<T>

    abstract fun reject(): Maybe<T>

    abstract fun <S> collapse(ifSatisfied: S, ifDissatisfied: S): S

    abstract fun <S> lazyCollapse(ifSatisfied: Supplier<out S>, ifDissatisfied: Supplier<out S>): S

    abstract fun <S> pipeCollapse(ifSatisfied: Function<in T, out S>, ifDissatisfied: Function<in T, out S>): S

    abstract fun whenSatisfiedDo(doF: Consumer<in T>): Satisfaction<T>

    abstract fun whenDissatisfiedDo(doF: Consumer<in T>): Satisfaction<T>

    //region Kotlin-specific overloads

    // TODO: Find a better way to phrase this in Kotlin, perhaps with an intermediary.
    @JvmSynthetic
    abstract fun <S> lazyCollapse(ifSatisfied: () -> S, ifDissatisfied: () -> S): S

    // TODO: Find a better way to phrase this in Kotlin, perhaps with an intermediary.
    @JvmSynthetic
    abstract fun <S> pipeCollapse(ifSatisfied: (T) -> S, ifDissatisfied: (T) -> S): S

    @JvmSynthetic
    abstract fun whenSatisfiedDo(doF: (T) -> kotlin.Unit): Satisfaction<T>

    @JvmSynthetic
    abstract fun whenDissatisfiedDo(doF: (T) -> kotlin.Unit): Satisfaction<T>

    //endregion

    companion object {
        @JvmStatic
        fun <T> satisfies(satisfyingValue: T): Satisfaction<T> = Satisfied(satisfyingValue)

        @JvmStatic
        fun <T> dissatisfies(dissatisfyingValue: T): Satisfaction<T> = Dissatisfied(dissatisfyingValue)
    }
}


internal class Satisfied<T> internal constructor(private val value: T) : Satisfaction<T>() {

    override val isSatisfied = true

    override fun preserve() = just(value)

    override fun reject() = none<T>()

    override fun <S> collapse(ifSatisfied: S, ifDissatisfied: S) = ifSatisfied

    override fun <S> lazyCollapse(
            ifSatisfied: Supplier<out S>,
            ifDissatisfied: Supplier<out S>) = ifSatisfied.get()

    override fun <S> pipeCollapse(
            ifSatisfied: Function<in T, out S>,
            ifDissatisfied: Function<in T, out S>) = ifSatisfied.apply(value)

    override fun whenSatisfiedDo(doF: Consumer<in T>) = also { doF.accept(value) }

    override fun whenDissatisfiedDo(doF: Consumer<in T>) = this

    //region Kotlin-specific overloads

    override fun <S> lazyCollapse(ifSatisfied: () -> S, ifDissatisfied: () -> S) = ifSatisfied()

    override fun <S> pipeCollapse(ifSatisfied: (T) -> S, ifDissatisfied: (T) -> S) = ifSatisfied(value)

    override fun whenSatisfiedDo(doF: (T) -> kotlin.Unit) = also { doF(value) }

    override fun whenDissatisfiedDo(doF: (T) -> kotlin.Unit) = this

    //endregion
}

internal class Dissatisfied<T> internal constructor(private val value: T) : Satisfaction<T>() {

    override val isSatisfied = false

    override fun preserve() = none<T>()

    override fun reject() = just(value)

    override fun <S> collapse(ifSatisfied: S, ifDissatisfied: S) = ifDissatisfied

    override fun <S> lazyCollapse(
            ifSatisfied: Supplier<out S>,
            ifDissatisfied: Supplier<out S>) = ifDissatisfied.get()

    override fun <S> pipeCollapse(
            ifSatisfied: Function<in T, out S>,
            ifDissatisfied: Function<in T, out S>) = ifDissatisfied.apply(value)

    override fun whenSatisfiedDo(doF: Consumer<in T>) = this

    override fun whenDissatisfiedDo(doF: Consumer<in T>) = also { doF.accept(value) }

    //region Kotlin-specific overloads

    override fun <S> lazyCollapse(ifSatisfied: () -> S, ifDissatisfied: () -> S) = ifDissatisfied()

    override fun <S> pipeCollapse(ifSatisfied: (T) -> S, ifDissatisfied: (T) -> S) = ifDissatisfied(value)

    override fun whenSatisfiedDo(doF: (T) -> kotlin.Unit) = this

    override fun whenDissatisfiedDo(doF: (T) -> kotlin.Unit) = also { doF(value) }

    //endregion
}