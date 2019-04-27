package io.klbz.curie

import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

sealed class Maybe<T> {
    abstract fun <S> map(transform: Function<in T, S>): Maybe<S>

    abstract fun <S> flatMap(transform: Function<in T, Maybe<S>>): Maybe<S>

    abstract fun assume(): T

    abstract fun assume(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>): T

    abstract fun collapse(defaultWhenNone: T): T

    abstract fun collapse(defaultWhenNone: Supplier<T>): T

    abstract fun satisfies(p: Predicate<in T>): Boolean

    abstract fun preserveIf(p: Predicate<in T>): Maybe<T>

    abstract fun rejectIf(p: Predicate<in T>): Maybe<T>

    abstract fun whenPresentDo(doF: Consumer<T>): Maybe<T>

    abstract fun whenMissingDo(doF: SideEffect): Maybe<T>

    //region Kotlin-specific overloads

    @JvmSynthetic
    abstract fun <S> map(transform: (T) -> S): Maybe<S>

    @JvmSynthetic
    abstract fun <S> flatMap(transform: (T) -> Maybe<S>): Maybe<S>

    @JvmSynthetic
    abstract fun assume(toThrowWhenAssumptionInvalid: () -> RuntimeException): T

    @JvmSynthetic
    abstract fun collapse(defaultWhenNone: () -> T): T

    @JvmSynthetic
    abstract fun satisfies(p: (T) -> Boolean): Boolean

    @JvmSynthetic
    abstract fun preserveIf(p: (T) -> Boolean): Maybe<T>

    @JvmSynthetic
    abstract fun rejectIf(p: (T) -> Boolean): Maybe<T>

    @JvmSynthetic
    abstract fun whenPresentDo(doF: (T) -> kotlin.Unit): Maybe<T>

    @JvmSynthetic
    abstract fun whenMissingDo(doF: () -> kotlin.Unit): Maybe<T>

    //endregion

    class ValueNotPresentException internal constructor()
        : RuntimeException("Cannot provide value from a None-type Maybe")

    companion object {
        @JvmStatic
        fun <T> just(value: T): Maybe<T> = Just(value)

        @JvmStatic
        fun <T> none(): Maybe<T> = None()
    }
}

class Just<T> internal constructor(private val value: T) : Maybe<T>() {

    override fun <S> map(transform: Function<in T, S>) = just(transform.apply(value))

    override fun <S> flatMap(transform: Function<in T, Maybe<S>>) = transform.apply(value)

    override fun assume() = value

    override fun assume(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>) = value

    override fun collapse(defaultWhenNone: T) = value

    override fun collapse(defaultWhenNone: Supplier<T>) = value

    override fun satisfies(p: Predicate<in T>) = p.test(value)

    override fun preserveIf(p: Predicate<in T>) = if (p.test(value)) this else none<T>()

    override fun rejectIf(p: Predicate<in T>) = if (p.test(value)) none<T>() else this

    override fun whenPresentDo(doF: Consumer<T>) = also { doF.accept(value) }

    override fun whenMissingDo(doF: SideEffect) = this

    //region Kotlin-specific overloads

    override fun <S> map(transform: (T) -> S) = just(transform(value))

    override fun <S> flatMap(transform: (T) -> Maybe<S>) = transform(value)

    override fun assume(toThrowWhenAssumptionInvalid: () -> RuntimeException) = value

    override fun collapse(defaultWhenNone: () -> T) = value

    override fun satisfies(p: (T) -> Boolean) = p(value)

    override fun preserveIf(p: (T) -> Boolean) = if (p(value)) this else none<T>()

    override fun rejectIf(p: (T) -> Boolean) = if (p(value)) none<T>() else this

    override fun whenPresentDo(doF: (T) -> kotlin.Unit) = also { doF(value) }

    override fun whenMissingDo(doF: () -> kotlin.Unit) = this

    //endregion

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Just<*>

        return value == other.value
    }

    override fun hashCode() = Objects.hash(value)

    override fun toString() = "Just{$value}"
}

class None<T> internal constructor() : Maybe<T>() {

    override fun <S> map(transform: Function<in T, S>) = none<S>()

    override fun <S> flatMap(transform: Function<in T, Maybe<S>>) = none<S>()

    override fun assume() = throw ValueNotPresentException()

    override fun assume(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>) =
            throw toThrowWhenAssumptionInvalid.get()

    override fun collapse(defaultWhenNone: T) = defaultWhenNone

    override fun collapse(defaultWhenNone: Supplier<T>) = defaultWhenNone.get()

    override fun satisfies(p: Predicate<in T>) = false

    override fun preserveIf(p: Predicate<in T>) = this

    override fun rejectIf(p: Predicate<in T>) = this

    override fun whenPresentDo(doF: Consumer<T>) = this

    override fun whenMissingDo(doF: SideEffect): Maybe<T> {
        doF.perform()
        return this
    }

    //region Kotlin-specific overloads

    override fun <S> map(transform: (T) -> S) = none<S>()

    override fun <S> flatMap(transform: (T) -> Maybe<S>) = none<S>()

    override fun assume(toThrowWhenAssumptionInvalid: () -> RuntimeException) = throw toThrowWhenAssumptionInvalid()

    override fun collapse(defaultWhenNone: () -> T) = defaultWhenNone()

    override fun satisfies(p: (T) -> Boolean) = false

    override fun preserveIf(p: (T) -> Boolean) = this

    override fun rejectIf(p: (T) -> Boolean) = this

    override fun whenPresentDo(doF: (T) -> kotlin.Unit) = this

    override fun whenMissingDo(doF: () -> kotlin.Unit) = also { doF() }

    //endregion

    override fun equals(other: Any?) = this === other || other != null && javaClass == other.javaClass

    override fun hashCode() = 0

    override fun toString() = "None{}"
}
