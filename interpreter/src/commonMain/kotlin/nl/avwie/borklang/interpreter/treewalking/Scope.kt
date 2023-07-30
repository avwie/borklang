package nl.avwie.borklang.interpreter.treewalking

import nl.avwie.borklang.ast.Expression

class Scope(private val parent: Scope? = null) {

    private val constants = mutableMapOf<String, Any?>()
    private val variables = mutableMapOf<String, Any?>()
    private val functions = mutableMapOf<String, Expression.Declaration.Function>()

    fun declareConstant(name: String, value: Any?) {
        if (hasConstant(name)) throw IllegalStateException("Cannot declare constant $name, constant with same name exists")
        if (hasVariable(name)) throw IllegalStateException("Cannot declare constant $name, variable with same name exists")
        constants[name] = value
    }

    fun hasConstant(name: String): Boolean = constants.containsKey(name) || (parent?.hasConstant(name) ?: false)

    fun getConstant(name: String): Any? = constants[name] ?: parent?.getConstant(name)

    fun declareVariable(name: String, value: Any?) {
        if (hasConstant(name)) throw IllegalStateException("Cannot declare variable $name, constant with same name exists")
        if (hasVariable(name)) throw IllegalStateException("Cannot declare variable $name, variable with same name exists")
        variables[name] = value
    }

    fun hasVariable(name: String): Boolean = variables.containsKey(name) || (parent?.hasVariable(name) ?: false)

    fun getVariable(name: String): Any? = variables[name] ?: parent?.getVariable(name)

    fun setVariable(name: String, value: Any?) {
        when  {
            variables.containsKey(name) -> variables[name] = value
            parent != null -> parent.setVariable(name, value)
            else -> throw IllegalStateException("Variable $name is not declared")
        }
    }

    fun declareFunction(name: String, function: Expression.Declaration.Function) {
        functions[name] = function
    }

    fun hasFunction(name: String): Boolean = functions.containsKey(name) || (parent?.hasFunction(name) ?: false)

    fun getFunction(name: String): Expression.Declaration.Function? = functions[name] ?: parent?.getFunction(name)

    fun child(): Scope = Scope(this)
}
