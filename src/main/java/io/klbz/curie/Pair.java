package io.klbz.curie;

import java.util.Objects;
import java.util.function.*;

public final class Pair<F, S> {
	private final F first;
	private final S second;

	private Pair(F first, S second){
		this.first = first;
		this.second = second;
	}

	public static <F, S> Pair<F, S> of(F first, S second){ return new Pair<>(first, second); }

	public static <F> PairOfAndBuilder<F> of(F first){ return new PairOfAndBuilder<>(first); }

	public static <F> PairBuilderMissingSecond<F> first(F first){ return new PairBuilderMissingSecond<>(first); }

	public static <S> PairBuilderMissingFirst<S> second(S second){ return new PairBuilderMissingFirst<>(second); }

	public <FPrime> Pair<FPrime, S> replaceFirst(FPrime newFirst){ return Pair.of(newFirst, second); }

	public <SPrime> Pair<F, SPrime> replaceSecond(SPrime newSecond){ return Pair.of(first, newSecond); }

	public FirstFocus<F, S> focusFirst(){ return FirstFocus.on(this); }

	public SecondFocus<F, S> focusSecond(){ return SecondFocus.on(this); }

	public F isolateFirst(){ return first; }

	public S isolateSecond(){ return second; }

	public <FPrime> Pair<FPrime, S> mapFirst(Function<? super F, FPrime> f){ return of(f.apply(first), second); }

	public <SPrime> Pair<F, SPrime> mapSecond(Function<? super S, SPrime> f){ return of(first, f.apply(second)); }

	public <T> T collapse(BiFunction<? super F, ? super S, T> f){ return f.apply(first, second); }

	public boolean satisfies(BiPredicate<? super F, ? super S> p){ return p.test(first, second); }

	public Pair<F, S> withFirstDo(Consumer<F> doF){
		doF.accept(first);
		return this;
	}

	public Pair<F, S> withSecondDo(Consumer<S> doF){
		doF.accept(second);
		return this;
	}

	public Pair<F, S> withBothDo(BiConsumer<F, S> doF){
		doF.accept(first, second);
		return this;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null) return false;

		if(o.getClass() == FirstFocus.class){
			FirstFocus<?, ?> that = (FirstFocus<?, ?>) o;
			return Objects.equals(this, that.unfocus());
		}

		if(o.getClass() == SecondFocus.class){
			SecondFocus<?, ?> that = (SecondFocus<?, ?>) o;
			return Objects.equals(this, that.unfocus());
		}

		if(getClass() != o.getClass()) return false;

		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(first, pair.first) &&
		       Objects.equals(second, pair.second);
	}

	@Override
	public int hashCode(){ return Objects.hash(first, second); }

	@Override
	public String toString(){ return "Pair{" + first + "," + second + '}'; }

	public static final class PairOfAndBuilder<F> {
		private final F first;

		private PairOfAndBuilder(F first){ this.first = first; }

		public <S> Pair<F, S> and(S second){ return Pair.of(first, second); }
	}

	public static final class PairBuilderMissingSecond<F> {
		private final F first;

		private PairBuilderMissingSecond(F first){ this.first = first; }

		public <S> Pair<F, S> second(S second){ return Pair.of(first, second); }
	}

	public static final class PairBuilderMissingFirst<S> {
		private final S second;

		private PairBuilderMissingFirst(S second){ this.second = second; }

		public <F> Pair<F, S> first(F first){ return Pair.of(first, second); }
	}
}
