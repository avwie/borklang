package nl.avwie.borklang.parser

sealed interface AST {
    sealed interface Constant : AST {
        data class Number(val value: Int): Constant
        data class String(val value: kotlin.String): Constant
    }
    data class Identifier(val name: String): AST
}
