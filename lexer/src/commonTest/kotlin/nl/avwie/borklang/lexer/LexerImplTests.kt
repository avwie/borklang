package nl.avwie.borklang.lexer

import kotlin.test.Test
import kotlin.test.assertEquals

class LexerImplTests {

    @Test
    fun empty() {
        val lexer = Lexer.instance("")
        assertEquals(
            listOf(
                Token.EOF
            ),
            lexer.getTokens().toList()
        )
    }

    @Test
    fun brackets() {
        val lexer = Lexer.instance("[]")
        assertEquals(
            listOf(
                Token.Bracket.Open,
                Token.Bracket.Close,
                Token.EOF
            ),
            lexer.getTokens().toList()
        )
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
                Token.Operator.Not,
                Token.EOF
            ),
            lexer.getTokens().toList()
        )
    }

    @Test
    fun literals() {
        val lexer = Lexer.instance("123 123.456 \"abc\" true false")
        assertEquals(
            listOf(
                Token.Literal.Integer(123),
                Token.Literal.Float(123.456),
                Token.Literal.String("abc"),
                Token.Literal.Boolean(true),
                Token.Literal.Boolean(false),
                Token.EOF
            ),
            lexer.getTokens().toList()
        )
    }

    @Test
    fun keywords() {
        val lexer = Lexer.instance("if def var const")
        assertEquals(
            listOf(
                Token.Keyword.If,
                Token.Keyword.Def,
                Token.Keyword.Var,
                Token.Keyword.Const,
                Token.EOF
            ),
            lexer.getTokens().toList()
        )
    }

    @Test
    fun identifier() {
        val lexer = Lexer.instance("[abc]")
        assertEquals(
            listOf(
                Token.Bracket.Open,
                Token.Identifier("abc"),
                Token.Bracket.Close,
                Token.EOF
            ),
            lexer.getTokens().toList()
        )
    }

    @Test
    fun whiteSpace() {
        val lexer = Lexer.instance(" \t\n \"abc\" \t\t\n")
        assertEquals(
            listOf(
                Token.Literal.String("abc"),
                Token.EOF
            ),
            lexer.getTokens().toList()
        )
    }

    @Test
    fun comments() {
        val lexer = Lexer.instance("# This is my file \n 123 # abc\n456")
        assertEquals(
            listOf(
                Token.Comment(" This is my file "),
                Token.Literal.Integer(123),
                Token.Comment(" abc"),
                Token.Literal.Integer(456),
                Token.EOF
            ),
            lexer.getTokens().toList()
        )
    }
}