package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import kotlin.reflect.KProperty

object Tokens : TokenSetBuilder() {

    val whitespace by regexToken("\\s+", ignore = true)
    val newline by regexToken("[\r\n]+", ignore = true)
    val semicolon by literalToken(";")
    val comma by literalToken(",")
    val leftBrace by literalToken("{")
    val rightBrace by literalToken("}")
    val leftParenthesis by literalToken("(")
    val rightParenthesis by literalToken(")")

    val const by literalToken("const")
    val let by literalToken("let")
    val fn by literalToken("fn")
    val nil by literalToken("Nil")
    val returnToken by literalToken("return")
    val ifToken by literalToken("if")
    val elseToken by literalToken("else")
    val whileToken by literalToken("while")
    val trueToken by literalToken("True")
    val falseToken by literalToken("False")

    val number by regexToken("-?\\d+")
    val string by regexToken("\"[^\"]*\"")
    val identifier by regexToken("[a-zA-Z_][a-zA-Z0-9_]*")

    val plus by literalToken("+")
    val minus by literalToken("-")
    val multiply by literalToken("*")
    val divide by literalToken("/")
    val modulo by literalToken("%")

    val and by literalToken("&&")
    val or by literalToken("||")

    val doubleEqual by literalToken("==")
    val notEqual by literalToken("!=")
    val equal by literalToken("=")
    val lessThan by literalToken("<")
    val lessThanOrEqual by literalToken("<=")
    val greaterThan by literalToken(">")
    val greaterThanOrEqual by literalToken(">=")

    val not by literalToken("!")
}

abstract class TokenSetBuilder(
    private val tokens: MutableList<Token> = mutableListOf()
) : List<Token> by tokens {
    protected operator fun Token.provideDelegate(thisRef: TokenSetBuilder, property: KProperty<*>): Token =
        also {
            if (it.name == null) {
                it.name = property.name
            }
            tokens.add(it)
        }

    protected operator fun Token.getValue(thisRef: TokenSetBuilder, property: KProperty<*>): Token = this
}