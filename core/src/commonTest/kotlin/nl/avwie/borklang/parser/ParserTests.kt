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
    fun booleans() {
        val trueToken = Grammar.parseToEnd("True")
        assertTrue { trueToken is AST.Constant }
        assertEquals(true, (trueToken as AST.Constant.Boolean).value)

        val falseToken = Grammar.parseToEnd("False")
        assertTrue { falseToken is AST.Constant }
        assertEquals(false, (falseToken as AST.Constant.Boolean).value)
    }

    @Test
    fun unaryOperations() {
        val not = Grammar.parseToEnd("!True")
        assertTrue { not is AST.UnaryOperation }
        assertEquals(Tokens.not, (not as AST.UnaryOperation).operator)
        assertTrue { not.expression is AST.Constant.Boolean }
        assertEquals(true, (not.expression as AST.Constant.Boolean).value)
    }

    @Test
    fun binaryOperations() {
        val plus = Grammar.parseToEnd("1 + 2")
        assertTrue { plus is AST.BinaryOperation }
        assertEquals(Tokens.plus, (plus as AST.BinaryOperation).operator)
        assertTrue { plus.left is AST.Constant.Number }
        assertEquals(1, (plus.left as AST.Constant.Number).value)
        assertTrue { plus.right is AST.Constant.Number }
        assertEquals(2, (plus.right as AST.Constant.Number).value)
    }

    @Test
    fun precedence() {
        val precedence = Grammar.parseToEnd("1 + 2 * 3")
        assertTrue { precedence is AST.BinaryOperation }
        assertEquals(Tokens.plus, (precedence as AST.BinaryOperation).operator)
        assertTrue { precedence.left is AST.Constant.Number }
        assertEquals(1, (precedence.left as AST.Constant.Number).value)
        assertTrue { precedence.right is AST.BinaryOperation }
        assertEquals(Tokens.multiply, (precedence.right as AST.BinaryOperation).operator)
        assertTrue { (precedence.right as AST.BinaryOperation).left is AST.Constant.Number }
        assertEquals(2, ((precedence.right as AST.BinaryOperation).left as AST.Constant.Number).value)
        assertTrue { (precedence.right as AST.BinaryOperation).right is AST.Constant.Number }
        assertEquals(3, ((precedence.right as AST.BinaryOperation).right as AST.Constant.Number).value)
    }

    @Test
    fun precedence2() {
        val precedence = Grammar.parseToEnd("1 * 2 + 3 / 4 < 5 * 6 / 7 + 8")
        require(precedence is AST.BinaryOperation)
        assertEquals(Tokens.lessThan, precedence.operator)

        val left = precedence.left as AST.BinaryOperation
        assertEquals(Tokens.plus, left.operator)
        val leftLeft = left.left as AST.BinaryOperation
        assertEquals(Tokens.multiply, leftLeft.operator)

        val leftRight = left.right as AST.BinaryOperation
        assertEquals(Tokens.divide, leftRight.operator)

        val right = precedence.right as AST.BinaryOperation
        assertEquals(Tokens.plus, right.operator)
        val rightLeft = right.left as AST.BinaryOperation
        assertEquals(Tokens.divide, rightLeft.operator)
        rightLeft.right as AST.Constant.Number
        val rightLeftLeft = rightLeft.left as AST.BinaryOperation
        assertEquals(Tokens.multiply, rightLeftLeft.operator)
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

    @Test
    fun nestedBlocks() {
        val program = Grammar.parseToEnd("""
            {
                {
                    const x = 123;
                }
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
        require(program.statements[0] is AST.Block)
        assertEquals(2, (program.statements[0] as AST.Block).statements.size)
        assertTrue { (program.statements[0] as AST.Block).statements[0] is AST.Block }
        assertTrue { (program.statements[0] as AST.Block).statements[1] is AST.Declaration.Variable }
    }

    @Test
    fun functionDeclaration() {
        val program = Grammar.parseToEnd("""
            fn foo() {
                const x = 123;
                let y = 456;
            }
            
            fn bar(x, y) {
                const z = 789;
            }
        """.trimIndent())

        require(program is AST.Program)
        assertEquals(2, (program).statements.size)
        assertTrue { program.statements[0] is AST.Declaration.Function }
        assertEquals(0, (program.statements[0] as AST.Declaration.Function).parameters.size)
        assertTrue { program.statements[1] is AST.Declaration.Function }
        assertEquals(2, (program.statements[1] as AST.Declaration.Function).parameters.size)
        assertEquals("x", (program.statements[1] as AST.Declaration.Function).parameters[0].name)
        assertEquals("y", (program.statements[1] as AST.Declaration.Function).parameters[1].name)
    }

    @Test
    fun functionCall() {
        val program = Grammar.parseToEnd("""
            foo();
            bar(123, "hello world");
        """.trimIndent())

        require(program is AST.Program)
        assertEquals(2, (program).statements.size)
        assertTrue { program.statements[0] is AST.FunctionCall }
        assertEquals(0, (program.statements[0] as AST.FunctionCall).arguments.size)
        assertTrue { program.statements[1] is AST.FunctionCall }
        assertEquals(2, (program.statements[1] as AST.FunctionCall).arguments.size)
        assertTrue { (program.statements[1] as AST.FunctionCall).arguments[0] is AST.Constant.Number }
        assertEquals(123, ((program.statements[1] as AST.FunctionCall).arguments[0] as AST.Constant.Number).value)
    }

    @Test
    fun nestedFunctionCall() {
        val statement = Grammar.parseToEnd("""
            foo(bar(123, "hello world"));
        """.trimIndent())

        require(statement is AST.Statement)
        assertTrue { statement is AST.FunctionCall }
        assertEquals(1, (statement as AST.FunctionCall).arguments.size)
        assertTrue { statement.arguments[0] is AST.FunctionCall }
        assertEquals(2, (statement.arguments[0] as AST.FunctionCall).arguments.size)
        assertTrue { (statement.arguments[0] as AST.FunctionCall).arguments[0] is AST.Constant.Number }
        assertEquals(123, ((statement.arguments[0] as AST.FunctionCall).arguments[0] as AST.Constant.Number).value)
    }
}