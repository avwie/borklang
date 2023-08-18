package nl.avwie.borklang.parser

sealed interface AST {
    sealed interface Statement : AST

    data class Program(val statements: List<Statement>): AST

    data class Block(val statements: List<Statement>): Statement
    data class Assignment(val identifier: Identifier, val expression: Expression): Statement

    sealed interface Declaration : Statement {
        data class Variable(val identifier: Identifier, val expression: Expression): Declaration
        data class Constant(val identifier: Identifier, val expression: Expression): Declaration
    }

    sealed interface Expression : Statement
    data object Nil : Expression
    sealed interface Constant : Expression {
        data class Number(val value: Int): Constant
        data class String(val value: kotlin.String): Constant
    }
    data class Identifier(val name: String): Expression
}
