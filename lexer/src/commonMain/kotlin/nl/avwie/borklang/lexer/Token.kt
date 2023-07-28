package nl.avwie.borklang.lexer

sealed interface Token {

    sealed interface Bracket : Token {
        data object Open : Bracket
        data object Close : Bracket
    }

    sealed interface Operator : Token {
        data object Plus : Operator
        data object Minus : Operator
        data object Multiply : Operator
        data object Divide : Operator
        data object Modulo : Operator
        data object Power : Operator
        data object Equals : Operator
        data object DoubleEquals : Operator
        data object NotEquals : Operator
        data object GreaterThan : Operator
        data object GreaterThanOrEqual : Operator
        data object LessThan : Operator
        data object LessThanOrEqual : Operator
        data object And : Operator
        data object Or : Operator
        data object Not : Operator
    }

    sealed interface Literal : Token {
        data class Integer(val value: kotlin.Int) : Literal
        data class Float(val value: kotlin.Double) : Literal
        data class String(val value: kotlin.String) : Literal
        data class Boolean(val value: kotlin.Boolean) : Literal
        data class Identifier(val value: kotlin.String) : Literal
    }

    sealed interface Keyword : Token {
        data object If : Keyword
        data object Def : Keyword
        data object Var : Keyword
        data object Const : Keyword
    }

    data class Comment(val value: String) : Literal

    data object EOF : Token

}