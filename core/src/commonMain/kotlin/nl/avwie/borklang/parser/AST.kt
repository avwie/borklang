package nl.avwie.borklang.parser

sealed interface AST {
    data class Constant(val value: Int): AST
}
