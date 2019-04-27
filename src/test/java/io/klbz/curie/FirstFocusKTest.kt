package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("First Focus - Kotlin variants")
internal class FirstFocusKTest {

    //region Map

    @Test
    @DisplayName("Maps the first element")
    fun maps() = assertEquals(Pair.of(first.length, second).focusFirst(), focus.map { it.length })

    //endregion

    //region Side-Effects

    @Test
    @DisplayName("Runs side-effects on the first element")
    fun runsSideEffect() {
        val box = boxed("value")
        focus.withDo { box.value = it }
        assertTrue(box.contains(first))
    }

    @Test
    @DisplayName("After running a side-effect on the first element, returned first-focus should be unchanged")
    fun sideEffectMutatesNothing() = assertEquals(focus, focus.withDo { })

    companion object {
        private const val first = "String"
        private const val second = 5
        private val pair = Pair.of(first).and(second)
        private val focus = FirstFocus.on(pair)
    }

    //endregion
}