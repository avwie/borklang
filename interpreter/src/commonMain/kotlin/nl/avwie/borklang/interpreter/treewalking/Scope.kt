package nl.avwie.borklang.interpreter.treewalking

class Scope {
    private val variables = mutableMapOf<String, Any?>()

    fun get(name: String): Any? = variables[name]

    fun set(name: String, value: Any?) {
        variables[name] = value
    }
}
