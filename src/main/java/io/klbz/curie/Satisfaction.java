package io.klbz.curie;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.klbz.curie.Maybe.just;
import static io.klbz.curie.Maybe.none;

/**
 * Satisfaction is an intermediate structure that represents the result of applying a predicate.
 * It is designed to be almost wholly transient, to be piped to concrete values using any of a variety of
 * transparent transformations.
 *
 * A Satisfaction carries the value that either satisfied or dissatisfied some predicate. This value is applied to most
 * transformation functions.
 */
public interface Satisfaction<T> {
	static <T> Satisfaction<T> satisfies(T satisfyingValue) { return new Satisfied<>(satisfyingValue); }

	static <T> Satisfaction<T> dissatisfies(T dissatisfyingValue) { return new Dissatisfied<>(dissatisfyingValue); }

	boolean isSatisfied();

	Maybe<T> preserve();

	Maybe<T> reject();

	<S> S collapse(S ifSatisfied, S ifDissatisfied);

	<S> S lazyCollapse(Supplier<? extends S> ifSatisfied, Supplier<? extends S> ifDissatisfied);

	<S> S pipeCollapse(Function<? super T, ? extends S> ifTrue, Function<? super T, ? extends S> ifFalse);

	Satisfaction<T> whenSatisfiedDo(Consumer<? super T> doF);

	Satisfaction<T> whenDissatisfiedDo(Consumer<? super T> doF);

	class Satisfied<T> implements Satisfaction<T> {
		private final T value;

		private Satisfied(T value){ this.value = value; }

		@Override
		public boolean isSatisfied(){ return true; }

		@Override
		public Maybe<T> preserve(){ return just(value); }

		@Override
		public Maybe<T> reject(){ return none(); }

		@Override
		public <S> S collapse(S ifSatisfied, S ifDissatisfied){ return ifSatisfied; }

		@Override
		public <S> S lazyCollapse(Supplier<? extends S> ifSatisfied, Supplier<? extends S> ifDissatisfied){
			return ifSatisfied.get();
		}

		@Override
		public <S> S pipeCollapse(
				Function<? super T, ? extends S> ifSatisfied,
				Function<? super T, ? extends S> ifDissatisfied){

			return ifSatisfied.apply(value);
		}

		@Override
		public Satisfaction<T> whenSatisfiedDo(Consumer<? super T> doF){
			doF.accept(value);
			return this;
		}

		@Override
		public Satisfaction<T> whenDissatisfiedDo(Consumer<? super T> doF){ return this; }
	}

	class Dissatisfied<T> implements Satisfaction<T> {
		private final T value;

		private Dissatisfied(T value){ this.value = value; }

		@Override
		public boolean isSatisfied(){ return false; }

		@Override
		public Maybe<T> preserve(){ return none(); }

		@Override
		public Maybe<T> reject(){ return just(value); }

		@Override
		public <S> S collapse(S ifSatisfied, S ifDissatisfied){ return ifDissatisfied; }

		@Override
		public <S> S lazyCollapse(Supplier<? extends S> ifSatisfied, Supplier<? extends S> ifDissatisfied){
			return ifDissatisfied.get();
		}

		@Override
		public <S> S pipeCollapse(
				Function<? super T, ? extends S> ifSatisfied,
				Function<? super T, ? extends S> ifDissatisfied){

			return ifDissatisfied.apply(value);
		}

		@Override
		public Satisfaction<T> whenSatisfiedDo(Consumer<? super T> doF){ return this;}

		@Override
		public Satisfaction<T> whenDissatisfiedDo(Consumer<? super T> doF){
			doF.accept(value);
			return this;
		}
	}
}
