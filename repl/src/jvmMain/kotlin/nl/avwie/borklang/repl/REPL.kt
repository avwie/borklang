package nl.avwie.borklang.repl

import nl.avwie.borklang.interpreter.treewalking.Scope
import nl.avwie.borklang.interpreter.treewalking.TreeWalkingInterpreter
import nl.avwie.borklang.parser.Parser
import nl.avwie.borklang.parser.parse
import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    val input = InputStreamReader(System.`in`);
    val reader = BufferedReader(input);
    val scope = Scope.default(
        stdOut = { println(it) },
    )
    val interpreter = TreeWalkingInterpreter(scope)
    val parser = Parser.instance()

    while (true) {
        print("> ")
        val line = reader.readLine() ?: break
        val ast = parser.parse(line)
        val result = interpreter.evaluate(ast)
        println(result)
    }
}