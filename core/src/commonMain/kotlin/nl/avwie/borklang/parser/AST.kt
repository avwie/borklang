package nl.avwie.borklang.parser

sealed interface AST {
    sealed interface Statement : AST
    data class Program(val statements: List<Statement>): AST
    sealed interface Expression : Statement
    data object Nil : Expression
    sealed interface Constant : Expression {
        data class Number(val value: Int): Constant
        data class String(val value: kotlin.String): Constant
    }
    data class Identifier(val name: String): Expression

    data class Assignment(val identifier: Identifier, val expression: Expression): Statement
}
