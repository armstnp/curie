package io.klbz.curie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.klbz.curie.Box.boxed;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Second Focus")
class SecondFocusTest {
	private static final String                      first  = "String";
	private static final Integer                     second = 5;
	private static final Pair<String, Integer>       pair   = Pair.of(first).and(second);
	private static final SecondFocus<String, Integer> focus  = SecondFocus.on(pair);

	//region Equality

	@Test
	@DisplayName("Equal to self")
	void selfEquality(){ assertEquals(focus, focus); }

	@Test
	@DisplayName("Equal to same")
	void sameEquality(){ assertEquals(focus, SecondFocus.on(pair)); }

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
	@DisplayName("Equal to first-focus of itself")
	void sameSecondFocusEquality(){ assertEquals(focus, FirstFocus.on(pair)); }

	//endregion

	//region Hashcode

	@Test
	@DisplayName("Same second focus hashes to same code")
	void hashSameToSame(){ assertEquals(SecondFocus.on(pair).hashCode(), focus.hashCode()); }

	@Test
	@DisplayName("Same pair hashes to same code")
	void hashFirstFocusToSame(){ assertEquals(pair.hashCode(), focus.hashCode()); }

	@Test
	@DisplayName("First focus of same pair hashes to same code")
	void hashSecondFocusToSame(){ assertEquals(pair.focusFirst().hashCode(), focus.hashCode()); }

	//endregion

	//region Unfocusing

	@Test
	@DisplayName("Unfocuses to its wrapped pair")
	void unfocuses(){ assertEquals(pair, focus.unfocus()); }

	//endregion

	//region Replacement

	@Test
	@DisplayName("Replaces the second element")
	void replaces(){ assertEquals(Pair.of(first, "replacement").focusSecond(), focus.replace("replacement")); }

	//endregion

	//region Isolate

	@Test
	@DisplayName("Isolates the second element")
	void isolates(){ assertEquals(second, focus.isolate()); }

	//endregion

	//region Map

	@Test
	@DisplayName("Maps the second element")
	void maps(){ assertEquals(Pair.of(first, second + 1).focusSecond(), focus.map(x -> x + 1)); }

	//endregion

	//region Side-Effects

	@Test
	@DisplayName("Runs side-effects on the second element")
	void runsSideEffect(){
		Box<Integer> box = boxed(0);
		focus.withDo(box::setValue);
		assertTrue(box.contains(second));
	}

	@Test
	@DisplayName("After running a side-effect on the second element, returned second-focus should be unchanged")
	void sideEffectMutatesNothing(){ assertEquals(focus, focus.withDo(f -> {})); }

	//endregion
}