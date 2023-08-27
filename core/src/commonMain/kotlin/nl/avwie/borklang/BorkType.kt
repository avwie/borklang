package nl.avwie.borklang

sealed interface BorkType {
    data object Nil : BorkType
    data object Number : BorkType
    data object Boolean : BorkType
    data object String : BorkType
}