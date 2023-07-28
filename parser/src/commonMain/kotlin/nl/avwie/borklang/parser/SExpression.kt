package nl.avwie.borklang.parser

import nl.avwie.borklang.lexer.Token

sealed interface SExpression {
    data class List(val expressions: kotlin.collections.List<SExpression>) : SExpression
    data class Identifier(val value: kotlin.String) : SExpression
    data class Operator(val operator: Token.Operator) : SExpression
    sealed interface Number : SExpression {
        data class Integer(val value: kotlin.Int) : Number
        data class Float(val value: kotlin.Double) : Number
    }
    data class String(val value: kotlin.String) : SExpression
    data class Boolean(val value: kotlin.Boolean) : SExpression
}