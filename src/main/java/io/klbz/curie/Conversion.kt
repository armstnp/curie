package io.klbz.curie

import io.klbz.curie.Either.Companion.right
import io.klbz.curie.Unit.Companion.unit
import java.util.function.Function as fn

object Conversion {
    fun <T> maybeToEither(maybe: Maybe<T>) =
            maybe.map<Either<T, Unit>>(fn { Either.left(it) })
                    .collapse(right(unit()))

    fun <L, R> eitherToPair(either: Either<L, R>) =
            Pair.of(either.isolateL())
                    .and(either.isolateR())
}
