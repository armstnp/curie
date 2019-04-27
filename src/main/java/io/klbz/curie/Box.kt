package io.klbz.curie

import java.util.*

/**
 * A tiny stateful object that carries a value.
 * Useful for detecting and/or testing side effects, as references to the box can be fixed, even if its contents change.
 */
class Box<T> constructor(var value: T) {
    operator fun contains(testValue: T) = value == testValue

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Box<*>

        return value == other.value
    }

    override fun hashCode() = Objects.hash(value)

    override fun toString() = "Box{$value}"

    companion object {
        @JvmStatic
        fun <T> boxed(value: T) = Box(value)
    }
}
