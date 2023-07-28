package nl.avwie.borklang.parser

import nl.avwie.borklang.lexer.Token
import nl.avwie.borklang.samples.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserImplTests {

    @Test
    fun empty() {
        val parser = Parser.instance(EMPTY)
        assertEquals(Expression.Nil, parser.parse())
    }

    @Test
    fun literal() {
        val parser = Parser.instance(LITERAL)
        val result = parser.parse()
        assertEquals(
            Expression.Block(
                listOf(
                    Expression.Literal(Token.Literal.Integer(1)),
                    Expression.Literal(Token.Literal.Float(2.0)),
                    Expression.Literal(Token.Literal.String("foo bar")),
                    Expression.Literal(Token.Literal.Boolean(true)),
                    Expression.Literal(Token.Literal.Boolean(false))
                )
            ),
            result
        )
    }

    @Test
    fun variableDeclaration() {
        val parser = Parser.instance(VARIABLE_DECLARATION)
        val result = parser.parse()
        assertEquals(
            Expression.Declaration.Variable("x", Expression.Literal(Token.Literal.Integer(1))),
            result
        )
    }

    @Test
    fun constantDeclaration() {
        val parser = Parser.instance(CONSTANT_DECLARATION)
        val result = parser.parse()
        assertEquals(
            Expression.Declaration.Constant("x", Expression.Literal(Token.Literal.Integer(1))),
            result
        )
    }

    @Test
    fun functionDeclaration() {
        val parser = Parser.instance(FUNCTION_DECLARATION)
        val result = parser.parse()
        assertEquals(
            Expression.Declaration.Function(
                "sum",
                listOf(Token.Identifier("x"), Token.Identifier("y")),
                Expression.Operator.Binary(
                    Token.Operator.Plus,
                    Expression.Identifier(Token.Identifier("x")),
                    Expression.Identifier(Token.Identifier("y"))
                )
            ),
            result
        )
    }

    @Test
    fun simpleProgram() {
        val parser = Parser.instance(SIMPLE_PROGRAM)
        val result = parser.parse()
        assertEquals(
            Expression.Block(
                listOf(
                    Expression.Literal(Token.Comment(" Simple program")),
                    Expression.Declaration.Variable("x", Expression.Literal(Token.Literal.Integer(0))),
                    Expression.Control.Loop(
                        Expression.Operator.Binary(
                            Token.Operator.LessThan,
                            Expression.Identifier(Token.Identifier("x")),
                            Expression.Literal(Token.Literal.Integer(10))
                        ),
                        Expression.Block(
                            listOf(
                                Expression.Call(
                                    Token.Identifier("print"),
                                    listOf(Expression.Identifier(Token.Identifier("x")))
                                ),
                                Expression.Assignment(
                                    Token.Identifier("x"),
                                    Expression.Operator.Binary(
                                        Token.Operator.Plus,
                                        Expression.Identifier(Token.Identifier("x")),
                                        Expression.Literal(Token.Literal.Integer(1))
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }
}