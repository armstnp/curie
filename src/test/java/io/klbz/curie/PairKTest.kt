package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Pair - Kotlin variants")
internal class PairKTest {

    //region Map

    @Test
    @DisplayName("Maps the first element")
    fun mapsFirst() = assertEquals(Pair.of(first.length, second), pair.mapFirst { it.length })

    @Test
    @DisplayName("Maps the second element")
    fun mapsSecond() = assertEquals(Pair.of(first, second.toString()), pair.mapSecond { it.toString() })

    //endregion

    //region Collapse

    @Test
    @DisplayName("Collapses both elements to a single value")
    fun collapses() = assertEquals(first.length + second, pair.collapse { f, s -> f.length + s })

    //endregion

    //region Satisfies

    @Test
    @DisplayName("Satisfies a bipredicate that holds true for its elements")
    fun satisfiesWhenTrue() = assertTrue(Pair.of(5, 5).satisfies { a, b -> a == b })

    @Test
    @DisplayName("Fails to satisfy a bipredicate that holds false for its elements")
    fun failsToSatisfyWhenFalse() = assertFalse(Pair.of(5, 6).satisfies { a, b -> a == b })

    //endregion

    //region Side-Effects

    @Test
    @DisplayName("Runs side-effects on the first element")
    fun runSideEffectWithFirst() {
        val box = boxed("value")
        pair.withFirstDo { box.value = it }
        assertTrue(box.contains(first))
    }

    @Test
    @DisplayName("After running a side-effect on the first element, returned pair should be unchanged")
    fun firstSideEffectMutatesNothing() = assertEquals(pair, pair.withFirstDo { })

    @Test
    @DisplayName("Runs side-effects on the first element")
    fun runSideEffectWithSecond() {
        val box = boxed(0)
        pair.withSecondDo { box.value = it }
        assertTrue(box.contains(second))
    }

    @Test
    @DisplayName("After running a side-effect on the second element, returned pair should be unchanged")
    fun secondSideEffectMutatesNothing() = assertEquals(pair, pair.withSecondDo { })

    @Test
    @DisplayName("Runs side-effects with both elements")
    fun runSideEffectWithBoth() {
        val strBox = boxed("value")
        val intBox = boxed(0)
        pair.withBothDo { f, s ->
            strBox.value = f
            intBox.value = s
        }
        assertTrue(strBox.contains(first))
        assertTrue(intBox.contains(second))
    }

    @Test
    @DisplayName("After running a side-effect on both elements, returned pair should be unchanged")
    fun bothSideEffectMutatesNothing() = assertEquals(pair, pair.withBothDo { _, _ -> })

    companion object {
        private const val first = "String"
        private const val second = 5
        private val pair = Pair.of(first).and(second)
    }

    //endregion
}