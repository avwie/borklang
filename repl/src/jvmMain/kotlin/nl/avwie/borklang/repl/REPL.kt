package nl.avwie.borklang.repl

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import nl.avwie.borklang.interpreter.Scope
import nl.avwie.borklang.interpreter.TreeWalkingInterpreter
import nl.avwie.borklang.parser.Grammar
import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    val input = InputStreamReader(System.`in`);
    val reader = BufferedReader(input);
    val scope = Scope.default()
    val interpreter = TreeWalkingInterpreter(scope)

    val lines = mutableListOf<String>()
    while (true) {
        if (lines.isEmpty()) print("> ")
        else print("  ")
        val line = reader.readLine() ?: break
        if (line.endsWith('\\')) {
            lines.add(line.substring(0, line.length - 1))
            continue
        } else {
            lines.add(line)
        }

        val ast = Grammar.parseToEnd(lines.joinToString("\n"))
        val result = interpreter.evaluate(ast)
        lines.clear()
        println(result)
    }
}