package nl.avwie.borklang.interpreter.treewalking

import nl.avwie.borklang.ast.Expression
import nl.avwie.borklang.interpreter.Interpreter
import nl.avwie.borklang.tokens.Token

class TreeWalkingInterpreter(
    private val scope: Scope = Scope()
) : Interpreter {
    override fun evaluate(expression: Expression): Any? = when (expression) {
        is Expression.Assignment -> TODO()
        is Expression.Block -> block(expression)
        is Expression.Call -> TODO()
        is Expression.Control.Conditional -> TODO()
        is Expression.Control.Loop -> TODO()
        is Expression.Declaration.Constant -> TODO()
        is Expression.Declaration.Function -> TODO()
        is Expression.Declaration.Variable -> declaration(expression)
        is Expression.Identifier -> identifier(expression)
        is Expression.Literal -> literal(expression)
        Expression.Nil -> TODO()
        is Expression.Operator.Binary -> TODO()
        is Expression.Operator.Unary -> TODO()
    }

    private fun declaration(declaration: Expression.Declaration): Any? = when (declaration) {
        is Expression.Declaration.Constant -> TODO()
        is Expression.Declaration.Function -> TODO()
        is Expression.Declaration.Variable -> {
            val value = evaluate(declaration.value)
            scope.set(declaration.name, value)
            value
        }
    }

    private fun block(block: Expression.Block): Any? {
        var result: Any? = null
        for (expression in block.expressions) {
            result = evaluate(expression)
        }
        return result
    }

    private fun identifier(identifier: Expression.Identifier): Any? = scope.get(identifier.identifier.value)

    private fun literal(literal: Expression.Literal): Any = when (literal.literal) {
        is Token.Literal.Boolean -> (literal.literal as Token.Literal.Boolean).value
        is Token.Literal.Float -> (literal.literal as Token.Literal.Float).value
        is Token.Literal.Integer -> (literal.literal as Token.Literal.Integer).value
        is Token.Literal.String -> (literal.literal as Token.Literal.String).value
    }
}