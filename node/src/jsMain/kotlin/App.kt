import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.regexToken
import nl.avwie.borklang.parser.Tokens

fun main() {
    val input = "fn foo = (n) -> { n + 1 }"
    val tokens = DefaultTokenizer(Tokens).tokenize(input)
    println(tokens.toList())
}