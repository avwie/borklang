package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.parser.Parser

object BaseParsers {
    val number: Parser<AST.Constant.Number> = Tokens.number.use { AST.Constant.Number(text.toInt()) }
    val string: Parser<AST.Constant.String> = Tokens.string.use { AST.Constant.String(text.substring(1, text.length - 1)) }
    val boolean: Parser<AST.Constant.Boolean> = (Tokens.trueToken or Tokens.falseToken).use { AST.Constant.Boolean(text == "True") }
    val constant: Parser<AST.Constant> = number or string or boolean
    val nil: Parser<AST.Nil> = Tokens.nil use { AST.Nil }
    val identifier: Parser<AST.Identifier> = Tokens.identifier.use { AST.Identifier(text) }

    val argumentsList: Parser<List<AST.Expression>> = (
            parser { LanguageParsers.expression } and zeroOrMore(Tokens.comma and parser { LanguageParsers.expression })
            )
        .map { (first, rest) ->
            listOf(first) + rest.map { (_, expression) -> expression }
        }

    val functionCall: Parser<AST.FunctionCall> = (
            identifier and skip(Tokens.leftParenthesis) and optional(argumentsList) and skip(Tokens.rightParenthesis)
            )
        .map { (identifier, arguments) ->
            AST.FunctionCall(identifier, arguments ?: emptyList())
        }

    val notTerm: Parser<AST.UnaryOperation> = (
            Tokens.not and parser { LanguageParsers.expression }
            ).map { (_, expression) ->
            AST.UnaryOperation(Tokens.not, expression)
        }

    val parenthesesTerm: Parser<AST.Expression> = (
            skip(Tokens.leftParenthesis) and parser { LanguageParsers.expression } and skip(Tokens.rightParenthesis)
            )

    val term: Parser<AST.Expression> = notTerm or parenthesesTerm or functionCall or constant or nil or identifier

    val multiplyDivide: Parser<AST.Expression> = leftAssociative(term, Tokens.multiply or Tokens.divide or Tokens.modulo) { left, op, right ->
        AST.BinaryOperation(left, op.type, right)
    }

    val plusMinus: Parser<AST.Expression> = leftAssociative(multiplyDivide, Tokens.plus or Tokens.minus) { left, op, right ->
        AST.BinaryOperation(left, op.type, right)
    }

    val comparison: Parser<AST.Expression> = leftAssociative(plusMinus, Tokens.doubleEqual or Tokens.lessThan or Tokens.lessThanOrEqual or Tokens.greaterThan or Tokens.greaterThanOrEqual) { left, op, right ->
        AST.BinaryOperation(left, op.type, right)
    }

    val and: Parser<AST.Expression> = leftAssociative(comparison, Tokens.and) { left, op, right ->
        AST.BinaryOperation(left, op.type, right)
    }

    val or: Parser<AST.Expression> = leftAssociative(and, Tokens.or) { left, op, right ->
        AST.BinaryOperation(left, op.type, right)
    }
}

object LanguageParsers {

    val identifier = BaseParsers.identifier

    val expression: Parser<AST.Expression> =  BaseParsers.or

    val assignment: Parser<AST.Assignment> = (
            identifier and skip(Tokens.equal) and expression
        ).map { (identifier, expression) ->
            AST.Assignment(identifier, expression)
        }

    val valueDeclaration: Parser<AST.Declaration> = (
            (Tokens.const or Tokens.let) and assignment
        )
        .map { (token, assignment) ->
            when (token.type) {
                Tokens.const -> AST.Declaration.Constant(assignment.identifier, assignment.expression)
                Tokens.let -> AST.Declaration.Variable(assignment.identifier, assignment.expression)
                else -> throw IllegalStateException("Unknown declaration type: ${token.type}")
            }
        }

    val parameterList: Parser<List<AST.Identifier>> = (
            identifier and zeroOrMore(Tokens.comma and identifier)
        )
        .map { (first, rest) ->
            listOf(first) + rest.map { (_, identifier) -> identifier }
        }

    val functionDeclaration: Parser<AST.Declaration.Function> = (
            skip(Tokens.fn) and
            identifier and
            skip(Tokens.leftParenthesis) and
            optional(parameterList) and
            skip(Tokens.rightParenthesis) and
            parser { block }
        )
        .map { (identifier, parameters, block) ->
            AST.Declaration.Function(identifier, parameters ?: emptyList(), block)
        }

    val declaration = functionDeclaration or valueDeclaration

    val block: Parser<AST.Block> = (
            skip(Tokens.leftBrace) and
            oneOrMore(parser { statement }) and
            skip(Tokens.rightBrace)
        )
        .map { statements ->  AST.Block(statements) }

    val statement: Parser<AST.Statement> = (
            block or
            declaration or
            assignment or
            expression
        ) and skip(zeroOrMore(Tokens.newline or Tokens.semicolon))
}

val ProgramParser = oneOrMore(LanguageParsers.statement).map { statements ->
    if (statements.size == 1) statements[0] else AST.Program(statements)
}

object Grammar : Grammar<AST>() {
    override val tokens: List<Token> = Tokens
    override val rootParser: Parser<AST> = ProgramParser
}
