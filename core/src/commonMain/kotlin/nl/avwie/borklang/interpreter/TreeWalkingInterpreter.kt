package nl.avwie.borklang.interpreter

import nl.avwie.borklang.parser.AST
import nl.avwie.borklang.parser.Tokens

class TreeWalkingInterpreter(
    scope: Scope = Scope()
) : Interpreter {

    private val scopes = ArrayDeque<Scope>().also { it.add(scope) }
    private val scope get() = scopes.last()
    override fun evaluate(ast: AST): Any? = when (ast) {
        is AST.Program -> program(ast)
        is AST.Statement -> statement(ast)
    }

    private fun program(program: AST.Program): Any? {
        var result: Any? = Unit
        program.statements.forEach { statement ->
            result = statement(statement)
        }
        return result
    }

    private fun statement(statement: AST.Statement): Any? = when (statement) {
        is AST.Assignment -> assignment(statement)
        is AST.Declaration -> declaration(statement)
        is AST.Expression -> expression(statement)
    }

    private fun assignment(assignment: AST.Assignment): Any? {
        val value = expression(assignment.expression)
        scope.setVariable(assignment.identifier.name, value)
        return value
    }

    private fun declaration(declaration: AST.Declaration): Any? = when (declaration) {
        is AST.Declaration.Variable -> declaration.variable()
        is AST.Declaration.Constant -> declaration.constant()
        is AST.Declaration.Function -> declaration.function()
    }

    private fun AST.Declaration.Variable.variable(): Any? {
        val value = expression(expression)
        scope.declareVariable(identifier.name, value)
        return value
    }

    private fun AST.Declaration.Constant.constant(): Any? {
        val value = expression(expression)
        scope.declareConstant(identifier.name, value)
        return value
    }

    private fun AST.Declaration.Function.function(): Any? {
        scope.declareFunction(identifier.name, this)
        return Unit
    }

    private fun expression(expression: AST.Expression): Any? = when (expression) {
        is AST.Control -> control(expression)
        is AST.Constant -> constant(expression)
        is AST.Block -> block(expression)
        is AST.UnaryOperation -> unaryOperation(expression)
        is AST.BinaryOperation -> binaryOperation(expression)
        is AST.Identifier -> identifier(expression)
        is AST.FunctionCall -> functionCall(expression)
        is AST.Nil -> null
    }

    private fun control(control: AST.Control): Any? = when (control) {
        is AST.Control.If -> ifStatement(control)
        is AST.Control.Return -> returnStatement(control)
    }

    private fun ifStatement(ifStatement: AST.Control.If): Any? {
        val condition = expression(ifStatement.condition)
        return if (condition == true) {
            expression(ifStatement.thenBlock)
        } else {
            expression(ifStatement.elseBlock ?: AST.Nil)
        }
    }

    private fun returnStatement(returnStatement: AST.Control.Return): Any? {
        return expression(returnStatement.expression)
    }

    private fun constant(constant: AST.Constant): Any? = when (constant) {
        is AST.Constant.Boolean -> constant.value
        is AST.Constant.Number -> constant.value
        is AST.Constant.String -> constant.value
    }

    private fun block(block: AST.Block): Any? {
       return  scoped {
           var result: Any? = Unit
           block.statements.forEach { statement ->
               result = statement(statement)
               if (statement is AST.Control.Return) {
                   return@scoped result
               }
           }
           result
        }
    }

    private fun unaryOperation(unaryOperation: AST.UnaryOperation): Any? {
        val value = expression(unaryOperation.expression)
        return when (unaryOperation.operator) {
            Tokens.minus -> -(value as Int)
            Tokens.not -> !(value as Boolean)
            else -> throw IllegalStateException("Unknown unary operator: ${unaryOperation.operator}")
        }
    }

    private fun binaryOperation(binaryOperation: AST.BinaryOperation): Any? {
        val left = expression(binaryOperation.left)
        val right = expression(binaryOperation.right)
        return when (binaryOperation.operator) {
            Tokens.plus -> (left as Int) + (right as Int)
            Tokens.minus -> (left as Int) - (right as Int)
            Tokens.multiply -> (left as Int) * (right as Int)
            Tokens.divide -> (left as Int) / (right as Int)
            Tokens.modulo -> (left as Int) % (right as Int)
            Tokens.and -> (left as Boolean) && (right as Boolean)
            Tokens.or -> (left as Boolean) || (right as Boolean)
            Tokens.doubleEqual -> left == right
            Tokens.notEqual -> left != right
            Tokens.lessThan -> (left as Int) < (right as Int)
            Tokens.lessThanOrEqual -> (left as Int) <= (right as Int)
            Tokens.greaterThan -> (left as Int) > (right as Int)
            Tokens.greaterThanOrEqual -> (left as Int) >= (right as Int)
            else -> throw IllegalStateException("Unknown binary operator: ${binaryOperation.operator}")
        }
    }

    private fun identifier(identifier: AST.Identifier): Any? {
        return scope.getVariable(identifier.name)
    }

    private fun functionCall(functionCall: AST.FunctionCall): Any? {
        val function = scope.getFunction(functionCall.identifier.name)
        val arguments = functionCall.arguments.map { expression(it) }
        return when (function) {
            is Scope.Function.Native -> function.block.invoke(scope)
            is Scope.Function.UserDefined -> {
                scoped {
                    function.parameters.zip(arguments).forEach { (parameter, argument) ->
                        scope.declareVariable(parameter, argument)
                    }
                    val result = expression(function.body)
                    result
                }
            }

            null -> throw IllegalStateException("Function ${functionCall.identifier.name} is not declared")
        }
    }

    private fun scoped(block: () -> Any?): Any? {
        scopes.add(scope.child())
        val result = block()
        scopes.removeLast()
        return result
    }
}