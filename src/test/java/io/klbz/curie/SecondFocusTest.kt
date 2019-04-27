package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.function.Consumer
import java.util.function.Function as fn

@DisplayName("Second Focus")
internal class SecondFocusTest {

    //region Equality

    @Test
    @DisplayName("Equal to self")
    fun selfEquality() = assertEquals(focus, focus)

    @Test
    @DisplayName("Equal to same")
    fun sameEquality() = assertEquals(focus, SecondFocus.on(pair))

    @Test
    @DisplayName("Unequal to null")
    fun nullInequality() = assertNotEquals(focus, null)

    @Test
    @DisplayName("Unequal to unrelated type")
    fun unrelatedTypeInequality() = assertNotEquals(focus, "other")

    @Test
    @DisplayName("Unequal to first value")
    fun firstValueInequality() = assertNotEquals(focus, first)

    @Test
    @DisplayName("Unequal to second value")
    fun secondValueInequality() = assertNotEquals(focus, second)

    @Test
    @DisplayName("Equal to its pair")
    fun samePairEquality() = assertEquals(focus, pair)

    @Test
    @DisplayName("Equal to first-focus of itself")
    fun sameSecondFocusEquality() = assertEquals(focus, FirstFocus.on(pair))

    //endregion

    //region Hashcode

    @Test
    @DisplayName("Same second focus hashes to same code")
    fun hashSameToSame() = assertEquals(SecondFocus.on(pair).hashCode(), focus.hashCode())

    @Test
    @DisplayName("Same pair hashes to same code")
    fun hashFirstFocusToSame() = assertEquals(pair.hashCode(), focus.hashCode())

    @Test
    @DisplayName("First focus of same pair hashes to same code")
    fun hashSecondFocusToSame() = assertEquals(pair.focusFirst().hashCode(), focus.hashCode())

    //endregion

    //region Unfocusing

    @Test
    @DisplayName("Unfocuses to its wrapped pair")
    fun unfocuses() = assertEquals(pair, focus.unfocus())

    //endregion

    //region Replacement

    @Test
    @DisplayName("Replaces the second element")
    fun replaces() = assertEquals(Pair.of(first, "replacement").focusSecond(), focus.replace("replacement"))

    //endregion

    //region Isolate

    @Test
    @DisplayName("Isolates the second element")
    fun isolates() = assertEquals(second, focus.isolate())

    //endregion

    //region Map

    @Test
    @DisplayName("Maps the second element")
    fun maps() = assertEquals(Pair.of(first, second + 1).focusSecond(), focus.map<Int>(fn { it + 1 }))

    //endregion

    //region Side-Effects

    @Test
    @DisplayName("Runs side-effects on the second element")
    fun runsSideEffect() {
        val box = boxed(0)
        focus.withDo(Consumer { box.value = it })
        assertTrue(box.contains(second))
    }

    @Test
    @DisplayName("After running a side-effect on the second element, returned second-focus should be unchanged")
    fun sideEffectMutatesNothing() = assertEquals(focus, focus.withDo(Consumer { }))

    companion object {
        private const val first = "String"
        private const val second = 5
        private val pair = Pair.of(first).and(second)
        private val focus = SecondFocus.on(pair)
    }

    //endregion
}