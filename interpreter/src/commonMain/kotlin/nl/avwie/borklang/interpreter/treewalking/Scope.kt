package nl.avwie.borklang.interpreter.treewalking

import nl.avwie.borklang.ast.Expression

class Scope(
    private val parent: Scope? = null
) {
    sealed interface Function {

        val name: String
        val parameters: List<String>
        data class Native(
            override val name: String,
            override val parameters: List<String>,
            val block: Scope.() -> Any
        ) : Function

        data class UserDefined(
            override val name: String,
            override val parameters: List<String>,
            val body: Expression
        ) : Function
    }

    private val constants = mutableMapOf<String, Any?>()
    private val variables = mutableMapOf<String, Any?>()
    private val functions = mutableMapOf<String, Function>()

    fun declareConstant(name: String, value: Any?) {
        if (hasConstant(name, checkParent = false)) throw IllegalStateException("Cannot declare constant $name, constant with same name exists")
        if (hasVariable(name, checkParent = false)) throw IllegalStateException("Cannot declare constant $name, variable with same name exists")
        constants[name] = value
    }

    fun hasConstant(name: String, checkParent: Boolean): Boolean = constants.containsKey(name) || (checkParent && parent?.hasConstant(name, checkParent) ?: false)

    fun getConstant(name: String): Any? = constants[name] ?: parent?.getConstant(name)

    fun declareVariable(name: String, value: Any?) {
        if (hasConstant(name, checkParent = false)) throw IllegalStateException("Cannot declare variable $name, constant with same name exists")
        if (hasVariable(name, checkParent = false)) throw IllegalStateException("Cannot declare variable $name, variable with same name exists")
        variables[name] = value
    }

    fun hasVariable(name: String, checkParent: Boolean): Boolean = variables.containsKey(name) || (checkParent && parent?.hasVariable(name, checkParent) ?: false)

    fun getVariable(name: String): Any? {
        return variables[name] ?: parent?.getVariable(name)
    }

    fun setVariable(name: String, value: Any?) {
        when  {
            variables.containsKey(name) -> variables[name] = value
            parent != null -> parent.setVariable(name, value)
            else -> throw IllegalStateException("Variable $name is not declared")
        }
    }

    fun declareNativeFunction(name: String, parameters: List<String>, block: Scope.() -> Any) {
        functions[name] = Function.Native(name, parameters, block)
    }

    fun declareFunction(name: String, function: Expression.Declaration.Function) {
        functions[name] = Function.UserDefined(name, function.parameters.map { it.name }, function.body)
    }

    fun hasFunction(name: String): Boolean = functions.containsKey(name) || (parent?.hasFunction(name) ?: false)

    fun getFunction(name: String): Function? = functions[name] ?: parent?.getFunction(name)

    fun child(): Scope = Scope(this)

    companion object {
        fun default(
            stdOut: (Any?) -> Unit = { println(it) },
        ): Scope = Scope().apply {
            declareNativeFunction("print", listOf("value")) {
                stdOut(getVariable("value"))
            }
        }
    }
}
