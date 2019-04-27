package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import io.klbz.curie.Either.Companion.left
import io.klbz.curie.Either.Companion.right
import io.klbz.curie.Either.InvalidAlternativeException
import io.klbz.curie.Maybe.Companion.just
import io.klbz.curie.Maybe.Companion.none
import io.klbz.curie.Toggle.Companion.on
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import io.klbz.curie.Predicate as pred
import java.util.function.Function as fn
import java.util.function.Supplier as sup

@DisplayName("Either")
internal class EitherTest {

    //region Equality

    @Test
    @DisplayName("Left: Equal to self")
    fun leftSelfEquality() = assertEquals(simpleLeft, simpleLeft)

    @Test
    @DisplayName("Left: Equal to same")
    fun leftSameEquality() = assertEquals(simpleLeft, left<String, Any>(leftVal))

    @Test
    @DisplayName("Left: Unequal to null")
    fun leftNullInequality() = assertNotEquals(simpleLeft, null)

    @Test
    @DisplayName("Left: Unequal to other type")
    fun leftOtherTypeInequality() = assertNotEquals(simpleLeft, "other")

    @Test
    @DisplayName("Left: Unequal to wrapped value")
    fun leftWrappedValueInequality() = assertNotEquals(simpleLeft, leftVal)

    @Test
    @DisplayName("Left: Unequal to right")
    fun leftRightInequality() = assertNotEquals(simpleLeft, right<Any, String>(leftVal))

    @Test
    @DisplayName("Right: Equal to self")
    fun rightSelfEquality() = assertEquals(simpleRight, simpleRight)

    @Test
    @DisplayName("Right: Equal to same")
    fun rightSameEquality() = assertEquals(simpleRight, right<Any, Int>(rightVal))

    @Test
    @DisplayName("Right: Unequal to null")
    fun rightNullInequality() = assertNotEquals(simpleRight, null)

    @Test
    @DisplayName("Right: Unequal to other type")
    fun rightOtherTypeInequality() = assertNotEquals(simpleRight, "other")

    @Test
    @DisplayName("Right: Unequal to wrapped value")
    fun rightWrappedValueInequality() = assertNotEquals(simpleRight, rightVal)

    @Test
    @DisplayName("Right: Unequal to left")
    fun rightLeftInequality() = assertNotEquals(simpleRight, left<Int, Any>(rightVal))

    //endregion

    //region Hashcode

    @Test
    @DisplayName("Left: Hash same value to same code")
    fun leftSameHashEquals() = assertEquals(left<String, Any>(leftVal).hashCode(), simpleLeft.hashCode())

    @Test
    @DisplayName("Right: Hash same value to same code")
    fun rightSameHashEquals() = assertEquals(right<Any, Int>(rightVal).hashCode(), simpleRight.hashCode())

    //endregion

    //region Swap

    @Test
    @DisplayName("Left: Swaps slots")
    fun leftSwap() = assertEquals(right<Int, String>(leftVal), simpleLeft.swap())

    @Test
    @DisplayName("Right: Swaps slots")
    fun rightSwap() = assertEquals(left<Int, String>(rightVal), simpleRight.swap())

    //endregion

    //region Map

    @Test
    @DisplayName("Left: Left-maps value")
    fun leftMapL() = assertEquals(left<String, Any>(leftVal + "Two"), simpleLeft.mapL<String>(fn { it + "Two" }))

    @Test
    @DisplayName("Left: Ignores right-maps")
    fun leftMapR() = assertEquals(left<String, Any>(leftVal), simpleLeft.mapR<Int>(fn { it + 1 }))

    @Test
    @DisplayName("Right: Ignores left-maps")
    fun rightMapL() = assertEquals(right<Any, Int>(rightVal), simpleRight.mapL<String>(fn { it + "Two" }))

    @Test
    @DisplayName("Right: Right-maps value")
    fun rightMapR() = assertEquals(right<Any, Int>(rightVal + 1), simpleRight.mapR<Int>(fn { it + 1 }))

    //endregion

    //region Flat-Map

    @Test
    @DisplayName("Left: Left-flat-maps to left-resulting value")
    fun leftFlatMapLToL() =
            assertEquals(left<String, Any>(leftVal + "Two"), simpleLeft.flatMapL<String>(fn { left(it + "Two") }))

    @Test
    @DisplayName("Left: Left-flat-maps to left-resulting value")
    fun leftFlatMapLToR() =
            assertEquals(right<Any, Int>(rightVal + 1), simpleLeft.flatMapL<Any>(fn { right(it.length) }))

    @Test
    @DisplayName("Left: Ignores right-flat-maps")
    fun leftFlatMapR() = assertEquals(left<String, Any>(leftVal), simpleLeft.flatMapR<Any>(fn { left(it.toString()) }))

    @Test
    @DisplayName("Right: Right-flat-maps to right-resulting value")
    fun rightFlatMapRToR() =
            assertEquals(right<Any, Int>(rightVal + 1), simpleRight.flatMapR<Int>(fn { right(it + 1) }))

    @Test
    @DisplayName("Right: Right-flat-maps to left-resulting value")
    fun rightFlatMapRToL() =
            assertEquals(left<String, Any>(rightVal.toString()), simpleRight.flatMapR<Any>(fn { left(it.toString()) }))

    @Test
    @DisplayName("Right: Ignores left-flat-maps")
    fun rightFlatMapL() = assertEquals(right<Any, Int>(rightVal), simpleRight.flatMapL<Any>(fn { right(it.length) }))

    //endregion

    //region Collapse

    @Test
    @DisplayName("Left: Collapses into left using its value")
    fun leftCollapseIntoL() = assertEquals(leftVal, simpleLeft.collapseIntoL(fn { it.toString() }))

    @Test
    @DisplayName("Left: Collapses into right using left-collapse function")
    fun leftCollapseIntoR() = assertEquals(leftVal.length, simpleLeft.collapseIntoR(fn { it.length }))

    @Test
    @DisplayName("Right: Collapses into left using right-collapse function")
    fun rightCollapseIntoL() = assertEquals(rightVal.toString(), simpleRight.collapseIntoL(fn { it.toString() }))

    @Test
    @DisplayName("Right: Collapses into right using its value")
    fun rightCollapseIntoR() = assertEquals(rightVal, simpleRight.collapseIntoR(fn { it.length }))

    //endregion

    //region Isolate

    @Test
    @DisplayName("Left: Left-isolates to just its value")
    fun leftIsolateL() = assertEquals(just(leftVal), simpleLeft.isolateL())

    @Test
    @DisplayName("Left: Right-isolates to none")
    fun leftIsolateR() = assertEquals(none<Any>(), simpleLeft.isolateR())

    @Test
    @DisplayName("Right: Left-isolates to none")
    fun rightIsolateL() = assertEquals(none<Any>(), simpleRight.isolateL())

    @Test
    @DisplayName("Right: Right-isolates to just its value")
    fun rightIsolateR() = assertEquals(just(rightVal), simpleRight.isolateR())

    //endregion

    //region Satisfaction

    @Test
    @DisplayName("Left: Tests whether its value satisfies left-targeted predicate")
    fun leftSatisfiesL() {
        assertTrue(simpleLeft.satisfiesL(pred { it == leftVal }))
        assertFalse(simpleLeft.satisfiesL(pred { it == "$leftVal not" }))
    }

    @Test
    @DisplayName("Left: Never satisfies a right-targeted predicate")
    fun leftNeverSatisfiesR() = assertFalse(simpleLeft.satisfiesR(pred { it == rightVal }))

    @Test
    @DisplayName("Left: Uses the left-targeted predicate to test its value when provided both")
    fun leftSatisfiesUsingL() = assertTrue(simpleLeft.satisfies(pred { it == leftVal }, pred { it == rightVal }))

    @Test
    @DisplayName("Right: Tests whether its value satisfies right-targeted predicate")
    fun rightSatisfiesR() {
        assertTrue(simpleRight.satisfiesR(pred { it == rightVal }))
        assertFalse(simpleRight.satisfiesR(pred { it == rightVal + 1 }))
    }

    @Test
    @DisplayName("Right: Never satisfies a left-targeted predicate")
    fun rightNeverSatisfiesL() = assertFalse(simpleRight.satisfiesL(pred { it == leftVal }))

    @Test
    @DisplayName("Right: Uses the right-targeted predicate to test its value when provided both")
    fun rightSatisfiesUsingR() = assertTrue(simpleRight.satisfies(pred { it == leftVal }, pred { it == rightVal }))

    //endregion

    //region Side-Effects

    @Test
    @DisplayName("Left: When Left, run side effect")
    fun leftWhenLDo() {
        val box = boxed("Box")
        simpleLeft.whenLDo(Consumer { box.value = it })
        assertTrue(box.contains(leftVal))
    }

    @Test
    @DisplayName("Left: When Right, ignore side effect")
    fun leftWhenRDo() {
        val toggle = on()
        simpleLeft.whenRDo(Consumer { toggle.turnOff() })
        assertTrue(toggle.isOn)
    }

    @Test
    @DisplayName("Left: When Left, after performing side effect, returned object should be identical")
    fun leftWhenLDoReturn() = assertEquals(left<String, Any>(leftVal), simpleLeft.whenLDo(Consumer { }))

    @Test
    @DisplayName("Left: When Right, after ignoring side effect, returned object should be identical")
    fun leftWhenRDoReturn() = assertEquals(left<String, Any>(leftVal), simpleLeft.whenRDo(Consumer { }))

    @Test
    @DisplayName("Right: When Left, ignore side effect")
    fun rightWhenLDo() {
        val toggle = on()
        simpleRight.whenLDo(Consumer { toggle.turnOff() })
        assertTrue(toggle.isOn)
    }

    @Test
    @DisplayName("Right: When Right, run side effect")
    fun rightWhenRDo() {
        val box = boxed(0)
        simpleRight.whenRDo(Consumer { box.value = it })
        assertTrue(box.contains(rightVal))
    }

    @Test
    @DisplayName("Right: When Left, after performing side effect, returned object should be identical")
    fun rightWhenLDoReturn() = assertEquals(right<Any, Int>(rightVal), simpleRight.whenLDo(Consumer { }))

    @Test
    @DisplayName("Right: When Right, after ignoring side effect, returned object should be identical")
    fun rightWhenRDoReturn() = assertEquals(right<Any, Int>(rightVal), simpleRight.whenRDo(Consumer { }))

    //endregion

    //region Assume

    @Test
    @DisplayName("Left: Left Assumption proves correct, returning value")
    fun leftAssumeL() = assertEquals(leftVal, simpleLeft.assumeL())

    @Test
    @DisplayName("Left: Right Assumption proves incorrect, throwing exception")
    fun leftAssumeR() {
        assertThrows(InvalidAlternativeException::class.java) { simpleLeft.assumeR() }
    }

    @Test
    @DisplayName("Left: Left Assumption proves correct, ignoring custom exception")
    fun leftAssumeLCustom() =
            assertEquals(leftVal, simpleLeft.assumeL(sup<RuntimeException> { IllegalArgumentException() }))

    @Test
    @DisplayName("Left: Right Assumption proves incorrect, throwing custom exception")
    fun leftAssumeRCustom() {
        assertThrows(IllegalArgumentException::class.java) {
            simpleLeft.assumeR(sup<RuntimeException> { IllegalArgumentException() })
        }
    }

    @Test
    @DisplayName("Right: Left Assumption proves incorrect, throwing exception")
    fun rightAssumeL() {
        assertThrows(InvalidAlternativeException::class.java) { simpleRight.assumeL() }
    }

    @Test
    @DisplayName("Right: Right Assumption proves correct, returning value")
    fun rightAssumeR() = assertEquals(rightVal, simpleRight.assumeR())

    @Test
    @DisplayName("Right: Left Assumption proves incorrect, throwing custom exception")
    fun rightAssumeLCustom() {
        assertThrows(IllegalArgumentException::class.java) {
            simpleRight.assumeL(sup<RuntimeException> { IllegalArgumentException() })
        }
    }

    @Test
    @DisplayName("Right: Right Assumption proves correct, ignoring custom exception")
    fun rightAssumeRCustom() =
            assertEquals(rightVal, simpleRight.assumeR(sup<RuntimeException> { IllegalArgumentException() }))

    companion object {
        private const val leftVal = "String"
        private val simpleLeft = left<String, Int>(leftVal)
        private const val rightVal = 5
        private val simpleRight = right<String, Int>(rightVal)
    }

    //endregion
}