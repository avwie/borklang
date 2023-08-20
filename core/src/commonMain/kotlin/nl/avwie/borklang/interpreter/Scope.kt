package nl.avwie.borklang.interpreter

import nl.avwie.borklang.parser.AST

class Scope(
    private val parent: Scope? = null
) {
    sealed interface Function {

        val name: String
        val parameters: List<String>
        data class Native(
            override val name: String,
            override val parameters: List<String>,
            val block: Scope.() -> BorkValue
        ) : Function

        data class UserDefined(
            override val name: String,
            override val parameters: List<String>,
            val body: AST.Expression
        ) : Function
    }

    private val constants = mutableMapOf<String, BorkValue>()
    private val variables = mutableMapOf<String, BorkValue>()
    private val functions = mutableMapOf<String, Function>()

    fun declareConstant(name: String, value: BorkValue) {
        if (hasConstant(name, checkParent = false)) throw IllegalStateException("Cannot declare constant $name, constant with same name exists")
        if (hasVariable(name, checkParent = false)) throw IllegalStateException("Cannot declare constant $name, variable with same name exists")
        constants[name] = value
    }

    fun resolveValue(name: String): BorkValue {
        return getVariable(name) ?: getConstant(name) ?: throw RuntimeException("Cannot resolve value $name, no constant or variable with same name exists")
    }

    fun resolveFunction(name: String): Function {
        return getFunction(name) ?: throw RuntimeException("Cannot resolve function $name, no function with same name exists")
    }

    private fun hasConstant(name: String, checkParent: Boolean): Boolean = constants.containsKey(name) || (checkParent && parent?.hasConstant(name, checkParent) ?: false)

    private fun getConstant(name: String): BorkValue? = constants[name] ?: parent?.getConstant(name)

    fun declareVariable(name: String, value: BorkValue) {
        if (hasConstant(name, checkParent = false)) throw RuntimeException("Cannot declare variable $name, constant with same name exists")
        if (hasVariable(name, checkParent = false)) throw RuntimeException("Cannot declare variable $name, variable with same name exists")
        variables[name] = value
    }

    private fun hasVariable(name: String, checkParent: Boolean): Boolean = variables.containsKey(name) || (checkParent && parent?.hasVariable(name, checkParent) ?: false)

    private fun getVariable(name: String): BorkValue? {
        return variables[name] ?: parent?.getVariable(name) ?: getConstant(name)
    }

    fun setVariable(name: String, value: BorkValue) {
        when  {
            variables.containsKey(name) -> variables[name] = value
            parent != null -> parent.setVariable(name, value)
            else -> throw IllegalStateException("Variable $name is not declared")
        }
    }

    fun declareNativeFunction(name: String, parameters: List<String>, block: Scope.() -> BorkValue) {
        functions[name] = Function.Native(name, parameters, block)
    }

    fun declareFunction(name: String, function: AST.Declaration.Function) {
        if (hasFunction(name)) throw RuntimeException("Cannot declare function $name, function with same name exists")
        functions[name] = Function.UserDefined(name, function.parameters.map { it.name }, function.body)
    }

    private fun hasFunction(name: String): Boolean = functions.containsKey(name) || (parent?.hasFunction(name) ?: false)

    private fun getFunction(name: String): Function? = functions[name] ?: parent?.getFunction(name)

    fun child(): Scope = Scope(this)

    companion object {
        fun default(
            stdOut: (BorkValue) -> BorkValue = { println(it.asString().value); BorkValue.Nil },
        ): Scope = Scope().apply {
            declareNativeFunction("print", listOf("value")) {
                stdOut(resolveValue("value"))
            }
        }
    }
}