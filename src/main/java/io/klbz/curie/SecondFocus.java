package io.klbz.curie;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class SecondFocus<F, S> {
	private final Pair<F, S> pair;

	private SecondFocus(Pair<F, S> pair){ this.pair = pair; }

	public static <F, S> SecondFocus<F, S> on(Pair<F, S> pair){ return new SecondFocus<>(pair); }

	public Pair<F, S> unfocus(){ return pair; }

	public <SPrime> SecondFocus<F, SPrime> replace(SPrime newSecond){ return on(pair.replaceSecond(newSecond)); }

	public S isolate(){ return pair.isolateSecond(); }

	public <SPrime> SecondFocus<F, SPrime> map(Function<? super S, SPrime> f){ return on(pair.mapSecond(f)); }

	public SecondFocus<F, S> withDo(Consumer<S> doF){ return on(pair.withSecondDo(doF)); }

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null) return false;

		if(o.getClass() == Pair.class) return Objects.equals(pair, o);

		if(o.getClass() == FirstFocus.class){
			FirstFocus<?, ?> that = (FirstFocus<?, ?>) o;
			return Objects.equals(pair, that.unfocus());
		}

		if(o.getClass() == SecondFocus.class){
			SecondFocus<?, ?> that = (SecondFocus<?, ?>) o;
			return Objects.equals(pair, that.pair);
		}

		return false;
	}

	@Override
	public int hashCode(){ return pair.hashCode(); }

	@Override
	public String toString(){ return "SecondFocus{" + pair + '}'; }
}
