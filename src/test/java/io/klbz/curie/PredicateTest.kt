package io.klbz.curie

import io.klbz.curie.Predicate.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Predicate")
internal class PredicateTest {
    //region Common Predicates

    @Test
    @DisplayName("everTrue predicates always returns true")
    fun everTrueIsEverTrue() {
        val everTrueInt = everTrue<Int>()
        assertTrue(everTrueInt.test(0))
        assertTrue(everTrueInt.test(null))
    }

    @Test
    @DisplayName("everFalse predicates always returns false")
    fun everFalseIsEverFalse() {
        val everFalseInt = everFalse<Int>()
        assertFalse(everFalseInt.test(1))
        assertFalse(everFalseInt.test(null))
    }

    @Test
    @DisplayName("isEqual predicates test for equality")
    fun isEqualTestsEquality() {
        assertTrue(isEqual<Any>("x").test("x"))
        assertFalse(isEqual<Any>(1).test(2))
    }

    @Test
    @DisplayName("isEqual handles null equality")
    fun isEqualHandlesNulls() {
        assertFalse(isEqual<Any>(null).test("x"))
        assertFalse(isEqual<Any>("x").test(null))
        assertTrue(isEqual<Any>(null).test(null))
    }

    //endregion

    //region Construction

    @Test
    @DisplayName("is creates predicates")
    fun createsPredicates() {
        assertTrue(`is`(Predicate<Any> { "x" == it }).test("x"))
        assertFalse(`is`(Predicate<Any> { "x" == it }).test("y"))
    }

    @Test
    @DisplayName("isNot creates negated predicates")
    fun createsNegatedPredicates() {
        assertFalse(isNot(Predicate<Any> { "x" == it }).test("x"))
        assertTrue(isNot(Predicate<Any> { "x" == it }).test("y"))
    }

    @Test
    @DisplayName("enrich creates Curie predicates from JDK ones")
    fun enrichesStandardPredicates() {
        val standardPredicate = java.util.function.Predicate<String> { "x" == it }
        assertTrue(enrich(standardPredicate).test("x"))
    }

    @Test
    @DisplayName("enrich is not wasteful when enriching Curie predicates")
    fun enrichesSelf() {
        val standardPredicate = Predicate<String> { "x" == it }
        assertTrue(enrich(standardPredicate).test("x"))
        assertSame(standardPredicate, enrich(standardPredicate))
    }

    //endregion

    //region Aggregation

    @Test
    @DisplayName("and expects this and another predicate to be satisfied")
    fun andAnotherPredicate() {
        val p = `is`<String> { it.contains("x") }.and { it.length == 2 }
        assertTrue(p.test("xy"))
        assertFalse(p.test("x"))
        assertFalse(p.test("yy"))
        assertFalse(p.test(""))
    }

    @Test
    @DisplayName("or accepts JDK predicates")
    fun andAnotherStandardPredicate() {
        val standardPredicate = java.util.function.Predicate<String> { it.length == 2 }
        val p = `is`<String> { it.contains("x") }.and(standardPredicate)
        assertTrue(p.test("xy"))
        assertFalse(p.test("x"))
        assertFalse(p.test("yy"))
        assertFalse(p.test(""))
    }

    @Test
    @DisplayName("or expects this or another predicate or both to be satisfied")
    fun orAnotherPredicate() {
        val p = `is`<String> { it.contains("x") }.or { it.length == 2 }
        assertTrue(p.test("xy"))
        assertTrue(p.test("x"))
        assertTrue(p.test("yy"))
        assertFalse(p.test(""))
    }

    @Test
    @DisplayName("or accepts JDK predicates")
    fun orAnotherStandardPredicate() {
        val standardPredicate = java.util.function.Predicate<String> { it.length == 2 }
        val p = `is`<String> { it.contains("x") }.or(standardPredicate)
        assertTrue(p.test("xy"))
        assertTrue(p.test("x"))
        assertTrue(p.test("yy"))
        assertFalse(p.test(""))
    }

    @Test
    @DisplayName("xor expects this or another predicate - but not both - to be satisfied")
    fun xorAnotherPredicate() {
        val p = `is`<String> { it.contains("x") }.xor { it.length == 2 }
        assertFalse(p.test("xy"))
        assertTrue(p.test("x"))
        assertTrue(p.test("yy"))
        assertFalse(p.test(""))
    }

    @Test
    @DisplayName("xor accepts JDK predicates")
    fun xorAnotherStandardPredicate() {
        val standardPredicate = java.util.function.Predicate<String> { it.length == 2 }
        val p = `is`<String> { it.contains("x") }.xor(standardPredicate)
        assertFalse(p.test("xy"))
        assertTrue(p.test("x"))
        assertTrue(p.test("yy"))
        assertFalse(p.test(""))
    }

    @Test
    @DisplayName("all of the predicates of an empty set are always satisfied")
    fun allOfAnEmptySet() = assertTrue(allOf<Any>().test("x"))

    @Test
    @DisplayName("all of the predicates of a singleton set are satisfied when that predicate is satisfied")
    fun allOfASinglePredicate() {
        val p = allOf(Predicate<String> { "x" == it })
        assertTrue(p.test("x"))
        assertFalse(p.test("y"))
    }

    @Test
    @DisplayName("all of the predicates of a set are satisfied when each predicate is satisfied")
    fun allOfMultiplePredicates() {
        val p = allOf<String>(Predicate { it.contains("x") }, Predicate { it.length == 2 })
        assertTrue(p.test("xy"))
        assertFalse(p.test("yy"))
        assertFalse(p.test("x"))
        assertFalse(p.test(""))
    }

    @Test
    @DisplayName("any of the predicates of an empty set are never satisfied")
    fun anyOfAnEmptySet() = assertFalse(anyOf<Any>().test("x"))

    @Test
    @DisplayName("any of the predicates of a singleton set are satisfied when that predicate is satisfied")
    fun anyOfASinglePredicate() {
        val p = anyOf(Predicate<String> { "x" == it })
        assertTrue(p.test("x"))
        assertFalse(p.test("y"))
    }

    @Test
    @DisplayName("any of the predicates of a set are satisfied when any one predicate is satisfied")
    fun anyOfMultiplePredicates() {
        val p = anyOf<String>(Predicate { it.contains("x") }, Predicate { it.length == 2 })
        assertTrue(p.test("xy"))
        assertTrue(p.test("yy"))
        assertTrue(p.test("x"))
        assertFalse(p.test(""))
    }

    @Test
    @DisplayName("none of the predicates of a empty set are satisfied")
    fun noneOfAnEmptySet() = assertTrue(noneOf<Any>().test("x"))

    @Test
    @DisplayName("none of the predicates of a singleton set are satisfied when that predicate is not satisfied")
    fun noneOfASinglePredicate() {
        val p = noneOf(Predicate<String> { "x" == it })
        assertFalse(p.test("x"))
        assertTrue(p.test("y"))
    }

    @Test
    @DisplayName("none of the predicates of a singleton set are satisfied when all predicates are not satisfied")
    fun noneOfMultiplePredicates() {
        val p = noneOf<String>(Predicate { it.contains("x") }, Predicate { it.length == 2 })
        assertFalse(p.test("xy"))
        assertFalse(p.test("yy"))
        assertFalse(p.test("x"))
        assertTrue(p.test(""))
    }

    //endregion

    //region Adaptation

    @Test
    @DisplayName("negate negates predicates")
    fun negate() = assertFalse(everTrue<Any>().negate().test("x"))

    //endregion

    //region Application

    @Test
    @DisplayName("satisfy reifies predicate satisfaction")
    fun satisfy() {
        assertTrue(everTrue<Any>().satisfy("x").isSatisfied)
        assertFalse(everFalse<Any>().satisfy("x").isSatisfied)
    }

    //endregion
}