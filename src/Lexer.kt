class Token(val type : TokenType, var value : String = "") : GraphItem {
    override fun visit(): String {
        return value
    }

    enum class TokenType
    {
        identifier, number, operation, ob, cb, ocb, ccb, osb, csb, qm, comma, colon, minus, assign, error
    }
    override var children: MutableList<GraphItem> = mutableListOf()
}

class Lexer {
    var source : String = ""
    val tokenStream : MutableList<Token> = mutableListOf()
    var curstate = 0
    var buf = ""
    fun lex() {
        for (c in source) {
            when(c){
                in 'a'..'z', in 'A'..'Z' -> if(tokenStream.isNotEmpty() && tokenStream.last().type == Token.TokenType.identifier)
                                                tokenStream.last().value += c
                                            else tokenStream.add(Token(Token.TokenType.identifier, c.toString()))
                in '0'..'9' -> if(tokenStream.isNotEmpty() && tokenStream.last().type == Token.TokenType.number)
                                    tokenStream.last().value += c
                                else tokenStream.add(Token(Token.TokenType.number, c.toString()))
                in "+*/%><" -> tokenStream.add(Token(Token.TokenType.operation, c.toString()))
                '(' -> tokenStream.add(Token(Token.TokenType.ob, c.toString()))
                ')' -> tokenStream.add(Token(Token.TokenType.cb, c.toString()))
                '{' -> tokenStream.add(Token(Token.TokenType.ocb, c.toString()))
                '}' -> tokenStream.add(Token(Token.TokenType.ccb, c.toString()))
                '[' -> tokenStream.add(Token(Token.TokenType.osb, c.toString()))
                ']' -> tokenStream.add(Token(Token.TokenType.csb, c.toString()))
                '?' -> tokenStream.add(Token(Token.TokenType.qm, c.toString()))
                ':' -> tokenStream.add(Token(Token.TokenType.colon, c.toString()))
                ',' -> tokenStream.add(Token(Token.TokenType.comma, c.toString()))
                '-' -> tokenStream.add(Token(Token.TokenType.minus, c.toString()))
                '=' -> tokenStream.add(Token(Token.TokenType.assign, c.toString()))
                else -> Token(Token.TokenType.error, c.toString())
            }
        }
    }
}