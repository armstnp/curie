package io.klbz.curie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.klbz.curie.Box.boxed;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("First Focus")
class FirstFocusTest {
	private static final String                      first  = "String";
	private static final Integer                     second = 5;
	private static final Pair<String, Integer>       pair   = Pair.of(first).and(second);
	private static final FirstFocus<String, Integer> focus  = FirstFocus.on(pair);

	//region Equality

	@Test
	@DisplayName("Equal to self")
	void selfEquality(){ assertEquals(focus, focus); }

	@Test
	@DisplayName("Equal to same")
	void sameEquality(){ assertEquals(focus, FirstFocus.on(pair)); }

	@Test
	@DisplayName("Unequal to null")
	void nullInequality(){ assertNotEquals(focus, null); }

	@Test
	@DisplayName("Unequal to unrelated type")
	void unrelatedTypeInequality(){ assertNotEquals(focus, "other"); }

	@Test
	@DisplayName("Unequal to first value")
	void firstValueInequality(){ assertNotEquals(focus, first); }

	@Test
	@DisplayName("Unequal to second value")
	void secondValueInequality(){ assertNotEquals(focus, second); }

	@Test
	@DisplayName("Equal to its pair")
	void samePairEquality(){ assertEquals(focus, pair); }

	@Test
	@DisplayName("Equal to second-focus of itself")
	void sameSecondFocusEquality(){ assertEquals(focus, SecondFocus.on(pair)); }

	//endregion

	//region Hashcode

	@Test
	@DisplayName("Same first focus hashes to same code")
	void hashSameToSame(){ assertEquals(FirstFocus.on(pair).hashCode(), focus.hashCode()); }

	@Test
	@DisplayName("Same pair hashes to same code")
	void hashFirstFocusToSame(){ assertEquals(pair.hashCode(), focus.hashCode()); }

	@Test
	@DisplayName("Second focus of same pair hashes to same code")
	void hashSecondFocusToSame(){ assertEquals(pair.focusSecond().hashCode(), focus.hashCode()); }

	//endregion

	//region Unfocusing

	@Test
	@DisplayName("Unfocuses to its wrapped pair")
	void unfocuses(){ assertEquals(pair, focus.unfocus()); }

	//endregion

	//region Replacement

	@Test
	@DisplayName("Replaces the first element")
	void replaces(){ assertEquals(Pair.of(99, second).focusFirst(), focus.replace(99)); }

	//endregion

	//region Isolate

	@Test
	@DisplayName("Isolates the first element")
	void isolates(){ assertEquals(first, focus.isolate()); }

	//endregion

	//region Map

	@Test
	@DisplayName("Maps the first element")
	void maps(){ assertEquals(Pair.of(first.length(), second).focusFirst(), focus.map(String::length)); }

	//endregion

	//region Side-Effects

	@Test
	@DisplayName("Runs side-effects on the first element")
	void runsSideEffect(){
		Box<String> box = boxed("value");
		focus.withDo(box::setValue);
		assertTrue(box.contains(first));
	}

	@Test
	@DisplayName("After running a side-effect on the first element, returned first-focus should be unchanged")
	void sideEffectMutatesNothing(){ assertEquals(focus, focus.withDo(f -> {})); }

	//endregion
}