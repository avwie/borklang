package nl.avwie.borklang.lexer

interface Lexer {

    fun getTokens(): Sequence<Token>

    companion object {
        fun instance(source: String): Lexer = LexerImpl(
            scanner = Scanner.instance(source)
        )
    }
}



internal class LexerImpl(
    private val scanner: Scanner
) : Lexer {
    override fun getTokens(): Sequence<Token> = sequence {
        while (!scanner.isEof()) {
            nextToken()?.also { yield(it) }
        }
        yield(Token.EOF)
    }

    private fun nextToken(): Token? = when (val c = scanner.scan()) {
        '[' -> Token.Bracket.Open
        ']' -> Token.Bracket.Close

        '+' -> Token.Operator.Plus
        '-' -> Token.Operator.Minus
        '*' -> Token.Operator.Multiply
        '/' -> Token.Operator.Divide
        '%' -> Token.Operator.Modulo
        '^' -> Token.Operator.Power
        '=' -> when (scanner.peek()) {
            '=' -> {
                scanner.scan()
                Token.Operator.DoubleEquals
            }
            else -> Token.Operator.Equals
        }

        '!' -> when (scanner.peek()) {
            '=' -> {
                scanner.scan()
                Token.Operator.NotEquals
            }
            else -> Token.Operator.Not
        }

        '>' -> when (scanner.peek()) {
            '=' -> {
                scanner.scan()
                Token.Operator.GreaterThanOrEqual
            }
            else -> Token.Operator.GreaterThan
        }

        '<' -> when (scanner.peek()) {
            '=' -> {
                scanner.scan()
                Token.Operator.LessThanOrEqual
            }
            else -> Token.Operator.LessThan
        }

        '&' -> when (scanner.peek()) {
            '&' -> {
                scanner.scan()
                Token.Operator.And
            }
            else -> unexpectedCharacter(c)
        }

        '|' -> when (scanner.peek()) {
            '|' -> {
                scanner.scan()
                Token.Operator.Or
            }
            else -> unexpectedCharacter(c)
        }

        in '0'..'9' -> {
            val number = StringBuilder()
            number.append(c)
            while (!scanner.isEof() && scanner.peek() in '0'..'9') {
                number.append(scanner.scan())
            }
            if (scanner.peek() == '.') {
                number.append(scanner.scan())
                while (!scanner.isEof() && scanner.peek() in '0'..'9') {
                    number.append(scanner.scan())
                }
                Token.Literal.Float(number.toString().toDouble())
            } else {
                Token.Literal.Integer(number.toString().toInt())
            }
        }

        in 'a'..'z', in 'A'..'Z', '_' -> {
            val identifier = StringBuilder()
            identifier.append(c)
            while (!scanner.isEof() && scanner.peek() in 'a'..'z' || scanner.peek() in 'A'..'Z' || scanner.peek() == '_') {
                identifier.append(scanner.scan())
            }
            when (identifier.toString()) {
                "if" -> Token.Keyword.If
                "def" -> Token.Keyword.Def
                "var" -> Token.Keyword.Var
                "const" -> Token.Keyword.Const
                "true" -> Token.Literal.Boolean(true)
                "false" -> Token.Literal.Boolean(false)
                else -> Token.Literal.Identifier(identifier.toString())
            }
        }

        in "'\"" -> {
            val string = StringBuilder()
            while (!scanner.isEof() && scanner.peek() != c) {
                string.append(scanner.scan())
            }
            if (scanner.isEof()) {
                throw IllegalStateException("Unexpected end of file")
            }
            scanner.scan()
            Token.Literal.String(string.toString())
        }

        '#' -> {
            val comment = StringBuilder()
            while (!scanner.isEof() && scanner.peek() != '\n') {
                comment.append(scanner.scan())
            }
            Token.Comment(comment.toString())
        }

        in " \t\r\n" -> when (scanner.peek()) {
            null -> null
            else -> nextToken()
        }

        else -> unexpectedCharacter(c)
    }

    private fun unexpectedCharacter(c: Char): Nothing = throw IllegalStateException("Unexpected character: $c at ${scanner.line}:${scanner.column}")
}