package io.klbz.curie

/**
 * A tiny stateful object that can be either off or on.
 * Useful for detecting and/or testing side effects.
 */
class Toggle private constructor(isOn: Boolean) {
    var isOn: Boolean = false
        private set

    init {
        this.isOn = isOn
    }

    fun toggle() {
        isOn = !isOn
    }

    fun turnOn() {
        isOn = true
    }

    fun turnOff() {
        isOn = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Toggle

        if (isOn != other.isOn) return false

        return true
    }

    override fun hashCode() = isOn.hashCode()

    override fun toString() = "Toggle{${if (isOn) "on" else "off"}}"

    companion object {
        @JvmStatic
        fun on() = Toggle(true)

        @JvmStatic
        fun off() = Toggle(false)
    }
}
