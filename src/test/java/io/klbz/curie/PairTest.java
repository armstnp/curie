package io.klbz.curie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.klbz.curie.Box.boxed;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pair")
class PairTest {
	private static final String                first  = "String";
	private static final Integer               second = 5;
	private static final Pair<String, Integer> pair   = Pair.of(first).and(second);

	//region Construction

	@Test
	@DisplayName("Can provide both values in one call")
	void constructsFromBothValues(){ assertEquals(pair, Pair.of(first, second)); }

	@Test
	@DisplayName("Can provide values using 'of...and' syntax")
	void constructsByOfAnd(){ assertEquals(pair, Pair.of(first).and(second)); }

	@Test
	@DisplayName("Can provide values using 'first...second' syntax")
	void constructsByFirstSecond(){ assertEquals(pair, Pair.first(first).second(second)); }

	@Test
	@DisplayName("Can provide values using 'second...first' syntax")
	void constructsBySecondFirst(){ assertEquals(pair, Pair.second(second).first(first)); }

	//endregion

	//region Equality

	@Test
	@DisplayName("Equal to self")
	void selfEquality(){ assertEquals(pair, pair); }

	@Test
	@DisplayName("Equal to same")
	void sameEquality(){ assertEquals(pair, Pair.of(first, second)); }

	@Test
	@DisplayName("Unequal to null")
	void nullInequality(){ assertNotEquals(pair, null); }

	@Test
	@DisplayName("Unequal to unrelated type")
	void unrelatedTypeInequality(){ assertNotEquals(pair, "other"); }

	@Test
	@DisplayName("Unequal to first value")
	void firstValueInequality(){ assertNotEquals(pair, first); }

	@Test
	@DisplayName("Unequal to second value")
	void secondValueInequality(){ assertNotEquals(pair, second); }

	@Test
	@DisplayName("Equal to first-focus of itself")
	void sameFirstFocusEquality(){ assertEquals(pair, FirstFocus.on(pair)); }

	@Test
	@DisplayName("Equal to second-focus of itself")
	void sameSecondFocusEquality(){ assertEquals(pair, SecondFocus.on(pair)); }

	//endregion

	//region Hashcode

	@Test
	@DisplayName("Same pair hashes to same code")
	void hashSameToSame(){ assertEquals(pair.hashCode(), Pair.of(first, second).hashCode()); }

	@Test
	@DisplayName("First focus of same pair hashes to same code")
	void hashFirstFocusToSame(){ assertEquals(pair.hashCode(), pair.focusFirst().hashCode()); }

	@Test
	@DisplayName("Second focus of same pair hashes to same code")
	void hashSecondFocusToSame(){ assertEquals(pair.hashCode(), pair.focusSecond().hashCode()); }

	//endregion

	//region Replacement

	@Test
	@DisplayName("Replaces the first element")
	void replacesFirst(){ assertEquals(Pair.of(99, second), pair.replaceFirst(99)); }

	@Test
	@DisplayName("Replaces the second element")
	void replacesSecond(){ assertEquals(Pair.of(first, "Zinc"), pair.replaceSecond("Zinc")); }

	//endregion

	//region Focus

	@Test
	@DisplayName("Focuses on the first element")
	void focusesFirst(){
		assertEquals(FirstFocus.on(pair), pair.focusFirst());
		assertEquals(FirstFocus.class, pair.focusFirst().getClass());
	}

	@Test
	@DisplayName("Focuses on the second element")
	void focusesSecond(){
		assertEquals(SecondFocus.on(pair), pair.focusSecond());
		assertEquals(SecondFocus.class, pair.focusSecond().getClass());
	}

	//endregion

	//region Isolate

	@Test
	@DisplayName("Isolates the first element")
	void isolatesFirst(){ assertEquals(first, pair.isolateFirst()); }

	@Test
	@DisplayName("Isolates the second element")
	void isolatesSecond(){ assertEquals(second, pair.isolateSecond()); }

	//endregion

	//region Map

	@Test
	@DisplayName("Maps the first element")
	void mapsFirst(){ assertEquals(Pair.of(first.length(), second), pair.mapFirst(String::length)); }

	@Test
	@DisplayName("Maps the second element")
	void mapsSecond(){ assertEquals(Pair.of(first, second.toString()), pair.mapSecond(Object::toString)); }

	//endregion

	//region Collapse

	@Test
	@DisplayName("Collapses both elements to a single value")
	void collapses(){
		assertEquals((Integer) (first.length() + second), pair.collapse((f, s) -> f.length() + s));
	}

	//endregion

	//region Satisfies

	@Test
	@DisplayName("Satisfies a bipredicate that holds true for its elements")
	void satisfiesWhenTrue(){ assertTrue(Pair.of(5, 5).satisfies(Object::equals)); }

	@Test
	@DisplayName("Fails to satisfy a bipredicate that holds false for its elements")
	void failsToSatisfyWhenFalse(){ assertFalse(Pair.of(5, 6).satisfies(Object::equals)); }

	//endregion

	//region Side-Effects

	@Test
	@DisplayName("Runs side-effects on the first element")
	void runSideEffectWithFirst(){
		Box<String> box = boxed("value");
		pair.withFirstDo(box::setValue);
		assertTrue(box.contains(first));
	}

	@Test
	@DisplayName("After running a side-effect on the first element, returned pair should be unchanged")
	void firstSideEffectMutatesNothing(){ assertEquals(pair, pair.withFirstDo(f -> {})); }

	@Test
	@DisplayName("Runs side-effects on the first element")
	void runSideEffectWithSecond(){
		Box<Integer> box = boxed(0);
		pair.withSecondDo(box::setValue);
		assertTrue(box.contains(second));
	}

	@Test
	@DisplayName("After running a side-effect on the second element, returned pair should be unchanged")
	void secondSideEffectMutatesNothing(){ assertEquals(pair, pair.withSecondDo(s -> {})); }

	@Test
	@DisplayName("Runs side-effects with both elements")
	void runSideEffectWithBoth(){
		Box<String> strBox = boxed("value");
		Box<Integer> intBox = boxed(0);
		pair.withBothDo((f, s) -> {
			strBox.setValue(f);
			intBox.setValue(s);
		});
		assertTrue(strBox.contains(first));
		assertTrue(intBox.contains(second));
	}

	@Test
	@DisplayName("After running a side-effect on both elements, returned pair should be unchanged")
	void bothSideEffectMutatesNothing(){ assertEquals(pair, pair.withBothDo((f, s) -> {})); }

	//endregion
}