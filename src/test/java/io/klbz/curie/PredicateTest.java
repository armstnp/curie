package io.klbz.curie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.klbz.curie.Predicate.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Predicate")
class PredicateTest {
	//region Common Predicates

	@Test
	@DisplayName("everTrue predicates always returns true")
	void everTrueIsEverTrue(){
		Predicate<Integer> everTrueInt = everTrue();
		assertTrue(everTrueInt.test(0));
		assertTrue(everTrueInt.test(null));
	}

	@Test
	@DisplayName("everFalse predicates always returns false")
	void everFalseIsEverFalse(){
		Predicate<Integer> everFalseInt = everFalse();
		assertFalse(everFalseInt.test(1));
		assertFalse(everFalseInt.test(null));
	}

	@Test
	@DisplayName("isEqual predicates test for equality")
	void isEqualTestsEquality(){
		assertTrue(isEqual("x").test("x"));
		assertFalse(isEqual(1).test(2));
	}

	@Test
	@DisplayName("isEqual handles null equality")
	void isEqualHandlesNulls(){
		assertFalse(isEqual(null).test("x"));
		assertFalse(isEqual("x").test(null));
		assertTrue(isEqual(null).test(null));
	}

	//endregion

	//region Construction

	@Test
	@DisplayName("is creates predicates")
	void createsPredicates(){
		assertTrue(is("x"::equals).test("x"));
		assertFalse(is("x"::equals).test("y"));
	}

	@Test
	@DisplayName("isNot creates negated predicates")
	void createsNegatedPredicates(){
		assertFalse(isNot("x"::equals).test("x"));
		assertTrue(isNot("x"::equals).test("y"));
	}

	@Test
	@DisplayName("enrich creates Curie predicates from JDK ones")
	void enrichesStandardPredicates(){
		java.util.function.Predicate<String> standardPredicate = "x"::equals;
		assertTrue(enrich(standardPredicate).test("x"));
	}

	@Test
	@DisplayName("enrich is not wasteful when enriching Curie predicates")
	void enrichesSelf(){
		Predicate<String> standardPredicate = "x"::equals;
		assertTrue(enrich(standardPredicate).test("x"));
		assertSame(standardPredicate, enrich(standardPredicate));
	}

	//endregion

	//region Aggregation

	@Test
	@DisplayName("and expects this and another predicate to be satisfied")
	void andAnotherPredicate(){
		Predicate<String> p = is((String x) -> x.contains("x")).and((String x) -> x.length() == 2);
		assertTrue(p.test("xy"));
		assertFalse(p.test("x"));
		assertFalse(p.test("yy"));
		assertFalse(p.test(""));
	}

	@Test
	@DisplayName("or accepts JDK predicates")
	void andAnotherStandardPredicate(){
		java.util.function.Predicate<String> standardPredicate = x -> x.length() == 2;
		Predicate<String> p = is((String x) -> x.contains("x")).and(standardPredicate);
		assertTrue(p.test("xy"));
		assertFalse(p.test("x"));
		assertFalse(p.test("yy"));
		assertFalse(p.test(""));
	}

	@Test
	@DisplayName("or expects this or another predicate or both to be satisfied")
	void orAnotherPredicate(){
		Predicate<String> p = is((String x) -> x.contains("x")).or((String x) -> x.length() == 2);
		assertTrue(p.test("xy"));
		assertTrue(p.test("x"));
		assertTrue(p.test("yy"));
		assertFalse(p.test(""));
	}

	@Test
	@DisplayName("or accepts JDK predicates")
	void orAnotherStandardPredicate(){
		java.util.function.Predicate<String> standardPredicate = x -> x.length() == 2;
		Predicate<String> p = is((String x) -> x.contains("x")).or(standardPredicate);
		assertTrue(p.test("xy"));
		assertTrue(p.test("x"));
		assertTrue(p.test("yy"));
		assertFalse(p.test(""));
	}

	@Test
	@DisplayName("xor expects this or another predicate - but not both - to be satisfied")
	void xorAnotherPredicate(){
		Predicate<String> p = is((String x) -> x.contains("x")).xor((String x) -> x.length() == 2);
		assertFalse(p.test("xy"));
		assertTrue(p.test("x"));
		assertTrue(p.test("yy"));
		assertFalse(p.test(""));
	}

	@Test
	@DisplayName("xor accepts JDK predicates")
	void xorAnotherStandardPredicate(){
		java.util.function.Predicate<String> standardPredicate = x -> x.length() == 2;
		Predicate<String> p = is((String x) -> x.contains("x")).xor(standardPredicate);
		assertFalse(p.test("xy"));
		assertTrue(p.test("x"));
		assertTrue(p.test("yy"));
		assertFalse(p.test(""));
	}

	@Test
	@DisplayName("all of the predicates of an empty set are always satisfied")
	void allOfAnEmptySet(){
		assertTrue(allOf().test("x"));
	}

	@Test
	@DisplayName("all of the predicates of a singleton set are satisfied when that predicate is satisfied")
	void allOfASinglePredicate(){
		Predicate<String> p = allOf("x"::equals);
		assertTrue(p.test("x"));
		assertFalse(p.test("y"));
	}

	@Test
	@DisplayName("all of the predicates of a set are satisfied when each predicate is satisfied")
	void allOfMultiplePredicates(){
		Predicate<String> p = allOf((String x) -> x.contains("x"), (String x) -> x.length() == 2);
		assertTrue(p.test("xy"));
		assertFalse(p.test("yy"));
		assertFalse(p.test("x"));
		assertFalse(p.test(""));
	}

	@Test
	@DisplayName("any of the predicates of an empty set are never satisfied")
	void anyOfAnEmptySet(){
		assertFalse(anyOf().test("x"));
	}

	@Test
	@DisplayName("any of the predicates of a singleton set are satisfied when that predicate is satisfied")
	void anyOfASinglePredicate(){
		Predicate<String> p = anyOf("x"::equals);
		assertTrue(p.test("x"));
		assertFalse(p.test("y"));
	}

	@Test
	@DisplayName("any of the predicates of a set are satisfied when any one predicate is satisfied")
	void anyOfMultiplePredicates(){
		Predicate<String> p = anyOf((String x) -> x.contains("x"), (String x) -> x.length() == 2);
		assertTrue(p.test("xy"));
		assertTrue(p.test("yy"));
		assertTrue(p.test("x"));
		assertFalse(p.test(""));
	}

	@Test
	@DisplayName("none of the predicates of a empty set are satisfied")
	void noneOfAnEmptySet(){
		assertTrue(noneOf().test("x"));
	}

	@Test
	@DisplayName("none of the predicates of a singleton set are satisfied when that predicate is not satisfied")
	void noneOfASinglePredicate(){
		Predicate<String> p = noneOf("x"::equals);
		assertFalse(p.test("x"));
		assertTrue(p.test("y"));
	}

	@Test
	@DisplayName("none of the predicates of a singleton set are satisfied when all predicates are not satisfied")
	void noneOfMultiplePredicates(){
		Predicate<String> p = noneOf((String x) -> x.contains("x"), (String x) -> x.length() == 2);
		assertFalse(p.test("xy"));
		assertFalse(p.test("yy"));
		assertFalse(p.test("x"));
		assertTrue(p.test(""));
	}

	//endregion

	//region Adaptation

	@Test
	@DisplayName("negate negates predicates")
	void negate(){
		assertFalse(everTrue().negate().test("x"));
	}

	//endregion

	//region Application

	@Test
	@DisplayName("satisfy reifies predicate satisfaction")
	void satisfy(){
		assertTrue(everTrue().satisfy("x").isSatisfied());
		assertFalse(everFalse().satisfy("x").isSatisfied());
	}

	//endregion
}