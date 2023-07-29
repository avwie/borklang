package nl.avwie.borklang.interpreter.treewalking

import nl.avwie.borklang.ast.Expression
import nl.avwie.borklang.parser.Parser
import nl.avwie.borklang.parser.parse
import nl.avwie.borklang.samples.SET_AND_GET
import kotlin.test.Test

class TreeWalkingInterpreterTests {

    private fun parse(input: String): Expression {
        return Parser.instance().parse(input)
    }

    @Test
    fun setAndGet() {
        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.evaluate(parse(SET_AND_GET))
    }
}