package nl.avwie.borklang.compiler

interface CompilerPass<I, O> {
    fun compile(input: I): O
}