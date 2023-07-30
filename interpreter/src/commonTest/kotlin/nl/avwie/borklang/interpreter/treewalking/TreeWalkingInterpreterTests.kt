package nl.avwie.borklang.interpreter.treewalking

import nl.avwie.borklang.ast.Expression
import nl.avwie.borklang.parser.Parser
import nl.avwie.borklang.parser.parse
import nl.avwie.borklang.samples.*
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

    @Test
    fun functionCall() {
        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.evaluate(parse(FUNCTION_CALL))
        assertEquals(3.0, result)
    }

    @Test
    fun scopes() {
        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.evaluate(parse(SCOPES))
        assertEquals(5.0, result)
    }

    @Test
    fun whileLoop() {
        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.evaluate(parse(WHILE))
        assertEquals(10.0, result)
    }

    @Test
    fun nativeFunction() {
        val output = StringBuilder()
        val scope = Scope.default(
            stdOut = { output.appendLine(it) }
        )
        val interpreter = TreeWalkingInterpreter(scope)
        interpreter.evaluate(parse(SIMPLE_PROGRAM))

        assertEquals(
            """
                0
                1.0
                2.0
                3.0
                4.0
                5.0
                6.0
                7.0
                8.0
                9.0
                
            """.trimIndent(),
            output.toString()
        )
    }
}