package nl.avwie.borklang.interpreter

import nl.avwie.borklang.BorkValue
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
        assertEquals(BorkValue.Number(3), result)
    }

    @Test
    fun fibonacci() {
        val program = """
            fn fib = (n) -> {
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
        assertEquals(BorkValue.Number(55), result)
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
        assertEquals(BorkValue.Number(789), result)
    }

    @Test
    fun earlyBreak2() {
        val program = """
            fn foo = (n) -> {
                if (n < 2) {
                    return n
                }
                return 123
                "hello"
            }
            
            foo(1)            
        """.trimIndent()

        val interpreter = TreeWalkingInterpreter()
        val result = interpreter.interpret(program)
        assertEquals(BorkValue.Number(1), result)
    }
}