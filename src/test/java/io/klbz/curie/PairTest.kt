package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.BiFunction as bifn
import java.util.function.BiPredicate as bipred
import java.util.function.Function as fn

@DisplayName("Pair")
internal class PairTest {

    //region Construction

    @Test
    @DisplayName("Can provide both values in one call")
    fun constructsFromBothValues() = assertEquals(pair, Pair.of(first, second))

    @Test
    @DisplayName("Can provide values using 'of...and' syntax")
    fun constructsByOfAnd() = assertEquals(pair, Pair.of(first).and(second))

    @Test
    @DisplayName("Can provide values using 'first...second' syntax")
    fun constructsByFirstSecond() = assertEquals(pair, Pair.first(first).second(second))

    @Test
    @DisplayName("Can provide values using 'second...first' syntax")
    fun constructsBySecondFirst() = assertEquals(pair, Pair.second(second).first(first))

    //endregion

    //region Equality

    @Test
    @DisplayName("Equal to self")
    fun selfEquality() = assertEquals(pair, pair)

    @Test
    @DisplayName("Equal to same")
    fun sameEquality() = assertEquals(pair, Pair.of(first, second))

    @Test
    @DisplayName("Unequal to null")
    fun nullInequality() = assertNotEquals(pair, null)

    @Test
    @DisplayName("Unequal to unrelated type")
    fun unrelatedTypeInequality() = assertNotEquals(pair, "other")

    @Test
    @DisplayName("Unequal to first value")
    fun firstValueInequality() = assertNotEquals(pair, first)

    @Test
    @DisplayName("Unequal to second value")
    fun secondValueInequality() = assertNotEquals(pair, second)

    @Test
    @DisplayName("Equal to first-focus of itself")
    fun sameFirstFocusEquality() = assertEquals(pair, FirstFocus.on(pair))

    @Test
    @DisplayName("Equal to second-focus of itself")
    fun sameSecondFocusEquality() = assertEquals(pair, SecondFocus.on(pair))

    //endregion

    //region Hashcode

    @Test
    @DisplayName("Same pair hashes to same code")
    fun hashSameToSame() = assertEquals(pair.hashCode(), Pair.of(first, second).hashCode())

    @Test
    @DisplayName("First focus of same pair hashes to same code")
    fun hashFirstFocusToSame() = assertEquals(pair.hashCode(), pair.focusFirst().hashCode())

    @Test
    @DisplayName("Second focus of same pair hashes to same code")
    fun hashSecondFocusToSame() = assertEquals(pair.hashCode(), pair.focusSecond().hashCode())

    //endregion

    //region Replacement

    @Test
    @DisplayName("Replaces the first element")
    fun replacesFirst() = assertEquals(Pair.of(99, second), pair.replaceFirst(99))

    @Test
    @DisplayName("Replaces the second element")
    fun replacesSecond() = assertEquals(Pair.of(first, "Zinc"), pair.replaceSecond("Zinc"))

    //endregion

    //region Focus

    @Test
    @DisplayName("Focuses on the first element")
    fun focusesFirst() {
        assertEquals(FirstFocus.on(pair), pair.focusFirst())
        assertEquals(FirstFocus::class.java, pair.focusFirst().javaClass)
    }

    @Test
    @DisplayName("Focuses on the second element")
    fun focusesSecond() {
        assertEquals(SecondFocus.on(pair), pair.focusSecond())
        assertEquals(SecondFocus::class.java, pair.focusSecond().javaClass)
    }

    //endregion

    //region Isolate

    @Test
    @DisplayName("Isolates the first element")
    fun isolatesFirst() = assertEquals(first, pair.isolateFirst())

    @Test
    @DisplayName("Isolates the second element")
    fun isolatesSecond() = assertEquals(second, pair.isolateSecond())

    //endregion

    //region Map

    @Test
    @DisplayName("Maps the first element")
    fun mapsFirst() = assertEquals(Pair.of(first.length, second), pair.mapFirst<Int>(fn { it.length }))

    @Test
    @DisplayName("Maps the second element")
    fun mapsSecond() = assertEquals(Pair.of(first, second.toString()), pair.mapSecond<String>(fn { it.toString() }))

    //endregion

    //region Collapse

    @Test
    @DisplayName("Collapses both elements to a single value")
    fun collapses() = assertEquals(first.length + second, pair.collapse(bifn { f, s -> f.length + s }))

    //endregion

    //region Satisfies

    @Test
    @DisplayName("Satisfies a bipredicate that holds true for its elements")
    fun satisfiesWhenTrue() = assertTrue(Pair.of(5, 5).satisfies(bipred { a, b -> a == b }))

    @Test
    @DisplayName("Fails to satisfy a bipredicate that holds false for its elements")
    fun failsToSatisfyWhenFalse() = assertFalse(Pair.of(5, 6).satisfies(bipred { a, b -> a == b }))

    //endregion

    //region Side-Effects

    @Test
    @DisplayName("Runs side-effects on the first element")
    fun runSideEffectWithFirst() {
        val box = boxed("value")
        pair.withFirstDo(Consumer { box.value = it })
        assertTrue(box.contains(first))
    }

    @Test
    @DisplayName("After running a side-effect on the first element, returned pair should be unchanged")
    fun firstSideEffectMutatesNothing() = assertEquals(pair, pair.withFirstDo(Consumer { }))

    @Test
    @DisplayName("Runs side-effects on the first element")
    fun runSideEffectWithSecond() {
        val box = boxed(0)
        pair.withSecondDo(Consumer { box.value = it })
        assertTrue(box.contains(second))
    }

    @Test
    @DisplayName("After running a side-effect on the second element, returned pair should be unchanged")
    fun secondSideEffectMutatesNothing() = assertEquals(pair, pair.withSecondDo(Consumer { }))

    @Test
    @DisplayName("Runs side-effects with both elements")
    fun runSideEffectWithBoth() {
        val strBox = boxed("value")
        val intBox = boxed(0)
        pair.withBothDo(BiConsumer { f, s ->
            strBox.value = f
            intBox.value = s
        })
        assertTrue(strBox.contains(first))
        assertTrue(intBox.contains(second))
    }

    @Test
    @DisplayName("After running a side-effect on both elements, returned pair should be unchanged")
    fun bothSideEffectMutatesNothing() = assertEquals(pair, pair.withBothDo(BiConsumer { _, _ -> }))

    companion object {
        private const val first = "String"
        private const val second = 5
        private val pair = Pair.of(first).and(second)
    }

    //endregion
}