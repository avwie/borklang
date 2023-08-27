package nl.avwie.borklang.parser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AST {

    fun asProgram(): Program = when (this) {
        is Program -> this
        is Statement -> Program(listOf(this))
    }

    @Serializable
    @SerialName("statement")
    sealed interface Statement : AST

    @Serializable
    @SerialName("expression")
    sealed interface Expression : Statement

    @Serializable
    @SerialName("program")
    data class Program(val statements: List<Statement>): AST

    @Serializable
    @SerialName("assignment")
    data class Assignment(val identifier: Identifier, val expression: Expression): Statement

    @Serializable
    @SerialName("declaration")
    sealed interface Declaration : Statement {
        @Serializable
        @SerialName("variable")
        data class Variable(val identifier: Identifier, val expression: Expression): Declaration
        @Serializable
        @SerialName("constant")
        data class Constant(val identifier: Identifier, val expression: Expression): Declaration

        @Serializable
        @SerialName("function")
        data class Function(val identifier: Identifier, val parameters: List<Identifier>, val body: Block): Declaration
    }

    @Serializable
    @SerialName("control")
    sealed interface Control : Expression {
        @Serializable
        @SerialName("if")
        data class If(val condition: Expression, val thenBlock: Expression, val elseBlock: Expression?): Control
        @Serializable
        @SerialName("return")
        data class Return(val expression: Expression): Control
    }

    @Serializable
    @SerialName("Nil")
    data object Nil : Expression

    @Serializable
    @SerialName("block")
    data class Block(val statements: List<Statement>): Expression
    @Serializable
    @SerialName("constant")
    sealed interface Constant : Expression {
        @Serializable
        @SerialName("boolean")
        data class Boolean(val value: kotlin.Boolean): Constant
        @Serializable
        @SerialName("number")
        data class Number(val value: Int): Constant
        @Serializable
        @SerialName("string")
        data class String(val value: kotlin.String): Constant
    }

    @Serializable
    @SerialName("unary")
    data class UnaryOperation(val operator: String, val expression: Expression): Expression

    @Serializable
    @SerialName("binary")
    data class BinaryOperation(val left: Expression, val operator: String, val right: Expression): Expression

    @Serializable
    @SerialName("identifier")
    data class Identifier(val name: String): Expression
    @Serializable
    @SerialName("functionCall")
    data class FunctionCall(val identifier: Identifier, val arguments: List<Expression>): Expression
}
