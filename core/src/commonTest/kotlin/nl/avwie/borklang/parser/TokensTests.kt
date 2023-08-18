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
        assertEquals("123", matches[0].text)
        assertEquals(" ", matches[1].text)
        assertEquals(true, matches[1].type.ignored)
        assertEquals("-456", matches[2].text)
    }
}