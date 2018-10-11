package io.klbz.curie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.klbz.curie.Box.boxed;
import static io.klbz.curie.Satisfaction.dissatisfies;
import static io.klbz.curie.Satisfaction.satisfies;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Satisfaction")
class SatisfactionTest {
	//region Inspection

	@Test
	@DisplayName("Satisfied: Is satisfied")
	void satisfiedIsSatisfied(){
		assertTrue(satisfies("x").isSatisfied());
	}

	@Test
	@DisplayName("Dissatisfied: Is not satisfied")
	void dissatisfiedIsSatisfied(){
		assertFalse(dissatisfies("x").isSatisfied());
	}

	//endregion

	//region Maybe

	@Test
	@DisplayName("Satisfied: Preserves the value")
	void satisfiedPreserves(){
		assertTrue(satisfies("x").preserve().satisfies("x"::equals));
	}

	@Test
	@DisplayName("Satisfied: Rejects the value")
	void satisfiedRejects(){
		assertFalse(satisfies("x").reject().satisfies("x"::equals));
	}

	@Test
	@DisplayName("Dissatisfied: Does not preserve the value")
	void dissatisfiedPreserves(){
		assertFalse(dissatisfies("x").preserve().satisfies("x"::equals));
	}

	@Test
	@DisplayName("Dissatisfied: Does not reject the value")
	void dissatisfiedRejects(){
		assertTrue(dissatisfies("x").reject().satisfies("x"::equals));
	}

	//endregion

	//region Collapse

	@Test
	@DisplayName("Satisfied: Collapses to satisfied value")
	void satisfiedCollapse(){
		assertEquals("x", satisfies(1).collapse("x", "y"));
	}

	@Test
	@DisplayName("Dissatisfied: Collapses to dissatisfied value")
	void dissatisfiedCollapse(){
		assertEquals("y", dissatisfies(1).collapse("x", "y"));
	}

	@Test
	@DisplayName("Satisfied: Collapses to satisfied production")
	void satisfiedLazyCollapse(){
		assertEquals("x", satisfies(1).lazyCollapse(() -> "x", () -> "y"));
	}

	@Test
	@DisplayName("Dissatisfied: Collapses to dissatisfied production")
	void dissatisfiedLazyCollapse(){
		assertEquals("y", dissatisfies(1).lazyCollapse(() -> "x", () -> "y"));
	}

	@Test
	@DisplayName("Satisfied: Collapses to satisfied transformation")
	void satisfiedPipeCollapse(){
		assertEquals("wx", satisfies("w").pipeCollapse(s -> s + "x", s -> s + "y"));
	}

	@Test
	@DisplayName("Dissatisfied: Collapses to dissatisfied transformation")
	void dissatisfiedPipeCollapse(){
		assertEquals("wy", dissatisfies("w").pipeCollapse(s -> s + "x", s -> s + "y"));
	}

	//endregion

	//region Side Effects

	@Test
	@DisplayName("Satisfied: When satisfied, run side effect")
	void satisfiedWhenSatisfied(){
		Box<String> box = boxed("x");
		satisfies("y").whenSatisfiedDo(box::setValue);
		assertEquals("y", box.getValue());
	}

	@Test
	@DisplayName("Satisfied: When dissatisfied, ignore side effect")
	void satisfiedWhenDissatisfied(){
		Box<String> box = boxed("x");
		satisfies("y").whenDissatisfiedDo(box::setValue);
		assertEquals("x", box.getValue());
	}

	@Test
	@DisplayName("Dissatisfied: When satisfied, ignore side effect")
	void dissatisfiedWhenSatisfied(){
		Box<String> box = boxed("x");
		dissatisfies("y").whenSatisfiedDo(box::setValue);
		assertEquals("x", box.getValue());
	}

	@Test
	@DisplayName("Dissatisfied: When dissatisfied, run side effect")
	void dissatisfiedWhenDissatisfied(){
		Box<String> box = boxed("x");
		dissatisfies("y").whenDissatisfiedDo(box::setValue);
		assertEquals("y", box.getValue());
	}

	//endregion
}