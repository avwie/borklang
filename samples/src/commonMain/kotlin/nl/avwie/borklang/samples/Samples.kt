package nl.avwie.borklang.samples

const val EMPTY = "[]"

const val LITERAL = "[1 2.0 'foo bar' true false]"

const val VARIABLE_DECLARATION = "[var x 1]"

const val CONSTANT_DECLARATION = "[const x 1]"

const val FUNCTION_DECLARATION = "[fn sum [x y] [+ x y]]"

const val SIMPLE_PROGRAM = """
    [
        # Simple program
        [var x 0]
        
        [while [< x 10]
            [
                [print x]
                [set x [+ x 1]]
            ]
        ]
    ]
"""