package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import io.klbz.curie.Either.Companion.left
import io.klbz.curie.Either.Companion.right
import io.klbz.curie.Toggle.Companion.on
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Either - Kotlin variants")
internal class EitherKTest {

    //region Map

    @Test
    @DisplayName("Left: Left-maps value")
    fun leftMapL() = assertEquals(left<String, Any>(leftVal + "Two"), simpleLeft.mapL { it + "Two" })

    @Test
    @DisplayName("Left: Ignores right-maps")
    fun leftMapR() = assertEquals(left<String, Any>(leftVal), simpleLeft.mapR { it + 1 })

    @Test
    @DisplayName("Right: Ignores left-maps")
    fun rightMapL() = assertEquals(right<Any, Int>(rightVal), simpleRight.mapL { it + "Two" })

    @Test
    @DisplayName("Right: Right-maps value")
    fun rightMapR() = assertEquals(right<Any, Int>(rightVal + 1), simpleRight.mapR { it + 1 })

    //endregion

    //region Flat-Map

    @Test
    @DisplayName("Left: Left-flat-maps to left-resulting value")
    fun leftFlatMapLToL() =
            assertEquals(left<String, Any>(leftVal + "Two"), simpleLeft.flatMapL<String> { left(it + "Two") })

    @Test
    @DisplayName("Left: Left-flat-maps to left-resulting value")
    fun leftFlatMapLToR() =
            assertEquals(right<Any, Int>(rightVal + 1), simpleLeft.flatMapL<Any> { right(it.length) })

    @Test
    @DisplayName("Left: Ignores right-flat-maps")
    fun leftFlatMapR() = assertEquals(left<String, Any>(leftVal), simpleLeft.flatMapR<Any> { left(it.toString()) })

    @Test
    @DisplayName("Right: Right-flat-maps to right-resulting value")
    fun rightFlatMapRToR() =
            assertEquals(right<Any, Int>(rightVal + 1), simpleRight.flatMapR<Int> { right(it + 1) })

    @Test
    @DisplayName("Right: Right-flat-maps to left-resulting value")
    fun rightFlatMapRToL() =
            assertEquals(left<String, Any>(rightVal.toString()), simpleRight.flatMapR<Any> { left(it.toString()) })

    @Test
    @DisplayName("Right: Ignores left-flat-maps")
    fun rightFlatMapL() = assertEquals(right<Any, Int>(rightVal), simpleRight.flatMapL<Any> { right(it.length) })

    //endregion

    //region Collapse

    @Test
    @DisplayName("Left: Collapses into left using its value")
    fun leftCollapseIntoL() = assertEquals(leftVal, simpleLeft.collapseIntoL { it.toString() })

    @Test
    @DisplayName("Left: Collapses into right using left-collapse function")
    fun leftCollapseIntoR() = assertEquals(leftVal.length, simpleLeft.collapseIntoR { it.length })

    @Test
    @DisplayName("Right: Collapses into left using right-collapse function")
    fun rightCollapseIntoL() = assertEquals(rightVal.toString(), simpleRight.collapseIntoL { it.toString() })

    @Test
    @DisplayName("Right: Collapses into right using its value")
    fun rightCollapseIntoR() = assertEquals(rightVal, simpleRight.collapseIntoR { it.length })

    //endregion

    //region Satisfaction

    @Test
    @DisplayName("Left: Tests whether its value satisfies left-targeted predicate")
    fun leftSatisfiesL() {
        assertTrue(simpleLeft.satisfiesL { it == leftVal })
        assertFalse(simpleLeft.satisfiesL { it == "$leftVal not" })
    }

    @Test
    @DisplayName("Left: Never satisfies a right-targeted predicate")
    fun leftNeverSatisfiesR() = assertFalse(simpleLeft.satisfiesR { it == rightVal })

    @Test
    @DisplayName("Left: Uses the left-targeted predicate to test its value when provided both")
    fun leftSatisfiesUsingL() = assertTrue(simpleLeft.satisfies({ it == leftVal }, { it == rightVal }))

    @Test
    @DisplayName("Right: Tests whether its value satisfies right-targeted predicate")
    fun rightSatisfiesR() {
        assertTrue(simpleRight.satisfiesR { it == rightVal })
        assertFalse(simpleRight.satisfiesR { it == rightVal + 1 })
    }

    @Test
    @DisplayName("Right: Never satisfies a left-targeted predicate")
    fun rightNeverSatisfiesL() = assertFalse(simpleRight.satisfiesL { it == leftVal })

    @Test
    @DisplayName("Right: Uses the right-targeted predicate to test its value when provided both")
    fun rightSatisfiesUsingR() = assertTrue(simpleRight.satisfies({ it == leftVal }, { it == rightVal }))

    //endregion

    //region Side-Effects

    @Test
    @DisplayName("Left: When Left, run side effect")
    fun leftWhenLDo() {
        val box = boxed("Box")
        simpleLeft.whenLDo { box.value = it }
        assertTrue(box.contains(leftVal))
    }

    @Test
    @DisplayName("Left: When Right, ignore side effect")
    fun leftWhenRDo() {
        val toggle = on()
        simpleLeft.whenRDo { toggle.turnOff() }
        assertTrue(toggle.isOn)
    }

    @Test
    @DisplayName("Left: When Left, after performing side effect, returned object should be identical")
    fun leftWhenLDoReturn() = assertEquals(left<String, Any>(leftVal), simpleLeft.whenLDo { })

    @Test
    @DisplayName("Left: When Right, after ignoring side effect, returned object should be identical")
    fun leftWhenRDoReturn() = assertEquals(left<String, Any>(leftVal), simpleLeft.whenRDo { })

    @Test
    @DisplayName("Right: When Left, ignore side effect")
    fun rightWhenLDo() {
        val toggle = on()
        simpleRight.whenLDo { toggle.turnOff() }
        assertTrue(toggle.isOn)
    }

    @Test
    @DisplayName("Right: When Right, run side effect")
    fun rightWhenRDo() {
        val box = boxed(0)
        simpleRight.whenRDo { box.value = it }
        assertTrue(box.contains(rightVal))
    }

    @Test
    @DisplayName("Right: When Left, after performing side effect, returned object should be identical")
    fun rightWhenLDoReturn() = assertEquals(right<Any, Int>(rightVal), simpleRight.whenLDo { })

    @Test
    @DisplayName("Right: When Right, after ignoring side effect, returned object should be identical")
    fun rightWhenRDoReturn() = assertEquals(right<Any, Int>(rightVal), simpleRight.whenRDo { })

    //endregion

    //region Assume

    @Test
    @DisplayName("Left: Left Assumption proves correct, ignoring custom exception")
    fun leftAssumeLCustom() =
            assertEquals(leftVal, simpleLeft.assumeL { IllegalArgumentException() })

    @Test
    @DisplayName("Left: Right Assumption proves incorrect, throwing custom exception")
    fun leftAssumeRCustom() {
        assertThrows(IllegalArgumentException::class.java) {
            simpleLeft.assumeR { IllegalArgumentException() }
        }
    }

    @Test
    @DisplayName("Right: Left Assumption proves incorrect, throwing custom exception")
    fun rightAssumeLCustom() {
        assertThrows(IllegalArgumentException::class.java) {
            simpleRight.assumeL { IllegalArgumentException() }
        }
    }

    @Test
    @DisplayName("Right: Right Assumption proves correct, ignoring custom exception")
    fun rightAssumeRCustom() =
            assertEquals(rightVal, simpleRight.assumeR { IllegalArgumentException() })

    companion object {
        private const val leftVal = "String"
        private val simpleLeft = left<String, Int>(leftVal)
        private const val rightVal = 5
        private val simpleRight = right<String, Int>(rightVal)
    }

    //endregion
}