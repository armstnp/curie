package io.klbz.curie

class Unit private constructor() {
    override fun equals(other: Any?) = this === other

    override fun hashCode() = 0

    override fun toString() = "Unit"

    companion object {
        @JvmStatic
        val unit = Unit()

        @JvmStatic
        fun unit() = unit
    }
}
