package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import io.klbz.curie.Maybe.Companion.just
import io.klbz.curie.Maybe.Companion.none
import io.klbz.curie.Toggle.Companion.off
import io.klbz.curie.Toggle.Companion.on
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import io.klbz.curie.Predicate as pred
import java.util.function.Function as fn
import java.util.function.Supplier as sup

@DisplayName("Maybe")
internal class MaybeTest {
    //region Equality

    @Test
    @DisplayName("Just: Equal to self")
    fun justSelfEquality() = just(5).let { assertEquals(it, it) }

    @Test
    @DisplayName("Just: Equal to same")
    fun justSameEquality() = assertEquals(just(5), just(5))

    @Test
    @DisplayName("Just: Unequal to null")
    fun justNullInequality() = assertNotEquals(just(5), null)

    @Test
    @DisplayName("Just: Unequal to other type")
    fun justOtherTypeInequality() = assertNotEquals(just(5), "other")

    @Test
    @DisplayName("Just: Unequal to wrapped value")
    fun justWrappedValueInequality() = assertNotEquals(just(5), 5)

    @Test
    @DisplayName("Just: Unequal to none")
    fun justNoneInequality() = assertNotEquals(just(5), none<Int>())

    @Test
    @DisplayName("None: Equal to self")
    fun noneSelfEquality() = none<Int>().let { assertEquals(it, it) }

    @Test
    @DisplayName("None: Equal to same")
    fun noneSameEquality() = assertEquals(none<Int>(), none<Int>())

    @Test
    @DisplayName("Just: Equal to none of different type")
    fun noneDifferentNoneTypeEquality() =
            // Note that type erasure makes this an unfortunately necessary property for now.
            assertEquals(none<String>(), none<Int>())

    @Test
    @DisplayName("None: Unequal to null")
    fun noneNullInequality() = assertNotEquals(none<Int>(), null)

    @Test
    @DisplayName("None: Unequal to other type")
    fun noneOtherTypeInequality() = assertNotEquals(none<Int>(), "other")

    //endregion

    //region Hashcode

    @Test
    @DisplayName("Just: Hash same value to same code")
    fun justSameHashEquals() = assertEquals(just(5).hashCode(), just(5).hashCode())

    @Test
    @DisplayName("None: Hash to same code as same nones")
    fun noneHashEqualsSameTypeNoneHash() = assertEquals(none<Int>().hashCode(), none<Int>().hashCode())

    @Test
    @DisplayName("None: Hash to same code as other nones")
    fun noneHashEqualsOtherTypeNoneHash() = assertEquals(none<String>().hashCode(), none<Int>().hashCode())

    //endregion

    //region Map

    @Test
    @DisplayName("Just: Map T -> T => Just T")
    fun mapJustSameType() = assertEquals(just(5), just(4).map<Int>(fn { it + 1 }))

    @Test
    @DisplayName("Just: Map T -> S => Just S")
    fun mapJustDifferentType() = assertEquals(just(5), just("four").map<Int>(fn { 5 }))

    @Test
    @DisplayName("Just: Map supertype of T -> S => Just S")
    fun mapJustSupportsSupertype() {
        val f = fn<List<Int>, Boolean> { it.contains(5) }

        assertEquals(just(true), just(listOf(5)).map(f))
    }

    @Test
    @DisplayName("None: Map T -> T => None T")
    fun mapNoneSameType() = assertEquals(none<Int>(), none<Int>().map<Int>(fn { it + 1 }))

    @Test
    @DisplayName("None: Map T -> S => None S")
    fun mapNoneDifferentType() = assertEquals(none<Int>(), none<String>().map<Int>(fn { 5 }))

    @Test
    @DisplayName("None: Map supertype of T -> S => None S")
    fun mapNoneSupportsSupertype() {
        val f = fn<List<Int>, Boolean> { it.contains(5) }

        assertEquals(none<Boolean>(), none<List<Int>>().map(f))
    }

    //endregion

    //region Flat-Map

    @Test
    @DisplayName("Just: Flat-map T -> Just T => Just T")
    fun flatMapJustToSameJust() = assertEquals(just(5), just(4).flatMap<Int>(fn { just(it + 1) }))

    @Test
    @DisplayName("Just: Flat-map T -> Just S => Just S")
    fun flatMapJustToDifferentJust() = assertEquals(just(5), just("four").flatMap<Int>(fn { just(5) }))

    @Test
    @DisplayName("Just: Flat-map supertype of T -> Just S => Just S")
    fun flatMapJustSupportsSupertype() {
        val f = fn<List<Int>, Maybe<Boolean>> { just(it.contains(5)) }

        assertEquals(just(true), just(listOf(5)).flatMap(f))
    }

    @Test
    @DisplayName("Just: Flat-map T -> None S => None S")
    fun flatMapJustToNone() = assertEquals(none<String>(), just(4).flatMap<String>(fn { none() }))

    @Test
    @DisplayName("None: Flat-map T -> Just T => None T")
    fun flatMapNoneToSameJust() = assertEquals(none<Int>(), none<Int>().flatMap<Int>(fn { just(it + 1) }))

    @Test
    @DisplayName("None: Flat-map T -> Just S => None S")
    fun flatMapNoneToDifferentJust() = assertEquals(none<Int>(), none<String>().flatMap<Int>(fn { just(5) }))

    @Test
    @DisplayName("None: Flat-map supertype of T -> Just S => None S")
    fun flatMapNoneSupportsSupertype() {
        val f = fn<Collection<Int>, Maybe<Boolean>> { just(it.contains(5)) }

        assertEquals(none<Boolean>(), none<Collection<Int>>().flatMap(f))
    }

    @Test
    @DisplayName("None: Flat-map T -> None S => None S")
    fun flatMapNoneToNone() = assertEquals(none<Int>(), none<String>().flatMap<Int>(fn { none() }))

    //endregion

    //region Assume

    @Test
    @DisplayName("Just: Assumption proves correct, returning contained value")
    fun justAssume() = assertEquals("five", just("five").assume())

    @Test
    @DisplayName("None: Assumption proves incorrect, throwing exception")
    fun noneAssume() {
        assertThrows(Maybe.ValueNotPresentException::class.java) { none<Any>().assume() }
    }

    @Test
    @DisplayName("Just: Assumption proves correct, ignoring custom exception")
    fun justAssumeCustom() =
            assertEquals("five", just("five").assume(sup<RuntimeException> { IllegalArgumentException() }))

    @Test
    @DisplayName("None: Assumption proves incorrect, using custom exception")
    fun noneAssumeCustom() {
        assertThrows(IllegalStateException::class.java) {
            none<Any>().assume(sup<RuntimeException> { IllegalStateException() })
        }
    }

    //endregion

    //region Collapse

    @Test
    @DisplayName("Just: Collapse to own value, ignoring default value")
    fun justCollapseWithValue() = assertEquals("five", just("five").collapse("seven"))

    @Test
    @DisplayName("Just: Collapse to own value, ignoring default supplier")
    fun justCollapseWithSupplier() = assertEquals("five", just("five").collapse(sup { "seven" }))

    @Test
    @DisplayName("None: Collapse to default value")
    fun noneCollapseWithValue() = assertEquals("seven", none<Any>().collapse("seven"))

    @Test
    @DisplayName("None: Collapse to default value by supplier")
    fun noneCollapseWithSupplier() = assertEquals("seven", none<String>().collapse(sup { "seven" }))

    //endregion

    //region Satisfaction

    @Test
    @DisplayName("Just: Tests whether its value satisfies the given predicate")
    fun justSatisfies() {
        assertTrue(just(5).satisfies(pred { it == 5 }))
        assertFalse(just(5).satisfies(pred { it == 6 }))
    }

    @Test
    @DisplayName("None: Never satisfies the given predicate")
    fun noneNeverSatisfies() = assertFalse(none<Int>().satisfies(pred { it == 5 }))

    //endregion

    //region Preservation and Rejection

    @Test
    @DisplayName("Just: Preserves its value if it satisfies the predicate")
    fun justPreserveIfSatisfying() = assertEquals(just(5), just(5).preserveIf(pred { it == 5 }))

    @Test
    @DisplayName("Just: Does not preserve its value if it fails to satisfy the predicate")
    fun justDontPreserveIfNotSatisfying() = assertEquals(none<Any>(), just(5).preserveIf(pred { it == 6 }))

    @Test
    @DisplayName("Just: Rejects its value if it satisfies the predicate")
    fun justRejectIfSatisfying() = assertEquals(none<Any>(), just(5).rejectIf(pred { it == 5 }))

    @Test
    @DisplayName("Just: Does not reject its value if it fails to satisfy the predicate")
    fun justDontRejectIfNotSatisfying() = assertEquals(just(5), just(5).rejectIf(pred { it == 6 }))

    @Test
    @DisplayName("None: Remains none whether preserving or rejecting")
    fun nonePreserveAndReject() {
        assertEquals(none<Any>(), none<Int>().preserveIf(pred { it == 5 }))
        assertEquals(none<Any>(), none<Int>().rejectIf(pred { it == 5 }))
    }

    //endregion

    //region Side Effects

    @Test
    @DisplayName("Just: When Present, run side effect")
    fun justWhenPresentDo() {
        val box = boxed(7)
        just(5).whenPresentDo(Consumer { box.value = it })
        assertTrue(box.contains(5))
    }

    @Test
    @DisplayName("Just: When Missing, ignore side effect")
    fun justWhenMissingDo() {
        val toggle = on()
        just(5).whenMissingDo(SideEffect { toggle.turnOff() })
        assertTrue(toggle.isOn)
    }

    @Test
    @DisplayName("Just: When Present, after running side effect, returned object should be identical")
    fun justWhenPresentDoReturn() = assertEquals(just(5), just(5).whenPresentDo(Consumer { }))

    @Test
    @DisplayName("Just: When Missing, after ignoring side effect, returned object should be identical")
    fun justWhenMissingDoReturn() = assertEquals(just(5), just(5).whenMissingDo(SideEffect { }))

    @Test
    @DisplayName("None: When Present, ignore side effect")
    fun noneWhenPresentDo() {
        val box = boxed(7)
        none<Int>().whenPresentDo(Consumer { box.value = 99 })
        assertTrue(box.contains(7))
    }

    @Test
    @DisplayName("None: When Missing, run side effect")
    fun noneWhenMissingDo() {
        val toggle = off()
        none<Int>().whenMissingDo(SideEffect { toggle.turnOn() })
        assertTrue(toggle.isOn)
    }

    @Test
    @DisplayName("None: When Present, after ignoring side effect, returned object should be identical")
    fun noneWhenPresentDoReturn() = assertEquals(none<Any>(), none<Any>().whenPresentDo(Consumer { }))

    @Test
    @DisplayName("None: When Missing, after running side effect, returned object should be identical")
    fun noneWhenMissingDoReturn() = assertEquals(none<Any>(), none<Any>().whenMissingDo(SideEffect { }))

    //endregion
}