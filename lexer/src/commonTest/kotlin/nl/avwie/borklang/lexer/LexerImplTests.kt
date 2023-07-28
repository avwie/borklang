package nl.avwie.borklang.lexer

import kotlin.test.Test
import kotlin.test.assertEquals

class LexerImplTests {

    @Test
    fun empty() {
        val lexer = Lexer.instance("")
        assertEquals(0, lexer.getTokens().count())
    }

    @Test
    fun brackets() {
        val lexer = Lexer.instance("[]")
        assertEquals(2, lexer.getTokens().count())
    }

    @Test
    fun operators() {
        val lexer = Lexer.instance("+-*/%^===!=><&&||!")
        assertEquals(
            listOf(
                Token.Operator.Plus,
                Token.Operator.Minus,
                Token.Operator.Multiply,
                Token.Operator.Divide,
                Token.Operator.Modulo,
                Token.Operator.Power,
                Token.Operator.DoubleEquals,
                Token.Operator.Equals,
                Token.Operator.NotEquals,
                Token.Operator.GreaterThan,
                Token.Operator.LessThan,
                Token.Operator.And,
                Token.Operator.Or,
                Token.Operator.Not
            ),
            lexer.getTokens().toList()
        )
    }

    @Test
    fun operators2() {
        val lexer = Lexer.instance("==>=<=")
        assertEquals(
            listOf(Token.Operator.DoubleEquals, Token.Operator.GreaterThanOrEqual, Token.Operator.LessThanOrEqual),
            lexer.getTokens().toList()
        )
    }
}