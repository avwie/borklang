package nl.avwie.borklang.interpreter

import nl.avwie.borklang.ast.Expression
import nl.avwie.borklang.interpreter.treewalking.TreeWalkingInterpreter

interface Interpreter {
    fun evaluate(expression: Expression): Any?

    companion object {
        fun instance(): Interpreter = TreeWalkingInterpreter()
    }
}