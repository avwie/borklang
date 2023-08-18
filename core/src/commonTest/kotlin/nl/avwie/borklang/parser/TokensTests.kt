package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import kotlin.test.Test
import kotlin.test.assertEquals

class TokensTests {

    private val tokenizer = DefaultTokenizer(Tokens.asList())

    @Test
    fun numbers() {
        val matches = tokenizer.tokenize("123 -456").toList()
        assertEquals(3, matches.size)
        assertEquals(Tokens.number, matches[0].type)
        assertEquals("123", matches[0].text)
        assertEquals(Tokens.whitespace, matches[1].type)
        assertEquals(" ", matches[1].text)
        assertEquals(true, matches[1].type.ignored)
        assertEquals(Tokens.number, matches[2].type)
        assertEquals("-456", matches[2].text)
    }

    @Test
    fun strings() {
        val matches = tokenizer.tokenize("\"hello world\"").toList()
        assertEquals(1, matches.size)
        assertEquals(Tokens.string, matches[0].type)
        assertEquals("\"hello world\"", matches[0].text)
    }

    @Test
    fun identifiers() {
        val matches = tokenizer.tokenize("hello_world").toList()
        assertEquals(1, matches.size)
        assertEquals(Tokens.identifier, matches[0].type)
        assertEquals("hello_world", matches[0].text)
    }
}