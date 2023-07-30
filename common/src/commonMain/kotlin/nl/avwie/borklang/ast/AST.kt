package nl.avwie.borklang.ast

import nl.avwie.borklang.tokens.Token

sealed interface Expression {

    sealed interface Simple : Expression
    sealed interface Complex : Expression

    data object Nil : Expression, Simple

    data class Block(val expressions: List<Expression>) : Expression, Complex

    data class Identifier(val identifier: Token.Identifier) : Expression, Simple

    data class Literal(val literal: Token.Literal) : Expression, Simple

    data class Assignment(val identifier: Token.Identifier, val value: Simple) : Expression, Complex

    data class Call(val identifier: Token.Identifier, val arguments: List<Simple>) : Expression, Simple

    sealed interface Declaration : Expression, Complex {
        data class Variable(val name: String, val value: Simple) : Declaration
        data class Constant(val name: String, val value: Literal) : Declaration
        data class Function(val name: String, val parameters: List<Token.Identifier>, val body: Expression) : Declaration
    }

    sealed interface Control : Expression, Complex {
        data class Conditional(val condition: Simple, val then: Expression, val otherwise: Expression?) : Control
        data class Loop(val condition: Simple, val body: Expression) : Control
    }

    sealed interface Operator : Expression, Simple {
        data class Unary(val operator: Token.Operator.Unary, val operand: Simple) : Operator
        data class Binary(val operator: Token.Operator.Binary, val left: Simple, val right: Simple) : Operator
    }
}