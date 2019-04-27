package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import io.klbz.curie.Maybe.Companion.just
import io.klbz.curie.Maybe.Companion.none
import io.klbz.curie.Toggle.Companion.off
import io.klbz.curie.Toggle.Companion.on
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Maybe - Kotlin variants")
internal class MaybeKTest {

    //region Map

    @Test
    @DisplayName("Just: Map T -> T => Just T")
    fun mapJustSameType() = assertEquals(just(5), just(4).map { it + 1 })

    @Test
    @DisplayName("Just: Map T -> S => Just S")
    fun mapJustDifferentType() = assertEquals(just(5), just("four").map { 5 })

    @Test
    @DisplayName("Just: Map supertype of T -> S => Just S")
    fun mapJustSupportsSupertype() = assertEquals(just(true), just(listOf(5)).map { x: List<Int> -> x.contains(5) })

    @Test
    @DisplayName("None: Map T -> T => None T")
    fun mapNoneSameType() = assertEquals(none<Int>(), none<Int>().map { it + 1 })

    @Test
    @DisplayName("None: Map T -> S => None S")
    fun mapNoneDifferentType() = assertEquals(none<Int>(), none<String>().map { 5 })

    @Test
    @DisplayName("None: Map supertype of T -> S => None S")
    fun mapNoneSupportsSupertype() =
            assertEquals(none<Boolean>(), none<List<Int>>().map { x: List<Int> -> x.contains(5) })

    //endregion

    //region Flat-Map

    @Test
    @DisplayName("Just: Flat-map T -> Just T => Just T")
    fun flatMapJustToSameJust() = assertEquals(just(5), just(4).flatMap { just(it + 1) })

    @Test
    @DisplayName("Just: Flat-map T -> Just S => Just S")
    fun flatMapJustToDifferentJust() = assertEquals(just(5), just("four").flatMap { just(5) })

    @Test
    @DisplayName("Just: Flat-map supertype of T -> Just S => Just S")
    fun flatMapJustSupportsSupertype() =
            assertEquals(just(true), just(listOf(5)).flatMap { x: List<Int> -> just(x.contains(5)) })

    @Test
    @DisplayName("Just: Flat-map T -> None S => None S")
    fun flatMapJustToNone() = assertEquals(none<String>(), just(4).flatMap { none<String>() })

    @Test
    @DisplayName("None: Flat-map T -> Just T => None T")
    fun flatMapNoneToSameJust() = assertEquals(none<Int>(), none<Int>().flatMap { just(it + 1) })

    @Test
    @DisplayName("None: Flat-map T -> Just S => None S")
    fun flatMapNoneToDifferentJust() = assertEquals(none<Int>(), none<String>().flatMap { just(5) })

    @Test
    @DisplayName("None: Flat-map supertype of T -> Just S => None S")
    fun flatMapNoneSupportsSupertype() =
            assertEquals(none<Boolean>(), none<Collection<Int>>().flatMap { x: Collection<Int> -> just(x.contains(5)) })

    @Test
    @DisplayName("None: Flat-map T -> None S => None S")
    fun flatMapNoneToNone() = assertEquals(none<Int>(), none<String>().flatMap { none<Int>() })

    //endregion

    //region Assume

    @Test
    @DisplayName("Just: Assumption proves correct, ignoring custom exception")
    fun justAssumeCustom() =
            assertEquals("five", just("five").assume { IllegalArgumentException() })

    @Test
    @DisplayName("None: Assumption proves incorrect, using custom exception")
    fun noneAssumeCustom() {
        assertThrows(IllegalStateException::class.java) {
            none<Any>().assume { IllegalStateException() }
        }
    }

    //endregion

    //region Collapse

    @Test
    @DisplayName("Just: Collapse to own value, ignoring default supplier")
    fun justCollapseWithSupplier() = assertEquals("five", just("five").collapse { "seven" })

    @Test
    @DisplayName("None: Collapse to default value by supplier")
    fun noneCollapseWithSupplier() = assertEquals("seven", none<String>().collapse { "seven" })

    //endregion

    //region Satisfaction

    @Test
    @DisplayName("Just: Tests whether its value satisfies the given predicate")
    fun justSatisfies() {
        assertTrue(just(5).satisfies { it == 5 })
        assertFalse(just(5).satisfies { it == 6 })
    }

    @Test
    @DisplayName("None: Never satisfies the given predicate")
    fun noneNeverSatisfies() = assertFalse(none<Int>().satisfies { it == 5 })

    //endregion

    //region Preservation and Rejection

    @Test
    @DisplayName("Just: Preserves its value if it satisfies the predicate")
    fun justPreserveIfSatisfying() = assertEquals(just(5), just(5).preserveIf { it == 5 })

    @Test
    @DisplayName("Just: Does not preserve its value if it fails to satisfy the predicate")
    fun justDontPreserveIfNotSatisfying() = assertEquals(none<Any>(), just(5).preserveIf { it == 6 })

    @Test
    @DisplayName("Just: Rejects its value if it satisfies the predicate")
    fun justRejectIfSatisfying() = assertEquals(none<Any>(), just(5).rejectIf { it == 5 })

    @Test
    @DisplayName("Just: Does not reject its value if it fails to satisfy the predicate")
    fun justDontRejectIfNotSatisfying() = assertEquals(just(5), just(5).rejectIf { it == 6 })

    @Test
    @DisplayName("None: Remains none whether preserving or rejecting")
    fun nonePreserveAndReject() {
        assertEquals(none<Any>(), none<Int>().preserveIf { it == 5 })
        assertEquals(none<Any>(), none<Int>().rejectIf { it == 5 })
    }

    //endregion

    //region Side Effects

    @Test
    @DisplayName("Just: When Present, run side effect")
    fun justWhenPresentDo() {
        val box = boxed(7)
        just(5).whenPresentDo { box.value = it }
        assertTrue(box.contains(5))
    }

    @Test
    @DisplayName("Just: When Missing, ignore side effect")
    fun justWhenMissingDo() {
        val toggle = on()
        just(5).whenMissingDo { toggle.turnOff() }
        assertTrue(toggle.isOn)
    }

    @Test
    @DisplayName("Just: When Present, after running side effect, returned object should be identical")
    fun justWhenPresentDoReturn() = assertEquals(just(5), just(5).whenPresentDo { })

    @Test
    @DisplayName("Just: When Missing, after ignoring side effect, returned object should be identical")
    fun justWhenMissingDoReturn() = assertEquals(just(5), just(5).whenMissingDo { })

    @Test
    @DisplayName("None: When Present, ignore side effect")
    fun noneWhenPresentDo() {
        val box = boxed(7)
        none<Int>().whenPresentDo { box.value = 99 }
        assertTrue(box.contains(7))
    }

    @Test
    @DisplayName("None: When Missing, run side effect")
    fun noneWhenMissingDo() {
        val toggle = off()
        none<Int>().whenMissingDo { toggle.turnOn() }
        assertTrue(toggle.isOn)
    }

    @Test
    @DisplayName("None: When Present, after ignoring side effect, returned object should be identical")
    fun noneWhenPresentDoReturn() = assertEquals(none<Any>(), none<Any>().whenPresentDo { })

    @Test
    @DisplayName("None: When Missing, after running side effect, returned object should be identical")
    fun noneWhenMissingDoReturn() = assertEquals(none<Any>(), none<Any>().whenMissingDo { })

    //endregion
}