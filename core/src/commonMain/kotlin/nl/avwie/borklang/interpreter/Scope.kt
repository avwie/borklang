package nl.avwie.borklang.interpreter

import nl.avwie.borklang.BorkValue
import nl.avwie.borklang.parser.AST

interface Scope {
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

    fun declareConstant(name: String, value: BorkValue)
    fun resolveValue(name: String): BorkValue
    fun resolveFunction(name: String): Scope.Function
    fun hasConstant(name: String, checkParent: Boolean): Boolean
    fun getConstant(name: String): BorkValue?
    fun declareVariable(name: String, value: BorkValue)
    fun hasVariable(name: String, checkParent: Boolean): Boolean
    fun getVariable(name: String): BorkValue?
    fun setVariable(name: String, value: BorkValue)
    fun declareNativeFunction(name: String, parameters: List<String>, block: Scope.() -> BorkValue)
    fun declareFunction(name: String, function: AST.Declaration.Function)
    fun hasFunction(name: String): Boolean
    fun getFunction(name: String): Scope.Function?
    fun child(): Scope
}

class ScopeImpl(
    private val parent: Scope? = null
) : Scope {

    private val constants = mutableMapOf<String, BorkValue>()
    private val variables = mutableMapOf<String, BorkValue>()
    private val functions = mutableMapOf<String, Scope.Function>()

    override fun declareConstant(name: String, value: BorkValue) {
        if (hasConstant(name, checkParent = false)) throw IllegalStateException("Cannot declare constant $name, constant with same name exists")
        if (hasVariable(name, checkParent = false)) throw IllegalStateException("Cannot declare constant $name, variable with same name exists")
        constants[name] = value
    }

    override fun resolveValue(name: String): BorkValue {
        return getVariable(name) ?: getConstant(name) ?: throw RuntimeException("Cannot resolve value $name, no constant or variable with same name exists")
    }

    override fun resolveFunction(name: String): Scope.Function {
        return getFunction(name) ?: throw RuntimeException("Cannot resolve function $name, no function with same name exists")
    }

    override fun hasConstant(name: String, checkParent: Boolean): Boolean = constants.containsKey(name) || (checkParent && parent?.hasConstant(name, checkParent) ?: false)

    override fun getConstant(name: String): BorkValue? = constants[name] ?: parent?.getConstant(name)

    override fun declareVariable(name: String, value: BorkValue) {
        if (hasConstant(name, checkParent = false)) throw RuntimeException("Cannot declare variable $name, constant with same name exists")
        if (hasVariable(name, checkParent = false)) throw RuntimeException("Cannot declare variable $name, variable with same name exists")
        variables[name] = value
    }

    override fun hasVariable(name: String, checkParent: Boolean): Boolean = variables.containsKey(name) || (checkParent && parent?.hasVariable(name, checkParent) ?: false)

    override fun getVariable(name: String): BorkValue? {
        return variables[name] ?: parent?.getVariable(name) ?: getConstant(name)
    }

    override fun setVariable(name: String, value: BorkValue) {
        when  {
            constants.containsKey(name) -> throw IllegalStateException("Cannot set constant $name")
            variables.containsKey(name) -> variables[name] = value
            parent != null -> parent.setVariable(name, value)
            else -> throw IllegalStateException("Variable $name is not declared")
        }
    }

    override fun declareNativeFunction(name: String, parameters: List<String>, block: Scope.() -> BorkValue) {
        functions[name] = Scope.Function.Native(name, parameters, block)
    }

    override fun declareFunction(name: String, function: AST.Declaration.Function) {
        if (hasFunction(name)) throw RuntimeException("Cannot declare function $name, function with same name exists")
        functions[name] = Scope.Function.UserDefined(name, function.parameters.map { it.name }, function.body)
    }

    override fun hasFunction(name: String): Boolean = functions.containsKey(name) || (parent?.hasFunction(name) ?: false)

    override fun getFunction(name: String): Scope.Function? = functions[name] ?: parent?.getFunction(name)

    override fun child(): Scope = ScopeImpl(this)

    companion object {
        fun default(
            stdOut: (BorkValue) -> BorkValue = { println(it.asString().value); BorkValue.Nil },
        ): Scope = ScopeImpl().apply {
            declareNativeFunction("print", listOf("value")) {
                stdOut(resolveValue("value"))
            }
        }
    }
}