package nl.avwie.borklang.parser

import nl.avwie.borklang.ast.Expression
import nl.avwie.borklang.samples.*
import nl.avwie.borklang.tokens.Token
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserImplTests {

    private val parser = Parser.instance()

    @Test
    fun empty() {
        assertEquals(Expression.Nil, parser.parse(EMPTY))
    }

    @Test
    fun literal() {
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
            parser.parse(LITERAL)
        )
    }

    @Test
    fun variableDeclaration() {
        assertEquals(
            Expression.Declaration.Variable("x", Expression.Literal(Token.Literal.Integer(1))),
            parser.parse(VARIABLE_DECLARATION)
        )
    }

    @Test
    fun constantDeclaration() {
        assertEquals(
            Expression.Declaration.Constant("x", Expression.Literal(Token.Literal.Integer(1))),
            parser.parse(CONSTANT_DECLARATION)
        )
    }

    @Test
    fun functionDeclaration() {
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
            parser.parse(FUNCTION_DECLARATION)
        )
    }

    @Test
    fun simpleProgram() {
        assertEquals(
            Expression.Block(
                listOf(
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
            parser.parse(SIMPLE_PROGRAM)
        )
    }
}