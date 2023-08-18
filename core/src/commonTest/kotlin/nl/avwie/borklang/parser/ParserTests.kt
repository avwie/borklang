package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParserTests {
    @Test
    fun numbers() {
        val positive = Grammar.parseToEnd("123")
        assertTrue { positive is AST.Constant }
        assertEquals(123, (positive as AST.Constant.Number).value)

        val negative = Grammar.parseToEnd("-123")
        assertTrue { negative is AST.Constant }
        assertEquals(-123, (negative as AST.Constant.Number).value)
    }

    @Test
    fun strings() {
        val string = Grammar.parseToEnd("\"hello world\"")
        assertTrue { string is AST.Constant }
        assertEquals("hello world", (string as AST.Constant.String).value)
    }

    @Test
    fun identifiers() {
        val identifier = Grammar.parseToEnd("hello_world")
        assertTrue { identifier is AST.Identifier }
        assertEquals("hello_world", (identifier as AST.Identifier).name)
    }

    @Test
    fun nil() {
        val nil = Grammar.parseToEnd("Nil")
        assertTrue { nil is AST.Nil }
    }

    @Test
    fun assignment() {
        val assignment = Grammar.parseToEnd("x = 123")
        assertTrue { assignment is AST.Assignment }
        require(assignment is AST.Assignment)
        assertEquals("x", assignment.identifier.name)
        assertTrue { assignment.expression is AST.Constant }
        assertEquals(123, (assignment.expression as AST.Constant.Number).value)
    }

    @Test
    fun multipleStatements() {
        val program = Grammar.parseToEnd("x = 123\ny = 456; z = \"Foobar\";;;\n;;foo=bar")
        assertTrue { program is AST.Program }
        require(program is AST.Program)
        assertEquals(4, program.statements.size)
        assertTrue { program.statements[0] is AST.Assignment }
        assertTrue { program.statements[1] is AST.Assignment }
        assertTrue { program.statements[2] is AST.Assignment }
        assertTrue { program.statements[3] is AST.Assignment }
    }

    @Test
    fun valueDeclarations() {
        val program = Grammar.parseToEnd("""
            const x = 123;
            let y = 456;
        """.trimIndent())
        require(program is AST.Program)
        assertEquals(2, (program).statements.size)
        assertTrue { program.statements[0] is AST.Declaration.Constant }
        assertEquals("x", (program.statements[0] as AST.Declaration.Constant).identifier.name)
        assertEquals(123, ((program.statements[0] as AST.Declaration.Constant).expression as AST.Constant.Number).value)
        assertTrue { program.statements[1] is AST.Declaration.Variable }
        assertEquals("y", (program.statements[1] as AST.Declaration.Variable).identifier.name)
        assertEquals(456, ((program.statements[1] as AST.Declaration.Variable).expression as AST.Constant.Number).value)
    }

    @Test
    fun block() {
        val program = Grammar.parseToEnd("""
            {
                const x = 123;
                let y = 456;
            }
            
            {
                const z = 789;
            }
        """.trimIndent())
        require(program is AST.Program)
        assertEquals(2, (program).statements.size)
        assertTrue { program.statements[0] is AST.Block }
        assertTrue { program.statements[1] is AST.Block }
    }
}