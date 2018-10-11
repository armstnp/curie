package io.klbz.curie;

import java.util.Objects;

import static io.klbz.curie.Satisfaction.dissatisfies;
import static io.klbz.curie.Satisfaction.satisfies;

public interface Predicate<T> extends java.util.function.Predicate<T> {

	//region Common Predicates

	static <T> Predicate<T> everTrue(){ return t -> true; }

	static <T> Predicate<T> everFalse(){ return t -> false; }

	static <T> Predicate<T> isEqual(Object targetRef){
		return (null == targetRef)
		       ? Objects::isNull
		       : targetRef::equals;
	}

	//endregion

	//region Construction

	static <T> Predicate<T> is(Predicate<T> p){ return p; }

	static <T> Predicate<T> isNot(Predicate<T> p){ return p.negate(); }

	static <T> Predicate<T> enrich(java.util.function.Predicate<T> base){
		return base instanceof Predicate ? (Predicate<T>) base : base::test;
	}

	//endregion

	//region Aggregation

	@Override
	default Predicate<T> and(java.util.function.Predicate<? super T> other){
		Objects.requireNonNull(other);
		return t -> test(t) && other.test(t);
	}

	@Override
	default Predicate<T> or(java.util.function.Predicate<? super T> other){
		Objects.requireNonNull(other);
		return t -> test(t) || other.test(t);
	}

	default Predicate<T> xor(java.util.function.Predicate<? super T> other){
		Objects.requireNonNull(other);
		return t -> test(t) ^ other.test(t);
	}

	@SafeVarargs
	static <T> Predicate<T> allOf(java.util.function.Predicate<? super T>... predicates){
		Predicate<T> aggregate = everTrue();
		for(java.util.function.Predicate<? super T> predicate : predicates){
			aggregate = aggregate.and(predicate);
		}
		return aggregate;
	}

	@SafeVarargs
	static <T> Predicate<T> anyOf(java.util.function.Predicate<? super T>... predicates){
		Predicate<T> aggregate = everFalse();
		for(java.util.function.Predicate<? super T> predicate : predicates){
			aggregate = aggregate.or(predicate);
		}
		return aggregate;
	}

	@SafeVarargs
	static <T> Predicate<T> noneOf(java.util.function.Predicate<? super T>... predicates){
		return anyOf(predicates).negate();
	}

	//endregion

	//region Adaptation

	@Override
	default Predicate<T> negate(){
		return t -> !test(t);
	}

	//endregion

	//region Application

	default Satisfaction<T> satisfy(T value){
		return test(value) ? satisfies(value) : dissatisfies(value);
	}

	//endregion
}
