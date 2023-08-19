package nl.avwie.borklang.interpreter

import kotlin.test.Test
import kotlin.test.assertEquals

class TreeWalkingInterpreterTests {

    @Test
    fun simpleProgram() {
        val program = """
            let x = 1
            let y = 2
            x + y
        """.trimIndent()

        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.interpret(program)
        assertEquals(3, result)
    }

    @Test
    fun fibonacci() {
        val program = """
            fn fib(n) {
                if (n < 2) {
                    return n
                } else {
                    return fib(n - 1) + fib(n - 2)
                }
            }
            
            fib(10)
        """.trimIndent()

        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.interpret(program)
        assertEquals(55, result)
    }

    @Test
    fun earlyBreak() {
        val program = """
            {
                456
                return 789
                101112
            }
        """.trimIndent()

        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.interpret(program)
        assertEquals(789, result)
    }
}