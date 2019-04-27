package io.klbz.curie

import io.klbz.curie.Box.Companion.boxed
import io.klbz.curie.Satisfaction.Companion.dissatisfies
import io.klbz.curie.Satisfaction.Companion.satisfies
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Satisfaction - Kotlin variants")
internal class SatisfactionKTest {

    //region Maybe

    @Test
    @DisplayName("Satisfied: Preserves the value")
    fun satisfiedPreserves() = assertTrue(satisfies("x").preserve().satisfies { "x" == it })

    @Test
    @DisplayName("Satisfied: Rejects the value")
    fun satisfiedRejects() = assertFalse(satisfies("x").reject().satisfies { "x" == it })

    @Test
    @DisplayName("Dissatisfied: Does not preserve the value")
    fun dissatisfiedPreserves() = assertFalse(dissatisfies("x").preserve().satisfies { "x" == it })

    @Test
    @DisplayName("Dissatisfied: Does not reject the value")
    fun dissatisfiedRejects() = assertTrue(dissatisfies("x").reject().satisfies { "x" == it })

    //endregion

    //region Collapse

    @Test
    @DisplayName("Satisfied: Collapses to satisfied production")
    fun satisfiedLazyCollapse() = assertEquals("x", satisfies(1).lazyCollapse({ "x" }, { "y" }))

    @Test
    @DisplayName("Dissatisfied: Collapses to dissatisfied production")
    fun dissatisfiedLazyCollapse() = assertEquals("y", dissatisfies(1).lazyCollapse({ "x" }, { "y" }))

    @Test
    @DisplayName("Satisfied: Collapses to satisfied transformation")
    fun satisfiedPipeCollapse() = assertEquals("wx", satisfies("w").pipeCollapse({ it + "x" }, { it + "y" }))

    @Test
    @DisplayName("Dissatisfied: Collapses to dissatisfied transformation")
    fun dissatisfiedPipeCollapse() = assertEquals("wy", dissatisfies("w").pipeCollapse({ it + "x" }, { it + "y" }))

    //endregion

    //region Side Effects

    @Test
    @DisplayName("Satisfied: When satisfied, run side effect")
    fun satisfiedWhenSatisfied() {
        val box = boxed("x")
        satisfies("y").whenSatisfiedDo { box.value = it }
        assertEquals("y", box.value)
    }

    @Test
    @DisplayName("Satisfied: When dissatisfied, ignore side effect")
    fun satisfiedWhenDissatisfied() {
        val box = boxed("x")
        satisfies("y").whenDissatisfiedDo { box.value = it }
        assertEquals("x", box.value)
    }

    @Test
    @DisplayName("Dissatisfied: When satisfied, ignore side effect")
    fun dissatisfiedWhenSatisfied() {
        val box = boxed("x")
        dissatisfies("y").whenSatisfiedDo { box.value = it }
        assertEquals("x", box.value)
    }

    @Test
    @DisplayName("Dissatisfied: When dissatisfied, run side effect")
    fun dissatisfiedWhenDissatisfied() {
        val box = boxed("x")
        dissatisfies("y").whenDissatisfiedDo { box.value = it }
        assertEquals("y", box.value)
    }

    //endregion
}