package io.klbz.curie;

import static io.klbz.curie.Either.right;
import static io.klbz.curie.Unit.unit;

public final class Conversion {
	private Conversion(){}

	public static <T> Either<T, Unit> maybeToEither(Maybe<T> maybe){
		return maybe
				.map(Either::<T, Unit>left)
				.collapse(right(unit()));
	}

	public static <L, R> Pair<Maybe<L>, Maybe<R>> eitherToPair(Either<L, R> either) {
		return Pair
				.of(either.isolateL())
				.and(either.isolateR());
	}
}
