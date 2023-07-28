package nl.avwie.borklang.parser

import kotlin.test.Test

class ParserImplTests {

    @Test
    fun empty() {
        val code = """
            [
                [var x 2]
                [def multiply [a b] [* a b]]
                
                [multiply x 2]
            ]
        """.trimIndent()

        val parser = Parser.instance(code)
        val result = parser.parse()
    }
}