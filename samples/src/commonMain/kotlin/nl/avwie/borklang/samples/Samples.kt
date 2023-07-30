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
                [print [x]]
                [set x [+ x 1]]
            ]
        ]
    ]
"""

const val SET_AND_GET = """
    [
        # Set and get
        [var x 0]
        [set x 4]
        x
    ]
"""

const val CONDITIONAL = """
    [
        [var x 0]
        [if [== x 0]
            [set x 1]
            [set x 2]
        ]
    ]
"""

const val FUNCTION_CALL = """
    [
        [fn sum [x y] [+ x y]]
        [sum [1 2]]
    ]
"""

const val SCOPES = """
    [
        [var x 0]
        [fn inc [amount] [
            [var y 2]
            [fn add [] [
                [set x [+ amount y]]
            ]]
            [set y [+ y 1]]
            [add []]
        ]]
        
        [inc [2]]
        x
    ]
"""

const val WHILE = """
    [
        [var x 0]
        [while [< x 10] [set x [+ x 1]]]
        x
    ]
"""

const val FIBONACCI = """
    [
        [fn fib [n] [
            [var a 1]
            [if [> n 1] [
                [var na [- n 1]]
                [var nb [- n 2]]
                [var fa [fib [na]]]
                [var fb [fib [nb]]]
                [set a [+ fa fb]]
            ]]
            a
        ]]
        [fib [10]]
    ]
"""