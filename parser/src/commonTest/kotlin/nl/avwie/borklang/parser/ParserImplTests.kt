package nl.avwie.borklang.parser

import kotlin.test.Test

class ParserImplTests {

    @Test
    fun empty() {
        val parser = Parser.instance("[+ [+ 3 5] [+ 5 6]]")
        val result = parser.parse()
    }
}