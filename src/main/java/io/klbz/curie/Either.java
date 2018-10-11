package io.klbz.curie;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.klbz.curie.Maybe.just;
import static io.klbz.curie.Maybe.none;

public interface Either<L, R> {
	static <L, R> Either<L, R> left(L value){ return new Left<>(value); }

	static <L, R> Either<L, R> right(R value){ return new Right<>(value); }

	Either<R, L> swap();

	<LPrime> Either<LPrime, R> mapL(Function<? super L, LPrime> f);

	<RPrime> Either<L, RPrime> mapR(Function<? super R, RPrime> f);

	<LPrime> Either<LPrime, R> flatMapL(Function<? super L, Either<LPrime, R>> f);

	<RPrime> Either<L, RPrime> flatMapR(Function<? super R, Either<L, RPrime>> f);

	L collapseIntoL(Function<? super R, ? extends L> f);

	R collapseIntoR(Function<? super L, ? extends R> f);

	Maybe<L> isolateL();

	Maybe<R> isolateR();

	boolean satisfiesL(Predicate<? super L> p);

	boolean satisfiesR(Predicate<? super R> p);

	boolean satisfies(Predicate<? super L> lp, Predicate<? super R> rp);

	Either<L, R> whenLDo(Consumer<L> doF);

	Either<L, R> whenRDo(Consumer<R> doF);

	L assumeL();

	L assumeL(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid);

	R assumeR();

	R assumeR(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid);

	enum Alternative {Left, Right}

	class Left<L, R> implements Either<L, R> {
		private final L value;

		private Left(L value){ this.value = value; }

		@Override
		public Either<R, L> swap(){ return right(value); }

		@Override
		public <LPrime> Either<LPrime, R> mapL(Function<? super L, LPrime> f){ return left(f.apply(value)); }

		@Override
		public <RPrime> Either<L, RPrime> mapR(Function<? super R, RPrime> f){ return left(value); }

		@Override
		public <LPrime> Either<LPrime, R> flatMapL(Function<? super L, Either<LPrime, R>> f){ return f.apply(value); }

		@Override
		public <RPrime> Either<L, RPrime> flatMapR(Function<? super R, Either<L, RPrime>> f){ return left(value); }

		@Override
		public L collapseIntoL(Function<? super R, ? extends L> f){ return value; }

		@Override
		public R collapseIntoR(Function<? super L, ? extends R> f){ return f.apply(value); }

		@Override
		public Maybe<L> isolateL(){ return just(value); }

		@Override
		public Maybe<R> isolateR(){ return none(); }

		@Override
		public boolean satisfiesL(Predicate<? super L> p){ return p.test(value); }

		@Override
		public boolean satisfiesR(Predicate<? super R> p){ return false; }

		@Override
		public boolean satisfies(Predicate<? super L> lp, Predicate<? super R> rp){ return lp.test(value); }

		@Override
		public Either<L, R> whenLDo(Consumer<L> doF){
			doF.accept(value);
			return this;
		}

		@Override
		public Either<L, R> whenRDo(Consumer<R> doF){ return this; }

		@Override
		public L assumeL(){ return value; }

		@Override
		public L assumeL(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid){ return value; }

		@Override
		public R assumeR(){ throw InvalidAlternativeException.left(); }

		@Override
		public R assumeR(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid){
			throw toThrowWhenAssumptionInvalid.get();
		}

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			Left<?, ?> left = (Left<?, ?>) o;
			return Objects.equals(value, left.value);
		}

		@Override
		public int hashCode(){ return Objects.hash(Alternative.Left, value); }

		@Override
		public String toString(){ return "Left{" + value + '}'; }
	}

	class Right<L, R> implements Either<L, R> {
		private final R value;

		private Right(R value){ this.value = value; }

		@Override
		public Either<R, L> swap(){ return left(value); }

		@Override
		public <LPrime> Either<LPrime, R> mapL(Function<? super L, LPrime> f){ return right(value); }

		@Override
		public <RPrime> Either<L, RPrime> mapR(Function<? super R, RPrime> f){ return right(f.apply(value)); }

		@Override
		public <LPrime> Either<LPrime, R> flatMapL(Function<? super L, Either<LPrime, R>> f){ return right(value); }

		@Override
		public <RPrime> Either<L, RPrime> flatMapR(Function<? super R, Either<L, RPrime>> f){ return f.apply(value); }

		@Override
		public L collapseIntoL(Function<? super R, ? extends L> f){ return f.apply(value); }

		@Override
		public R collapseIntoR(Function<? super L, ? extends R> f){ return value; }

		@Override
		public Maybe<L> isolateL(){ return none(); }

		@Override
		public Maybe<R> isolateR(){ return just(value); }

		@Override
		public boolean satisfiesL(Predicate<? super L> p){ return false; }

		@Override
		public boolean satisfiesR(Predicate<? super R> p){ return p.test(value); }

		@Override
		public boolean satisfies(Predicate<? super L> lp, Predicate<? super R> rp){ return rp.test(value); }

		@Override
		public Either<L, R> whenLDo(Consumer<L> doF){ return this; }

		@Override
		public Either<L, R> whenRDo(Consumer<R> doF){
			doF.accept(value);
			return this;
		}

		@Override
		public L assumeL(){ throw InvalidAlternativeException.right(); }

		@Override
		public L assumeL(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid){
			throw toThrowWhenAssumptionInvalid.get();
		}

		@Override
		public R assumeR(){ return value; }

		@Override
		public R assumeR(Supplier<? extends RuntimeException> toThrowWhenAssumptionInvalid){ return value; }

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			Right<?, ?> right = (Right<?, ?>) o;
			return Objects.equals(value, right.value);
		}

		@Override
		public int hashCode(){ return Objects.hash(Alternative.Right, value); }

		@Override
		public String toString(){ return "Right{" + value + '}'; }
	}

	class InvalidAlternativeException extends RuntimeException {
		private InvalidAlternativeException(Alternative attemptedAlternative){
			super("Attempted to get value of alternative " + attemptedAlternative.name() + " when it was not present" +
			      ".");
		}

		private static InvalidAlternativeException left(){ return new InvalidAlternativeException(Alternative.Left); }

		private static InvalidAlternativeException right(){
			return new InvalidAlternativeException(Alternative.Right);
		}
	}
}
