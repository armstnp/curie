package io.klbz.curie

import io.klbz.curie.Maybe.Companion.just
import io.klbz.curie.Maybe.Companion.none
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

sealed class Either<L, R> {

    enum class Alternative {
        Left, Right
    }

    abstract fun swap(): Either<R, L>

    abstract fun <LPrime> mapL(f: Function<in L, LPrime>): Either<LPrime, R>

    abstract fun <RPrime> mapR(f: Function<in R, RPrime>): Either<L, RPrime>

    abstract fun <LPrime> flatMapL(f: Function<in L, Either<LPrime, R>>): Either<LPrime, R>

    abstract fun <RPrime> flatMapR(f: Function<in R, Either<L, RPrime>>): Either<L, RPrime>

    abstract fun collapseIntoL(f: Function<in R, out L>): L

    abstract fun collapseIntoR(f: Function<in L, out R>): R

    abstract fun isolateL(): Maybe<L>

    abstract fun isolateR(): Maybe<R>

    abstract fun satisfiesL(p: Predicate<in L>): Boolean

    abstract fun satisfiesR(p: Predicate<in R>): Boolean

    abstract fun satisfies(lp: Predicate<in L>, rp: Predicate<in R>): Boolean

    abstract fun whenLDo(doF: Consumer<L>): Either<L, R>

    abstract fun whenRDo(doF: Consumer<R>): Either<L, R>

    abstract fun assumeL(): L

    abstract fun assumeL(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>): L

    abstract fun assumeR(): R

    abstract fun assumeR(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>): R

    //region Kotlin-specific overloads

    @JvmSynthetic
    abstract fun <LPrime> mapL(f: (L) -> LPrime): Either<LPrime, R>

    @JvmSynthetic
    abstract fun <RPrime> mapR(f: (R) -> RPrime): Either<L, RPrime>

    @JvmSynthetic
    abstract fun <LPrime> flatMapL(f: (L) -> Either<LPrime, R>): Either<LPrime, R>

    @JvmSynthetic
    abstract fun <RPrime> flatMapR(f: (R) -> Either<L, RPrime>): Either<L, RPrime>

    @JvmSynthetic
    abstract fun collapseIntoL(f: (R) -> L): L

    @JvmSynthetic
    abstract fun collapseIntoR(f: (L) -> R): R

    @JvmSynthetic
    abstract fun satisfiesL(p: (L) -> Boolean): Boolean

    @JvmSynthetic
    abstract fun satisfiesR(p: (R) -> Boolean): Boolean

    // TODO: Find a more elegant way to phrase this in Kotlin, likely using an intermediate function call
    @JvmSynthetic
    abstract fun satisfies(lp: (L) -> Boolean, rp: (R) -> Boolean): Boolean

    @JvmSynthetic
    abstract fun whenLDo(doF: (L) -> kotlin.Unit): Either<L, R>

    @JvmSynthetic
    abstract fun whenRDo(doF: (R) -> kotlin.Unit): Either<L, R>

    @JvmSynthetic
    abstract fun assumeL(toThrowWhenAssumptionInvalid: () -> RuntimeException): L

    @JvmSynthetic
    abstract fun assumeR(toThrowWhenAssumptionInvalid: () -> RuntimeException): R

    //endregion

    companion object {
        @JvmStatic
        fun <L, R> left(value: L): Either<L, R> = Left(value)

        @JvmStatic
        fun <L, R> right(value: R): Either<L, R> = Right(value)
    }

    internal class Left<L, R> internal constructor(private val value: L) : Either<L, R>() {

        override fun swap() = right<R, L>(value)

        override fun <LPrime> mapL(f: Function<in L, LPrime>) = left<LPrime, R>(f.apply(value))

        override fun <RPrime> mapR(f: Function<in R, RPrime>) = left<L, RPrime>(value)

        override fun <LPrime> flatMapL(f: Function<in L, Either<LPrime, R>>) = f.apply(value)

        override fun <RPrime> flatMapR(f: Function<in R, Either<L, RPrime>>) = left<L, RPrime>(value)

        override fun collapseIntoL(f: Function<in R, out L>) = value

        override fun collapseIntoR(f: Function<in L, out R>) = f.apply(value)

        override fun isolateL() = just(value)

        override fun isolateR() = none<R>()

        override fun satisfiesL(p: Predicate<in L>) = p.test(value)

        override fun satisfiesR(p: Predicate<in R>) = false

        override fun satisfies(lp: Predicate<in L>, rp: Predicate<in R>) = lp.test(value)

        override fun whenLDo(doF: Consumer<L>) = also { doF.accept(value) }

        override fun whenRDo(doF: Consumer<R>) = this

        override fun assumeL() = value

        override fun assumeL(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>) = value

        override fun assumeR() = throw InvalidAlternativeException.left()

        override fun assumeR(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>): R {
            throw toThrowWhenAssumptionInvalid.get()
        }

        //region Kotlin-specific overloads

        @JvmSynthetic
        override fun <LPrime> mapL(f: (L) -> LPrime) = left<LPrime, R>(f(value))

        @JvmSynthetic
        override fun <RPrime> mapR(f: (R) -> RPrime) = left<L, RPrime>(value)

        @JvmSynthetic
        override fun <LPrime> flatMapL(f: (L) -> Either<LPrime, R>) = f(value)

        @JvmSynthetic
        override fun <RPrime> flatMapR(f: (R) -> Either<L, RPrime>) = left<L, RPrime>(value)

        @JvmSynthetic
        override fun collapseIntoL(f: (R) -> L) = value

        @JvmSynthetic
        override fun collapseIntoR(f: (L) -> R) = f(value)

        @JvmSynthetic
        override fun satisfiesL(p: (L) -> Boolean) = p(value)

        @JvmSynthetic
        override fun satisfiesR(p: (R) -> Boolean) = false

        @JvmSynthetic
        override fun satisfies(lp: (L) -> Boolean, rp: (R) -> Boolean) = lp(value)

        @JvmSynthetic
        override fun whenLDo(doF: (L) -> kotlin.Unit) = also { doF(value) }

        @JvmSynthetic
        override fun whenRDo(doF: (R) -> kotlin.Unit) = this

        @JvmSynthetic
        override fun assumeL(toThrowWhenAssumptionInvalid: () -> RuntimeException) = value

        @JvmSynthetic
        override fun assumeR(toThrowWhenAssumptionInvalid: () -> RuntimeException) =
                throw toThrowWhenAssumptionInvalid()

        //endregion

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Left<*, *>

            return value == other.value

        }

        override fun hashCode() = value?.hashCode() ?: 0

        override fun toString(): String = "Left{$value}"
    }

    internal class Right<L, R> internal constructor(private val value: R) : Either<L, R>() {

        override fun swap() = left<R, L>(value)

        override fun <LPrime> mapL(f: Function<in L, LPrime>) = right<LPrime, R>(value)

        override fun <RPrime> mapR(f: Function<in R, RPrime>) = right<L, RPrime>(f.apply(value))

        override fun <LPrime> flatMapL(f: Function<in L, Either<LPrime, R>>) = right<LPrime, R>(value)

        override fun <RPrime> flatMapR(f: Function<in R, Either<L, RPrime>>) = f.apply(value)

        override fun collapseIntoL(f: Function<in R, out L>) = f.apply(value)

        override fun collapseIntoR(f: Function<in L, out R>) = value

        override fun isolateL() = none<L>()

        override fun isolateR() = just(value)

        override fun satisfiesL(p: Predicate<in L>) = false

        override fun satisfiesR(p: Predicate<in R>) = p.test(value)

        override fun satisfies(lp: Predicate<in L>, rp: Predicate<in R>) = rp.test(value)

        override fun whenLDo(doF: Consumer<L>) = this

        override fun whenRDo(doF: Consumer<R>) = also { doF.accept(value) }

        override fun assumeL() = throw InvalidAlternativeException.right()

        override fun assumeL(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>) =
                throw toThrowWhenAssumptionInvalid.get()

        override fun assumeR() = value

        override fun assumeR(toThrowWhenAssumptionInvalid: Supplier<out RuntimeException>) = value

        //region Kotlin-specific overloads

        @JvmSynthetic
        override fun <LPrime> mapL(f: (L) -> LPrime) = right<LPrime, R>(value)

        @JvmSynthetic
        override fun <RPrime> mapR(f: (R) -> RPrime) = right<L, RPrime>(f(value))

        @JvmSynthetic
        override fun <LPrime> flatMapL(f: (L) -> Either<LPrime, R>) = right<LPrime, R>(value)

        @JvmSynthetic
        override fun <RPrime> flatMapR(f: (R) -> Either<L, RPrime>) = f(value)

        @JvmSynthetic
        override fun collapseIntoL(f: (R) -> L) = f(value)

        @JvmSynthetic
        override fun collapseIntoR(f: (L) -> R) = value

        @JvmSynthetic
        override fun satisfiesL(p: (L) -> Boolean) = false

        @JvmSynthetic
        override fun satisfiesR(p: (R) -> Boolean) = p(value)

        @JvmSynthetic
        override fun satisfies(lp: (L) -> Boolean, rp: (R) -> Boolean) = rp(value)

        @JvmSynthetic
        override fun whenLDo(doF: (L) -> kotlin.Unit) = this

        @JvmSynthetic
        override fun whenRDo(doF: (R) -> kotlin.Unit) = also { doF(value) }

        @JvmSynthetic
        override fun assumeL(toThrowWhenAssumptionInvalid: () -> RuntimeException) =
                throw toThrowWhenAssumptionInvalid()

        @JvmSynthetic
        override fun assumeR(toThrowWhenAssumptionInvalid: () -> RuntimeException) = value

        //endregion

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Right<*, *>

            return value == other.value

        }

        override fun hashCode() = value?.hashCode() ?: 0

        override fun toString() = "Right{$value}"
    }

    class InvalidAlternativeException private constructor(attemptedAlternative: Alternative)
        : RuntimeException(
            "Attempted to get value of alternative ${attemptedAlternative.name} when it was not present.") {

        companion object {
            internal fun left(): InvalidAlternativeException = InvalidAlternativeException(Alternative.Left)

            internal fun right(): InvalidAlternativeException = InvalidAlternativeException(Alternative.Right)
        }
    }
}
