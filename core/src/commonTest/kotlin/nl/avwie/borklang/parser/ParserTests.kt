package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.parseToEnd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParserTests {
    @Test
    fun numbers() {
        val positive = Grammar.parseToEnd("123")
        assertTrue { positive is AST.Constant }
        assertEquals(123, (positive as AST.Constant.Number).value)

        val negative = Grammar.parseToEnd("-123")
        assertTrue { negative is AST.Constant }
        assertEquals(-123, (negative as AST.Constant.Number).value)
    }

    @Test
    fun strings() {
        val string = Grammar.parseToEnd("\"hello world\"")
        assertTrue { string is AST.Constant }
        assertEquals("hello world", (string as AST.Constant.String).value)
    }

    @Test
    fun identifiers() {
        val identifier = Grammar.parseToEnd("hello_world")
        assertTrue { identifier is AST.Identifier }
        assertEquals("hello_world", (identifier as AST.Identifier).name)
    }
}