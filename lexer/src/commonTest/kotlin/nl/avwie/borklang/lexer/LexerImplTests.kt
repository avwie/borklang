package nl.avwie.borklang.lexer

import nl.avwie.borklang.tokens.Token
import kotlin.test.Test
import kotlin.test.assertEquals

class LexerImplTests {

    private val lexer = Lexer.instance()

    @Test
    fun empty() {
        assertEquals(
            listOf(
                Token.EOF
            ),
            lexer.tokenize("").toList()
        )
    }

    @Test
    fun brackets() {
        assertEquals(
            listOf(
                Token.Bracket.Open,
                Token.Bracket.Close,
                Token.EOF
            ),
            lexer.tokenize("[]").toList()
        )
    }

    @Test
    fun operators() {
        assertEquals(
            listOf(
                Token.Operator.Plus,
                Token.Operator.Minus,
                Token.Operator.Multiply,
                Token.Operator.Divide,
                Token.Operator.Modulo,
                Token.Operator.Power,
                Token.Operator.DoubleEquals,
                Token.Operator.NotEquals,
                Token.Operator.GreaterThan,
                Token.Operator.LessThan,
                Token.Operator.And,
                Token.Operator.Or,
                Token.Operator.Not,
                Token.EOF
            ),
            lexer.tokenize("+-*/%^==!=><&&||!").toList()
        )
    }

    @Test
    fun literals() {
        assertEquals(
            listOf(
                Token.Literal.Integer(123),
                Token.Literal.Float(123.456),
                Token.Literal.String("abc"),
                Token.Literal.Boolean(true),
                Token.Literal.Boolean(false),
                Token.EOF
            ),
            lexer.tokenize("123 123.456 \"abc\" true false").toList()
        )
    }

    @Test
    fun keywords() {
        assertEquals(
            listOf(
                Token.Keyword.If,
                Token.Keyword.Fn,
                Token.Keyword.Var,
                Token.Keyword.Const,
                Token.EOF
            ),
            lexer.tokenize("if fn var const").toList()
        )
    }

    @Test
    fun identifier() {
        assertEquals(
            listOf(
                Token.Bracket.Open,
                Token.Identifier("abc"),
                Token.Bracket.Close,
                Token.EOF
            ),
            lexer.tokenize("[abc]").toList()
        )
    }

    @Test
    fun whiteSpace() {
        assertEquals(
            listOf(
                Token.Literal.String("abc"),
                Token.EOF
            ),
            lexer.tokenize(" \t\n \"abc\" \t\t\n").toList()
        )
    }

    @Test
    fun comments() {
        assertEquals(
            listOf(
                Token.Comment(" This is my file "),
                Token.Literal.Integer(123),
                Token.Comment(" abc"),
                Token.Literal.Integer(456),
                Token.EOF
            ),
            lexer.tokenize("# This is my file \n 123 # abc\n456").toList()
        )
    }
}