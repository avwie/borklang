package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser


object Grammar : Grammar<AST>() {

    private val numberToken by regexToken("-?\\d+")

    private val number: Parser<AST.Constant> by numberToken.use { AST.Constant(text.toInt()) }

    override val rootParser: Parser<AST> = number
}