package nl.avwie.borklang.parser

sealed interface AST {
    data class Constant(val value: Int): AST
    data class Identifier(val name: String): AST
}
