package nl.avwie.borklang.interpreter.treewalking

import nl.avwie.borklang.ast.Expression
import nl.avwie.borklang.parser.Parser
import nl.avwie.borklang.parser.parse
import nl.avwie.borklang.samples.CONDITIONAL
import nl.avwie.borklang.samples.SET_AND_GET
import kotlin.test.Test
import kotlin.test.assertEquals

class TreeWalkingInterpreterTests {

    private fun parse(input: String): Expression {
        return Parser.instance().parse(input)
    }

    @Test
    fun setAndGet() {
        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.evaluate(parse(SET_AND_GET))
        assertEquals(4, result)
    }

    @Test
    fun conditional() {
        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.evaluate(parse(CONDITIONAL))
        assertEquals(1, result)
    }
}