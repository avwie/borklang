import com.github.h0tk3y.betterParse.grammar.parseToEnd
import kotlinx.browser.document
import kotlinx.dom.clear
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.borklang.interpreter.Interpreter
import nl.avwie.borklang.interpreter.Scope
import nl.avwie.borklang.interpreter.TreeWalkingInterpreter
import nl.avwie.borklang.parser.Grammar
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLTextAreaElement

val serializer = Json {
    prettyPrint = true
}

fun main() {
    val interpreter = TreeWalkingInterpreter(Scope.default())

    val input = document.getElementById("input") as HTMLTextAreaElement
    val output = document.getElementById("output") as HTMLTextAreaElement
    val ast = document.getElementById("ast") as HTMLTextAreaElement

    val run = document.getElementById("run") as HTMLButtonElement
    val resetRun = document.getElementById("reset_run") as HTMLButtonElement
    val clear = document.getElementById("clear") as HTMLButtonElement
    val reset = document.getElementById("reset") as HTMLButtonElement

    run.onclick = {
        it.preventDefault()
        val (result, serializedAst) = run(input.value, interpreter)
        output.value = result
        ast.value = serializedAst
        it
    }

    resetRun.onclick = {
        it.preventDefault()
        reset.click()
        run.click()
        it
    }

    clear.onclick = {
        it.preventDefault()
        input.value = ""
        output.value = ""
        ast.value = ""
        Unit
    }

    reset.onclick = {
        it.preventDefault()
        interpreter.reset(Scope.default())
        ast.clear()
        output.value = "Interpreter reset"
        Unit
    }
}

fun run(code: String, interpreter: Interpreter): Pair<String, String> {
    try {
        val ast = Grammar.parseToEnd(code)
        val serialized = serializer.encodeToString(ast)
        val result = interpreter.evaluate(ast)
        return result.toString() to serialized
    } catch (e: Exception) {
        return (e.message ?: "Unknown error") to ""
    }
}