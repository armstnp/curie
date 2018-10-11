package io.klbz.curie;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class FirstFocus<F, S> {
	private final Pair<F, S> pair;

	private FirstFocus(Pair<F, S> pair){ this.pair = pair; }

	public static <F, S> FirstFocus<F, S> on(Pair<F, S> pair){ return new FirstFocus<>(pair); }

	public Pair<F, S> unfocus(){ return pair; }

	public <FPrime> FirstFocus<FPrime, S> replace(FPrime newFirst){ return on(pair.replaceFirst(newFirst)); }

	public F isolate(){ return pair.isolateFirst(); }

	public <FPrime> FirstFocus<FPrime, S> map(Function<? super F, FPrime> f){ return on(pair.mapFirst(f)); }

	public FirstFocus<F, S> withDo(Consumer<F> doF){
		pair.withFirstDo(doF);
		return this;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null) return false;

		if(o.getClass() == Pair.class) return Objects.equals(pair, o);

		if(o.getClass() == FirstFocus.class){
			FirstFocus<?, ?> that = (FirstFocus<?, ?>) o;
			return Objects.equals(pair, that.pair);
		}

		if(o.getClass() == SecondFocus.class){
			SecondFocus<?, ?> that = (SecondFocus<?, ?>) o;
			return Objects.equals(pair, that.unfocus());
		}

		return false;
	}

	@Override
	public int hashCode(){ return pair.hashCode(); }

	@Override
	public String toString(){ return "FirstFocus{" + pair + '}'; }
}
