package io.klbz.curie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static io.klbz.curie.Box.boxed;
import static io.klbz.curie.Maybe.just;
import static io.klbz.curie.Maybe.none;
import static io.klbz.curie.Toggle.off;
import static io.klbz.curie.Toggle.on;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Maybe")
class MaybeTest {
	//region Equality

	@Test
	@DisplayName("Just: Equal to self")
	void justSelfEquality(){
		Maybe<Integer> x = just(5);

		assertEquals(x, x);
	}

	@Test
	@DisplayName("Just: Equal to same")
	void justSameEquality(){ assertEquals(just(5), just(5)); }

	@Test
	@DisplayName("Just: Unequal to null")
	void justNullInequality(){ assertNotEquals(just(5), null); }

	@Test
	@DisplayName("Just: Unequal to other type")
	void justOtherTypeInequality(){ assertNotEquals(just(5), "other"); }

	@Test
	@DisplayName("Just: Unequal to wrapped value")
	void justWrappedValueInequality(){ assertNotEquals(just(5), 5); }

	@Test
	@DisplayName("Just: Unequal to none")
	void justNoneInequality(){ assertNotEquals(just(5), Maybe.<Integer>none()); }

	@Test
	@DisplayName("None: Equal to self")
	void noneSelfEquality(){
		Maybe<Integer> x = none();

		assertEquals(x, x);
	}

	@Test
	@DisplayName("None: Equal to same")
	void noneSameEquality(){ assertEquals(Maybe.<Integer>none(), Maybe.<Integer>none()); }

	@Test
	@DisplayName("Just: Equal to none of different type")
		// Note that type erasure makes this an unfortunately necessary property for now.
	void noneDifferentNoneTypeEquality(){
		//noinspection AssertEqualsBetweenInconvertibleTypes
		assertEquals(Maybe.<String>none(), Maybe.<Integer>none());
	}

	@Test
	@DisplayName("None: Unequal to null")
	void noneNullInequality(){ assertNotEquals(Maybe.<Integer>none(), null); }

	@Test
	@DisplayName("None: Unequal to other type")
	void noneOtherTypeInequality(){ assertNotEquals(Maybe.<Integer>none(), "other"); }

	//endregion

	//region Hashcode

	@Test
	@DisplayName("Just: Hash same value to same code")
	void justSameHashEquals(){ assertEquals(just(5).hashCode(), just(5).hashCode()); }

	@Test
	@DisplayName("None: Hash to same code as same nones")
	void noneHashEqualsSameTypeNoneHash(){
		assertEquals(Maybe.<Integer>none().hashCode(),
		             Maybe.<Integer>none().hashCode());
	}

	@Test
	@DisplayName("None: Hash to same code as other nones")
	void noneHashEqualsOtherTypeNoneHash(){
		assertEquals(Maybe.<String>none().hashCode(),
		             Maybe.<Integer>none().hashCode());
	}

	//endregion

	//region Map

	@Test
	@DisplayName("Just: Map T -> T => Just T")
	void mapJustSameType(){ assertEquals(just(5), just(4).map(x -> x + 1)); }

	@Test
	@DisplayName("Just: Map T -> S => Just S")
	void mapJustDifferentType(){ assertEquals(just(5), just("four").map(x -> 5)); }

	@Test
	@DisplayName("Just: Map supertype of T -> S => Just S")
	void mapJustSupportsSupertype(){
		Function<Collection<Integer>, Boolean> f = x -> x.contains(5);

		assertEquals(just(true), just(singletonList(5)).map(f));
	}

	@Test
	@DisplayName("None: Map T -> T => None T")
	void mapNoneSameType(){ assertEquals(Maybe.<Integer>none(), Maybe.<Integer>none().map(x -> x + 1)); }

	@Test
	@DisplayName("None: Map T -> S => None S")
	void mapNoneDifferentType(){ assertEquals(Maybe.<Integer>none(), Maybe.<String>none().map(x -> 5)); }

	@Test
	@DisplayName("None: Map supertype of T -> S => None S")
	void mapNoneSupportsSupertype(){
		Function<Collection<Integer>, Boolean> f = x -> x.contains(5);

		assertEquals(Maybe.<Boolean>none(), Maybe.<List<Integer>>none().map(f));
	}

	//endregion

	//region Flat-Map

	@Test
	@DisplayName("Just: Flat-map T -> Just T => Just T")
	void flatMapJustToSameJust(){ assertEquals(just(5), just(4).flatMap(x -> just(x + 1))); }

	@Test
	@DisplayName("Just: Flat-map T -> Just S => Just S")
	void flatMapJustToDifferentJust(){ assertEquals(just(5), just("four").flatMap(x -> just(5))); }

	@Test
	@DisplayName("Just: Flat-map supertype of T -> Just S => Just S")
	void flatMapJustSupportsSupertype(){
		Function<Collection<Integer>, Maybe<Boolean>> f = x -> just(x.contains(5));

		assertEquals(just(true), just(singletonList(5)).flatMap(f));
	}

	@Test
	@DisplayName("Just: Flat-map T -> None S => None S")
	void flatMapJustToNone(){ assertEquals(Maybe.<String>none(), just(4).flatMap(x -> Maybe.<String>none())); }

	@Test
	@DisplayName("None: Flat-map T -> Just T => None T")
	void flatMapNoneToSameJust(){
		assertEquals(Maybe.<Integer>none(),
		             Maybe.<Integer>none().flatMap(x -> just(x + 1)));
	}

	@Test
	@DisplayName("None: Flat-map T -> Just S => None S")
	void flatMapNoneToDifferentJust(){
		assertEquals(Maybe.<Integer>none(),
		             Maybe.<String>none().flatMap(x -> just(5)));
	}

	@Test
	@DisplayName("None: Flat-map supertype of T -> Just S => None S")
	void flatMapNoneSupportsSupertype(){
		Function<Collection<Integer>, Maybe<Boolean>> f = x -> just(x.contains(5));

		assertEquals(Maybe.<Boolean>none(), Maybe.<Collection<Integer>>none().flatMap(f));
	}

	@Test
	@DisplayName("None: Flat-map T -> None S => None S")
	void flatMapNoneToNone(){
		assertEquals(Maybe.<Integer>none(),
		             Maybe.<String>none().flatMap(x -> Maybe.<Integer>none()));
	}

	//endregion

	//region Assume

	@Test
	@DisplayName("Just: Assumption proves correct, returning contained value")
	void justAssume(){ assertEquals("five", just("five").assume()); }

	@Test
	@DisplayName("None: Assumption proves incorrect, throwing exception")
	void noneAssume(){ assertThrows(Maybe.ValueNotPresentException.class, () -> none().assume()); }

	@Test
	@DisplayName("Just: Assumption proves correct, ignoring custom exception")
	void justAssumeCustom(){ assertEquals("five", just("five").assume(IllegalArgumentException::new)); }

	@Test
	@DisplayName("None: Assumption proves incorrect, using custom exception")
	void noneAssumeCustom(){
		assertThrows(IllegalStateException.class,
		             () -> none().assume(IllegalStateException::new));
	}

	//endregion

	//region Collapse

	@Test
	@DisplayName("Just: Collapse to own value, ignoring default value")
	void justCollapseWithValue(){ assertEquals("five", just("five").collapse("seven")); }

	@Test
	@DisplayName("Just: Collapse to own value, ignoring default supplier")
	void justCollapseWithSupplier(){ assertEquals("five", just("five").collapse(() -> "seven")); }

	@Test
	@DisplayName("None: Collapse to default value")
	void noneCollapseWithValue(){ assertEquals("seven", none().collapse("seven")); }

	@Test
	@DisplayName("None: Collapse to default value by supplier")
	void noneCollapseWithSupplier(){ assertEquals("seven", none().collapse(() -> "seven")); }

	//endregion

	//region Satisfaction

	@Test
	@DisplayName("Just: Tests whether its value satisfies the given predicate")
	void justSatisfies(){
		assertTrue(just(5).satisfies(x -> x == 5));
		assertFalse(just(5).satisfies(x -> x == 6));
	}

	@Test
	@DisplayName("None: Never satisfies the given predicate")
	void noneNeverSatisfies(){ assertFalse(Maybe.<Integer>none().satisfies(x -> x == 5)); }

	//endregion

	//region Preservation and Rejection

	@Test
	@DisplayName("Just: Preserves its value if it satisfies the predicate")
	void justPreserveIfSatisfying(){ assertEquals(just(5), just(5).preserveIf(x -> x == 5)); }

	@Test
	@DisplayName("Just: Does not preserve its value if it fails to satisfy the predicate")
	void justDontPreserveIfNotSatisfying(){ assertEquals(none(), just(5).preserveIf(x -> x == 6)); }

	@Test
	@DisplayName("Just: Rejects its value if it satisfies the predicate")
	void justRejectIfSatisfying(){ assertEquals(none(), just(5).rejectIf(x -> x == 5)); }

	@Test
	@DisplayName("Just: Does not reject its value if it fails to satisfy the predicate")
	void justDontRejectIfNotSatisfying(){ assertEquals(just(5), just(5).rejectIf(x -> x == 6)); }

	@Test
	@DisplayName("None: Remains none whether preserving or rejecting")
	void nonePreserveAndReject(){
		assertEquals(none(), Maybe.<Integer>none().preserveIf(x -> x == 5));
		assertEquals(none(), Maybe.<Integer>none().rejectIf(x -> x == 5));
	}

	//endregion

	//region Side Effects

	@Test
	@DisplayName("Just: When Present, run side effect")
	void justWhenPresentDo(){
		Box<Integer> box = boxed(7);
		just(5).whenPresentDo(box::setValue);
		assertTrue(box.contains(5));
	}

	@Test
	@DisplayName("Just: When Missing, ignore side effect")
	void justWhenMissingDo(){
		Toggle toggle = on();
		just(5).whenMissingDo(toggle::turnOff);
		assertTrue(toggle.isOn());
	}

	@Test
	@DisplayName("Just: When Present, after running side effect, returned object should be identical")
	void justWhenPresentDoReturn(){ assertEquals(just(5), just(5).whenPresentDo(x -> {})); }

	@Test
	@DisplayName("Just: When Missing, after ignoring side effect, returned object should be identical")
	void justWhenMissingDoReturn(){ assertEquals(just(5), just(5).whenMissingDo(() -> {})); }

	@Test
	@DisplayName("None: When Present, ignore side effect")
	void noneWhenPresentDo(){
		Box<Integer> box = boxed(7);
		Maybe.<Integer>none().whenPresentDo(x -> box.setValue(99));
		assertTrue(box.contains(7));
	}

	@Test
	@DisplayName("None: When Missing, run side effect")
	void noneWhenMissingDo(){
		Toggle toggle = off();
		Maybe.<Integer>none().whenMissingDo(toggle::turnOn);
		assertTrue(toggle.isOn());
	}

	@Test
	@DisplayName("None: When Present, after ignoring side effect, returned object should be identical")
	void noneWhenPresentDoReturn(){ assertEquals(none(), none().whenPresentDo(x -> {})); }

	@Test
	@DisplayName("None: When Missing, after running side effect, returned object should be identical")
	void noneWhenMissingDoReturn(){ assertEquals(none(), none().whenMissingDo(() -> {})); }

	//endregion
}