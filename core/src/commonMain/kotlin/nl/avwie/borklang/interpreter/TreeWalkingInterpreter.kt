package nl.avwie.borklang.interpreter

import nl.avwie.borklang.parser.AST
import nl.avwie.borklang.parser.Tokens

class TreeWalkingInterpreter(
    scope: Scope = Scope()
) : Interpreter {

    private val scopes = ArrayDeque<Scope>().also { it.add(scope) }
    private val scope get() = scopes.last()
    override fun evaluate(ast: AST): BorkValue = when (ast) {
        is AST.Program -> program(ast)
        is AST.Statement -> statement(ast)
    }

    private fun program(program: AST.Program): BorkValue {
        var result: BorkValue = BorkValue.Nil
        program.statements.forEach { statement ->
            result = statement(statement)
        }
        return result
    }

    private fun statement(statement: AST.Statement): BorkValue = when (statement) {
        is AST.Assignment -> assignment(statement)
        is AST.Declaration -> declaration(statement)
        is AST.Expression -> expression(statement)
    }

    private fun assignment(assignment: AST.Assignment): BorkValue {
        val value = expression(assignment.expression)
        scope.setVariable(assignment.identifier.name, value)
        return value
    }

    private fun declaration(declaration: AST.Declaration): BorkValue = when (declaration) {
        is AST.Declaration.Variable -> declaration.variable()
        is AST.Declaration.Constant -> declaration.constant()
        is AST.Declaration.Function -> declaration.function()
    }

    private fun AST.Declaration.Variable.variable(): BorkValue {
        val value = expression(expression)
        scope.declareVariable(identifier.name, value)
        return value
    }

    private fun AST.Declaration.Constant.constant(): BorkValue {
        val value = expression(expression)
        scope.declareConstant(identifier.name, value)
        return value
    }

    private fun AST.Declaration.Function.function(): BorkValue {
        scope.declareFunction(identifier.name, this)
        return BorkValue.Nil
    }

    private fun expression(expression: AST.Expression): BorkValue = when (expression) {
        is AST.Control -> control(expression)
        is AST.Constant -> constant(expression)
        is AST.Block -> block(expression)
        is AST.UnaryOperation -> unaryOperation(expression)
        is AST.BinaryOperation -> binaryOperation(expression)
        is AST.Identifier -> identifier(expression)
        is AST.FunctionCall -> functionCall(expression)
        is AST.Nil -> BorkValue.Nil
    }

    private fun control(control: AST.Control): BorkValue = when (control) {
        is AST.Control.If -> ifStatement(control)
        is AST.Control.Return -> returnStatement(control)
    }

    private fun ifStatement(ifStatement: AST.Control.If): BorkValue {
        val condition = expression(ifStatement.condition)
        return if (condition.asBoolean().value) {
            expression(ifStatement.thenBlock)
        } else {
            expression(ifStatement.elseBlock ?: AST.Nil)
        }
    }

    private fun returnStatement(returnStatement: AST.Control.Return): BorkValue {
        return expression(returnStatement.expression)
    }

    private fun constant(constant: AST.Constant): BorkValue = when (constant) {
        is AST.Constant.Boolean -> BorkValue.Boolean(constant.value)
        is AST.Constant.Number -> BorkValue.Number(constant.value)
        is AST.Constant.String -> BorkValue.String(constant.value)
    }

    private fun block(block: AST.Block): BorkValue {
       return  scoped {
           var result: BorkValue = BorkValue.Nil
           block.statements.forEach { statement ->
               result = statement(statement)
               if (statement is AST.Control.Return) {
                   return@scoped result
               }
           }
           result
        }
    }

    private fun unaryOperation(unaryOperation: AST.UnaryOperation): BorkValue {
        val value = expression(unaryOperation.expression)
        return when (unaryOperation.operator) {
            Tokens.minus.name-> -value
            Tokens.not.name -> !value
            else -> throw RuntimeException("Unknown unary operator: ${unaryOperation.operator}")
        }
    }

    private fun binaryOperation(binaryOperation: AST.BinaryOperation): BorkValue {
        val left = expression(binaryOperation.left)
        val right = expression(binaryOperation.right)
        return when (binaryOperation.operator) {
            Tokens.plus.name -> left + right
            Tokens.minus.name -> left - right
            Tokens.multiply.name -> left * right
            Tokens.divide.name -> left  / right
            Tokens.modulo.name -> left % right
            Tokens.and.name -> left and right
            Tokens.or.name -> left or right
            Tokens.doubleEqual.name -> left.isEqual(right)
            Tokens.notEqual.name -> !(left.isEqual(right))
            Tokens.lessThan.name -> BorkValue.Boolean(left < right)
            Tokens.lessThanOrEqual.name -> BorkValue.Boolean(left <= right)
            Tokens.greaterThan.name -> BorkValue.Boolean(left > right)
            Tokens.greaterThanOrEqual.name -> BorkValue.Boolean(left >= right)
            else -> throw IllegalStateException("Unknown binary operator: ${binaryOperation.operator}")
        }
    }

    private fun identifier(identifier: AST.Identifier): BorkValue {
        return scope.resolveValue(identifier.name)
    }

    private fun functionCall(functionCall: AST.FunctionCall): BorkValue {
        val function = scope.resolveFunction(functionCall.identifier.name)
        val arguments = functionCall.arguments.map { expression(it) }
        return scoped {

            function.parameters.zip(arguments).forEach { (parameter, argument) ->
                scope.declareVariable(parameter, argument)
            }

            when (function) {
                is Scope.Function.Native -> function.block.invoke(scope)
                is Scope.Function.UserDefined -> {
                    val result = expression(function.body)
                    result
                }
            }
        }
    }

    private fun scoped(block: () -> BorkValue): BorkValue {
        scopes.add(scope.child())
        val result = block()
        scopes.removeLast()
        return result
    }
}