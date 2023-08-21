package nl.avwie.borklang.interpreter

sealed interface BorkValue {
    data object Nil : BorkValue
    data object True : BorkValue
    data object False : BorkValue

    data class Number(val value: Int) : BorkValue

    data class Boolean(val value: kotlin.Boolean) : BorkValue

    data class String(val value: kotlin.String) : BorkValue

    fun asBoolean(): Boolean = when (this) {
        is True -> true
        is False -> false
        is Number -> value != 0
        is Boolean -> value
        is String -> value.isNotEmpty()
        is Nil -> false
    }.let { Boolean(it) }

    fun asNumber(): Number = when (this) {
        is True -> Number(1)
        is False -> Number(0)
        is Number -> this
        is Boolean -> Number(if (value) 1 else 0)
        is String -> Number(value.length)
        is Nil -> Number(0)
    }

    fun asString(): String = when (this) {
        is True -> String("True")
        is False -> String("False")
        is Number -> String(value.toString())
        is Boolean -> String(value.toString())
        is String -> this
        is Nil -> String("Nil")
    }

    operator fun plus(other: BorkValue): BorkValue = when (this) {
        is Number -> Number(this.value + other.asNumber().value)
        is String -> String(this.value + other.asString().value)
        else -> throw RuntimeException("Cannot add $this and $other")
    }

    operator fun minus(other: BorkValue): BorkValue = when (this) {
        is Number -> Number(this.value - other.asNumber().value)
        else -> throw RuntimeException("Cannot subtract $other from $this")
    }

    operator fun times(other: BorkValue): BorkValue = when (this) {
        is Number -> when (other) {
            is Number -> Number(this.value * other.value)
            is String -> String(other.value.repeat(this.value))
            is Boolean -> Number(this.value * other.asNumber().value)
            else -> throw RuntimeException("Cannot multiply $this and $other")
        }
        is String -> when (other) {
            is Number -> String(this.value.repeat(other.value))
            else -> throw RuntimeException("Cannot multiply $this and $other")
        }
        else -> throw RuntimeException("Cannot multiply $this and $other")
    }

    operator fun div(other: BorkValue): BorkValue = when (this) {
        is Number -> Number(this.value / other.asNumber().value)
        else -> throw RuntimeException("Cannot divide $this by $other")
    }

    operator fun rem(other: BorkValue): BorkValue = when (this) {
        is Number -> Number(this.value % other.asNumber().value)
        else -> throw RuntimeException("Cannot modulo $this by $other")
    }
    operator fun unaryMinus(): BorkValue = when (this) {
        is Number -> Number(-this.value)
        else -> throw RuntimeException("Cannot negate $this")
    }

    operator fun not(): BorkValue = this.asBoolean()

    infix fun and(other: BorkValue): BorkValue = Boolean(this.asBoolean().value && other.asBoolean().value)

    infix fun or(other: BorkValue): BorkValue = Boolean(this.asBoolean().value || other.asBoolean().value)

    fun isEqual(other: BorkValue): BorkValue = Boolean(this == other)

    operator fun compareTo(other: BorkValue): Int = when (this) {
        is Number -> this.value.compareTo(other.asNumber().value)
        is String -> this.value.compareTo(other.asString().value)
        else -> throw RuntimeException("Cannot compare $this and $other")
    }
}