package nl.avwie.borklang.interpreter.treewalking

class Scope {

    private val constants = mutableMapOf<String, Any?>()
    private val variables = mutableMapOf<String, Any?>()

    fun declareConstant(name: String, value: Any?) {
        constants[name] = value
    }

    fun hasConstant(name: String): Boolean = constants.containsKey(name)

    fun getConstant(name: String): Any? = constants[name]

    fun declareVariable(name: String, value: Any?) {
        variables[name] = value
    }

    fun hasVariable(name: String): Boolean = variables.containsKey(name)

    fun getVariable(name: String): Any? = variables[name]

    fun setVariable(name: String, value: Any?) {
        if (!variables.containsKey(name)) throw IllegalStateException("Variable $name is not declared")
        variables[name] = value
    }
}
