package io.klbz.curie;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Maybe<T> {
	static <T> Maybe<T> just(T value){ return new Just<>(value); }

	static <T> Maybe<T> none(){ return new None<>(); }

	<S> Maybe<S> map(Function<? super T, S> transform);

	<S> Maybe<S> flatMap(Function<? super T, Maybe<S>> transform);

	T assume();

	T assume(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid);

	T collapse(T defaultWhenNone);

	T collapse(Supplier<T> defaultWhenNone);

	boolean satisfies(Predicate<? super T> p);

	Maybe<T> preserveIf(Predicate<? super T> p);

	Maybe<T> rejectIf(Predicate<? super T> p);

	Maybe<T> whenPresentDo(Consumer<T> doF);

	Maybe<T> whenMissingDo(SideEffect doF);

	class Just<T> implements Maybe<T> {
		private final T value;

		private Just(T value){ this.value = value; }

		@Override
		public <S> Maybe<S> map(Function<? super T, S> transform){ return just(transform.apply(value)); }

		@Override
		public <S> Maybe<S> flatMap(Function<? super T, Maybe<S>> transform){ return transform.apply(value); }

		@Override
		public T assume(){ return value; }

		@Override
		public T assume(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid){ return value; }

		@Override
		public T collapse(T defaultWhenNone){ return value; }

		@Override
		public T collapse(Supplier<T> defaultWhenNone){ return value; }

		@Override
		public boolean satisfies(Predicate<? super T> p){ return p.test(value); }

		@Override
		public Maybe<T> preserveIf(Predicate<? super T> p){ return p.test(value) ? this : none(); }

		@Override
		public Maybe<T> rejectIf(Predicate<? super T> p){ return p.test(value) ? none() : this; }

		@Override
		public Maybe<T> whenPresentDo(Consumer<T> doF){
			doF.accept(value);
			return this;
		}

		@Override
		public Maybe<T> whenMissingDo(SideEffect doF){ return this; }

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			Just<?> just = (Just<?>) o;
			return Objects.equals(value, just.value);
		}

		@Override
		public int hashCode(){ return Objects.hash(value); }

		@Override
		public String toString(){ return "Just{" + value + '}'; }
	}

	class None<T> implements Maybe<T> {
		private None(){}

		@Override
		public <S> Maybe<S> map(Function<? super T, S> transform){ return none(); }

		@Override
		public <S> Maybe<S> flatMap(Function<? super T, Maybe<S>> transform){ return none(); }

		@Override
		public T assume(){ throw new ValueNotPresentException(); }

		@Override
		public T assume(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid){
			throw toThrowWhenAssumptionInvalid.get();
		}

		@Override
		public T collapse(T defaultWhenNone){ return defaultWhenNone; }

		@Override
		public T collapse(Supplier<T> defaultWhenNone){ return defaultWhenNone.get(); }

		@Override
		public boolean satisfies(Predicate<? super T> p){ return false; }

		@Override
		public Maybe<T> preserveIf(Predicate<? super T> p){ return none(); }

		@Override
		public Maybe<T> rejectIf(Predicate<? super T> p){ return none(); }

		@Override
		public Maybe<T> whenPresentDo(Consumer<T> doF){ return this; }

		@Override
		public Maybe<T> whenMissingDo(SideEffect doF){
			doF.perform();
			return this;
		}

		@Override
		public boolean equals(Object o){ return (this == o) || ((o != null) && (getClass() == o.getClass())); }

		@Override
		public int hashCode(){ return 0; }

		@Override
		public String toString(){ return "None{}"; }
	}

	class ValueNotPresentException extends RuntimeException {
		private ValueNotPresentException(){ super("Cannot provide value from a None-type Maybe"); }
	}
}
