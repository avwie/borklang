package nl.avwie.borklang.tokens

sealed interface Token {

    sealed interface Bracket : Token {
        data object Open : Bracket
        data object Close : Bracket
    }


    sealed interface Operator : Token {
        sealed interface Unary : Operator
        sealed interface Binary : Operator

        data object Plus : Operator, Binary
        data object Minus : Operator, Binary
        data object Multiply : Operator, Binary
        data object Divide : Operator, Binary
        data object Modulo : Operator, Binary
        data object Power : Operator, Binary
        data object DoubleEquals : Operator, Binary
        data object NotEquals : Operator, Binary
        data object GreaterThan : Operator, Binary
        data object GreaterThanOrEqual : Operator, Binary
        data object LessThan : Operator, Binary
        data object LessThanOrEqual : Operator, Binary
        data object And : Operator, Binary
        data object Or : Operator, Binary
        data object Not : Operator, Unary
    }

    sealed interface Literal : Token {
        data class Integer(val value: kotlin.Int) : Literal
        data class Float(val value: kotlin.Double) : Literal
        data class String(val value: kotlin.String) : Literal
        data class Boolean(val value: kotlin.Boolean) : Literal
    }

    sealed interface Keyword : Token {
        data object If : Keyword
        data object While : Keyword
        data object Fn : Keyword
        data object Var : Keyword
        data object Const : Keyword
        data object Set : Keyword
    }

    data class Identifier(val name: kotlin.String) : Token

    data class Comment(val text: String) : Token

    data object EOF : Token

}