package nl.avwie.borklang.interpreter.treewalking

import nl.avwie.borklang.ast.Expression
import nl.avwie.borklang.interpreter.Interpreter
import nl.avwie.borklang.tokens.Token
import kotlin.math.pow

class TreeWalkingInterpreter(
    scope: Scope = Scope()
) : Interpreter {

    private val scopes = ArrayDeque<Scope>().also { it.add(scope) }
    private val scope get() = scopes.last()

    override fun evaluate(expression: Expression): Any = when (expression) {
        is Expression.Assignment -> assignment(expression)
        is Expression.Block -> block(expression)
        is Expression.Call -> call(expression)
        is Expression.Control.Conditional -> conditional(expression)
        is Expression.Control.Loop -> TODO()
        is Expression.Declaration -> declaration(expression)
        is Expression.Identifier -> identifier(expression)
        is Expression.Literal -> literal(expression)
        Expression.Nil -> Unit
        is Expression.Operator.Binary -> binary(expression)
        is Expression.Operator.Unary -> unary(expression)
    }

    private fun assignment(assignment: Expression.Assignment): Any {
        val value = evaluate(assignment.value)
        when {
            scope.hasConstant(assignment.identifier.name) -> throw IllegalStateException("Cannot assign to constant ${assignment.identifier.name}")
            scope.hasVariable(assignment.identifier.name) -> scope.setVariable(assignment.identifier.name, value)
            else -> throw IllegalStateException("Identifier ${assignment.identifier.name} is not declared")
        }
        return value
    }

    private fun conditional(conditional: Expression.Control.Conditional): Any {
        val condition = evaluate(conditional.condition)
        if (condition !is Boolean) throw IllegalStateException("Condition must be a boolean")
        return if (condition) evaluate(conditional.then) else evaluate(conditional.otherwise ?: Expression.Nil)
    }

    private fun declaration(declaration: Expression.Declaration): Any = when (declaration) {
        is Expression.Declaration.Constant -> evaluate(declaration.value).also {
            scope.declareConstant(declaration.name, it)
        }
        is Expression.Declaration.Variable -> evaluate(declaration.value).also {
            scope.declareVariable(declaration.name, it)
        }
        is Expression.Declaration.Function -> scope.declareFunction(declaration.name, declaration)
    }

    private fun block(block: Expression.Block): Any {
        var result: Any = Unit
        scoped {
            for (expression in block.expressions) {
                result = evaluate(expression)
            }
        }
        return result
    }

    private fun identifier(identifier: Expression.Identifier): Any {
        val constant = scope.getConstant(identifier.identifier.name)
        if (constant != null) return constant

        val variable = scope.getVariable(identifier.identifier.name)
        if (variable != null) return variable

        throw IllegalStateException("Identifier ${identifier.identifier.name} is not declared")
    }

    private fun literal(literal: Expression.Literal): Any = when (literal.literal) {
        is Token.Literal.Boolean -> (literal.literal as Token.Literal.Boolean).value
        is Token.Literal.Float -> (literal.literal as Token.Literal.Float).value
        is Token.Literal.Integer -> (literal.literal as Token.Literal.Integer).value
        is Token.Literal.String -> (literal.literal as Token.Literal.String).value
    }

    private fun binary(binary: Expression.Operator.Binary): Any = when (binary.operator) {
        Token.Operator.And -> evaluate(binary.left).asBoolean() && evaluate(binary.right).asBoolean()
        Token.Operator.Divide -> evaluate(binary.left).asNumber().toDouble() / evaluate(binary.right).asNumber().toDouble()
        Token.Operator.DoubleEquals -> evaluate(binary.left) == evaluate(binary.right)
        Token.Operator.GreaterThan -> evaluate(binary.left).asNumber().toDouble() > evaluate(binary.right).asNumber().toDouble()
        Token.Operator.GreaterThanOrEqual -> evaluate(binary.left).asNumber().toDouble() >= evaluate(binary.right).asNumber().toDouble()
        Token.Operator.LessThan -> evaluate(binary.left).asNumber().toDouble() < evaluate(binary.right).asNumber().toDouble()
        Token.Operator.LessThanOrEqual -> evaluate(binary.left).asNumber().toDouble() <= evaluate(binary.right).asNumber().toDouble()
        Token.Operator.Minus -> evaluate(binary.left).asNumber().toDouble() - evaluate(binary.right).asNumber().toDouble()
        Token.Operator.Modulo -> evaluate(binary.left).asNumber().toDouble() % evaluate(binary.right).asNumber().toDouble()
        Token.Operator.Multiply -> evaluate(binary.left).asNumber().toDouble() * evaluate(binary.right).asNumber().toDouble()
        Token.Operator.NotEquals -> evaluate(binary.left) != evaluate(binary.right)
        Token.Operator.Or -> evaluate(binary.left).asBoolean() || evaluate(binary.right).asBoolean()
        Token.Operator.Plus -> evaluate(binary.left).asNumber().toDouble() + evaluate(binary.right).asNumber().toDouble()
        Token.Operator.Power -> evaluate(binary.left).asNumber().toDouble().pow(evaluate(binary.right).asNumber().toDouble())
    }

    private fun unary(unary: Expression.Operator.Unary): Any = when (unary.operator) {
        Token.Operator.Not -> !evaluate(unary.operand).asBoolean()
    }

    private fun call(call: Expression.Call): Any {
        val function = scope.getFunction(call.identifier.name)
            ?: throw IllegalStateException("Function ${call.identifier.name} is not declared")

        if (function.parameters.size != call.arguments.size) throw IllegalStateException("Function ${call.identifier.name} expects ${function.parameters.size} arguments, but ${call.arguments.size} were given")

        val result = scoped {
            for ((parameter, argument) in function.parameters.zip(call.arguments)) {
                scope.declareVariable(parameter.name, evaluate(argument))
            }
            evaluate(function.body)
        }

        return result
    }

    private fun Any.asBoolean(): Boolean = when (this) {
        is Boolean -> this
        is Int -> this != 0
        is Float -> this != 0f
        is String -> this.isNotEmpty()
        else -> throw IllegalStateException("Cannot convert $this to boolean")
    }

    private fun Any.asNumber(): Number = when (this) {
        is Boolean -> if (this) 1 else 0
        is Int -> this
        is Float -> this
        is String -> this.toIntOrNull() ?: this.toFloatOrNull() ?: throw IllegalStateException("Cannot convert $this to number")
        else -> throw IllegalStateException("Cannot convert $this to number")
    }

    private fun scoped(block: () -> Any): Any {
        scopes.add(Scope())
        val result = block()
        scopes.removeLast()
        return result
    }
}