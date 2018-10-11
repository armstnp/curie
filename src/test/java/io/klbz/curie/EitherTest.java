package io.klbz.curie;

import io.klbz.curie.Either.InvalidAlternativeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.klbz.curie.Box.boxed;
import static io.klbz.curie.Either.left;
import static io.klbz.curie.Either.right;
import static io.klbz.curie.Maybe.just;
import static io.klbz.curie.Maybe.none;
import static io.klbz.curie.Toggle.on;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Either")
class EitherTest {
	private static final String                  leftVal     = "String";
	private static final Either<String, Integer> simpleLeft  = left(leftVal);
	private static final Integer                 rightVal    = 5;
	private static final Either<String, Integer> simpleRight = right(rightVal);

	//region Equality

	@Test
	@DisplayName("Left: Equal to self")
	void leftSelfEquality(){ assertEquals(simpleLeft, simpleLeft); }

	@Test
	@DisplayName("Left: Equal to same")
	void leftSameEquality(){ assertEquals(simpleLeft, left(leftVal)); }

	@Test
	@DisplayName("Left: Unequal to null")
	void leftNullInequality(){ assertNotEquals(simpleLeft, null); }

	@Test
	@DisplayName("Left: Unequal to other type")
	void leftOtherTypeInequality(){ assertNotEquals(simpleLeft, "other"); }

	@Test
	@DisplayName("Left: Unequal to wrapped value")
	void leftWrappedValueInequality(){ assertNotEquals(simpleLeft, leftVal); }

	@Test
	@DisplayName("Left: Unequal to right")
	void leftRightInequality(){ assertNotEquals(simpleLeft, right(leftVal)); }

	@Test
	@DisplayName("Right: Equal to self")
	void rightSelfEquality(){ assertEquals(simpleRight, simpleRight); }

	@Test
	@DisplayName("Right: Equal to same")
	void rightSameEquality(){ assertEquals(simpleRight, right(rightVal)); }

	@Test
	@DisplayName("Right: Unequal to null")
	void rightNullInequality(){ assertNotEquals(simpleRight, null); }

	@Test
	@DisplayName("Right: Unequal to other type")
	void rightOtherTypeInequality(){ assertNotEquals(simpleRight, "other"); }

	@Test
	@DisplayName("Right: Unequal to wrapped value")
	void rightWrappedValueInequality(){ assertNotEquals(simpleRight, rightVal); }

	@Test
	@DisplayName("Right: Unequal to left")
	void rightLeftInequality(){ assertNotEquals(simpleRight, left(rightVal)); }

	//endregion

	//region Hashcode

	@Test
	@DisplayName("Left: Hash same value to same code")
	void leftSameHashEquals(){ assertEquals(left(leftVal).hashCode(), simpleLeft.hashCode()); }

	@Test
	@DisplayName("Right: Hash same value to same code")
	void rightSameHashEquals(){ assertEquals(right(rightVal).hashCode(), simpleRight.hashCode()); }

	//endregion

	//region Swap

	@Test
	@DisplayName("Left: Swaps slots")
	void leftSwap(){ assertEquals(Either.<Integer, String>right(leftVal), simpleLeft.swap()); }

	@Test
	@DisplayName("Right: Swaps slots")
	void rightSwap(){ assertEquals(Either.<Integer, String>left(rightVal), simpleRight.swap()); }

	//endregion

	//region Map

	@Test
	@DisplayName("Left: Left-maps value")
	void leftMapL(){ assertEquals(left(leftVal + "Two"), simpleLeft.mapL(l -> l + "Two")); }

	@Test
	@DisplayName("Left: Ignores right-maps")
	void leftMapR(){ assertEquals(left(leftVal), simpleLeft.mapR(r -> r + 1)); }

	@Test
	@DisplayName("Right: Ignores left-maps")
	void rightMapL(){ assertEquals(right(rightVal), simpleRight.mapL(l -> l + "Two")); }

	@Test
	@DisplayName("Right: Right-maps value")
	void rightMapR(){ assertEquals(right(rightVal + 1), simpleRight.mapR(r -> r + 1)); }

	//endregion

	//region Flat-Map

	@Test
	@DisplayName("Left: Left-flat-maps to left-resulting value")
	void leftFlatMapLToL(){ assertEquals(left(leftVal + "Two"), simpleLeft.flatMapL(l -> left(l + "Two"))); }

	@Test
	@DisplayName("Left: Left-flat-maps to left-resulting value")
	void leftFlatMapLToR(){ assertEquals(right(rightVal + 1), simpleLeft.flatMapL(l -> right(l.length()))); }

	@Test
	@DisplayName("Left: Ignores right-flat-maps")
	void leftFlatMapR(){ assertEquals(left(leftVal), simpleLeft.flatMapR(r -> left(r.toString()))); }

	@Test
	@DisplayName("Right: Right-flat-maps to right-resulting value")
	void rightFlatMapRToR(){ assertEquals(right(rightVal + 1), simpleRight.flatMapR(r -> right(r + 1))); }

	@Test
	@DisplayName("Right: Right-flat-maps to left-resulting value")
	void rightFlatMapRToL(){ assertEquals(left(rightVal.toString()), simpleRight.flatMapR(r -> left(r.toString()))); }

	@Test
	@DisplayName("Right: Ignores left-flat-maps")
	void rightFlatMapL(){ assertEquals(right(rightVal), simpleRight.flatMapL(l -> right(l.length()))); }

	//endregion

	//region Collapse

	@Test
	@DisplayName("Left: Collapses into left using its value")
	void leftCollapseIntoL(){ assertEquals(leftVal, simpleLeft.collapseIntoL(Object::toString)); }

	@Test
	@DisplayName("Left: Collapses into right using left-collapse function")
	void leftCollapseIntoR(){ assertEquals((Integer) leftVal.length(), simpleLeft.collapseIntoR(String::length)); }

	@Test
	@DisplayName("Right: Collapses into left using right-collapse function")
	void rightCollapseIntoL(){ assertEquals(rightVal.toString(), simpleRight.collapseIntoL(Object::toString)); }

	@Test
	@DisplayName("Right: Collapses into right using its value")
	void rightCollapseIntoR(){ assertEquals(rightVal, simpleRight.collapseIntoR(String::length)); }

	//endregion

	//region Isolate

	@Test
	@DisplayName("Left: Left-isolates to just its value")
	void leftIsolateL(){ assertEquals(just(leftVal), simpleLeft.isolateL()); }

	@Test
	@DisplayName("Left: Right-isolates to none")
	void leftIsolateR(){ assertEquals(none(), simpleLeft.isolateR()); }

	@Test
	@DisplayName("Right: Left-isolates to none")
	void rightIsolateL(){ assertEquals(none(), simpleRight.isolateL()); }

	@Test
	@DisplayName("Right: Right-isolates to just its value")
	void rightIsolateR(){ assertEquals(just(rightVal), simpleRight.isolateR()); }

	//endregion

	//region Satisfaction

	@Test
	@DisplayName("Left: Tests whether its value satisfies left-targeted predicate")
	void leftSatisfiesL(){
		assertTrue(simpleLeft.satisfiesL(x -> x.equals(leftVal)));
		assertFalse(simpleLeft.satisfiesL(x -> x.equals(leftVal + " not")));
	}

	@Test
	@DisplayName("Left: Never satisfies a right-targeted predicate")
	void leftNeverSatisfiesR(){
		assertFalse(simpleLeft.satisfiesR(x -> x.equals(rightVal)));
	}

	@Test
	@DisplayName("Left: Uses the left-targeted predicate to test its value when provided both")
	void leftSatisfiesUsingL(){
		assertTrue(simpleLeft.satisfies(x -> x.equals(leftVal), x -> x.equals(rightVal)));
	}

	@Test
	@DisplayName("Right: Tests whether its value satisfies right-targeted predicate")
	void rightSatisfiesR(){
		assertTrue(simpleRight.satisfiesR(x -> x.equals(rightVal)));
		assertFalse(simpleRight.satisfiesR(x -> x.equals(rightVal + 1)));
	}

	@Test
	@DisplayName("Right: Never satisfies a left-targeted predicate")
	void rightNeverSatisfiesL(){
		assertFalse(simpleRight.satisfiesL(x -> x.equals(leftVal)));
	}

	@Test
	@DisplayName("Right: Uses the right-targeted predicate to test its value when provided both")
	void rightSatisfiesUsingR(){
		assertTrue(simpleRight.satisfies(x -> x.equals(leftVal), x -> x.equals(rightVal)));
	}

	//endregion

	//region Side-Effects

	@Test
	@DisplayName("Left: When Left, run side effect")
	void leftWhenLDo(){
		Box<String> box = boxed("Box");
		simpleLeft.whenLDo(box::setValue);
		assertTrue(box.contains(leftVal));
	}

	@Test
	@DisplayName("Left: When Right, ignore side effect")
	void leftWhenRDo(){
		Toggle toggle = on();
		simpleLeft.whenRDo(r -> toggle.turnOff());
		assertTrue(toggle.isOn());
	}

	@Test
	@DisplayName("Left: When Left, after performing side effect, returned object should be identical")
	void leftWhenLDoReturn(){ assertEquals(left(leftVal), simpleLeft.whenLDo(l -> {})); }

	@Test
	@DisplayName("Left: When Right, after ignoring side effect, returned object should be identical")
	void leftWhenRDoReturn(){ assertEquals(left(leftVal), simpleLeft.whenRDo(r -> {})); }

	@Test
	@DisplayName("Right: When Left, ignore side effect")
	void rightWhenLDo(){
		Toggle toggle = on();
		simpleRight.whenLDo(l -> toggle.turnOff());
		assertTrue(toggle.isOn());
	}

	@Test
	@DisplayName("Right: When Right, run side effect")
	void rightWhenRDo(){
		Box<Integer> box = boxed(0);
		simpleRight.whenRDo(box::setValue);
		assertTrue(box.contains(rightVal));
	}

	@Test
	@DisplayName("Right: When Left, after performing side effect, returned object should be identical")
	void rightWhenLDoReturn(){ assertEquals(right(rightVal), simpleRight.whenLDo(l -> {})); }

	@Test
	@DisplayName("Right: When Right, after ignoring side effect, returned object should be identical")
	void rightWhenRDoReturn(){ assertEquals(right(rightVal), simpleRight.whenRDo(r -> {})); }

	//endregion

	//region Assume

	@Test
	@DisplayName("Left: Left Assumption proves correct, returning value")
	void leftAssumeL(){ assertEquals(leftVal, simpleLeft.assumeL()); }

	@Test
	@DisplayName("Left: Right Assumption proves incorrect, throwing exception")
	void leftAssumeR(){ assertThrows(InvalidAlternativeException.class, simpleLeft::assumeR); }

	@Test
	@DisplayName("Left: Left Assumption proves correct, ignoring custom exception")
	void leftAssumeLCustom(){ assertEquals(leftVal, simpleLeft.assumeL(IllegalArgumentException::new)); }

	@Test
	@DisplayName("Left: Right Assumption proves incorrect, throwing custom exception")
	void leftAssumeRCustom(){
		assertThrows(IllegalArgumentException.class,
		             () -> simpleLeft.assumeR(IllegalArgumentException::new));
	}

	@Test
	@DisplayName("Right: Left Assumption proves incorrect, throwing exception")
	void rightAssumeL(){ assertThrows(InvalidAlternativeException.class, simpleRight::assumeL); }

	@Test
	@DisplayName("Right: Right Assumption proves correct, returning value")
	void rightAssumeR(){ assertEquals(rightVal, simpleRight.assumeR()); }

	@Test
	@DisplayName("Right: Left Assumption proves incorrect, throwing custom exception")
	void rightAssumeLCustom(){
		assertThrows(IllegalArgumentException.class,
		             () -> simpleRight.assumeL(IllegalArgumentException::new));
	}

	@Test
	@DisplayName("Right: Right Assumption proves correct, ignoring custom exception")
	void rightAssumeRCustom(){ assertEquals(rightVal, simpleRight.assumeR(IllegalArgumentException::new));}

	//endregion
}