package nl.avwie.borklang.interpreter

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import nl.avwie.borklang.parser.AST
import nl.avwie.borklang.parser.Grammar

interface Interpreter {
    fun evaluate(ast: AST): Any?

    fun interpret(program: String): Any? {
        val ast = Grammar.parseToEnd(program)
        return evaluate(ast)
    }
}