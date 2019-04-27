package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Second Focus - Kotlin variants")
internal class SecondFocusKTest {

    //region Map

    @Test
    @DisplayName("Maps the second element")
    fun maps() = assertEquals(Pair.of(first, second + 1).focusSecond(), focus.map { it + 1 })

    //endregion

    //region Side-Effects

    @Test
    @DisplayName("Runs side-effects on the second element")
    fun runsSideEffect() {
        val box = boxed(0)
        focus.withDo { box.value = it }
        assertTrue(box.contains(second))
    }

    @Test
    @DisplayName("After running a side-effect on the second element, returned second-focus should be unchanged")
    fun sideEffectMutatesNothing() = assertEquals(focus, focus.withDo { })

    companion object {
        private const val first = "String"
        private const val second = 5
        private val pair = Pair.of(first).and(second)
        private val focus = SecondFocus.on(pair)
    }

    //endregion
}