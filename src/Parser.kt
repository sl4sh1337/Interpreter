interface GraphItem{
    var children : MutableList<GraphItem>
    fun visit() : String
}

class NonTerm(val type : NonTermType) : GraphItem {
    override fun visit(): String {
        when(type) {
            NonTermType.program, NonTermType.expr -> return children[0].visit()
            NonTermType.constexpr -> when(children.size) {
                1 -> return children[0].visit()
                2 -> return children[1].visit() + children[0].visit()
            }
            NonTermType.binexpr -> {
                var op = children[2].visit()
                when(op) {
                    "+" -> return (children[3].visit().toInt() + children[1].visit().toInt()).toString()
                    "-" -> return (children[3].visit().toInt() - children[1].visit().toInt()).toString()
                    "*" -> return (children[3].visit().toInt() * children[1].visit().toInt()).toString()
                    "/" -> return (children[3].visit().toInt() / children[1].visit().toInt()).toString()
                    "<" -> return if(children[3].visit().toInt() < children[1].visit().toInt()) 1.toString() else 0.toString()
                    ">" -> return if(children[3].visit().toInt() > children[1].visit().toInt()) 1.toString() else 0.toString()
                }
            }
        }
        return ""
    }

    override var children: MutableList<GraphItem> = mutableListOf()

    enum class NonTermType {
        program, expr, constexpr, binexpr
    }
}

class End : GraphItem {
    override fun visit(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var children: MutableList<GraphItem> = mutableListOf()}

class Parser {
    var tokenStream : MutableList<Token> = mutableListOf()
    var root : GraphItem? = null
    fun parse() {
        val stack = mutableListOf<GraphItem>()
        stack.add(End())
        stack.add(NonTerm(NonTerm.NonTermType.program))
        root = stack.last()
        var i = 0
        while (stack.last() !is End) {
            when(val x = stack.last()) {
                is Token -> if(x.type == tokenStream[i].type || (x.type == Token.TokenType.operation && tokenStream[i].type == Token.TokenType.minus)) {
                    (stack.last() as Token).value = tokenStream[i].value
                    ++i;
                    stack.removeAt(stack.lastIndex)
                }
                is NonTerm -> when(x.type) {

                    NonTerm.NonTermType.program -> when(tokenStream[i].type) {
                        Token.TokenType.identifier, Token.TokenType.number, Token.TokenType.minus, Token.TokenType.ob -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(NonTerm(NonTerm.NonTermType.expr))
                            p.children.add(stack.last())
                        }
                    }

                    NonTerm.NonTermType.expr -> when(tokenStream[i].type) {
                        Token.TokenType.number, Token.TokenType.minus -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(NonTerm(NonTerm.NonTermType.constexpr))
                            p.children.add(stack.last())
                        }
                        Token.TokenType.ob -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(NonTerm(NonTerm.NonTermType.binexpr))
                            p.children.add(stack.last())
                        }
                    }

                    NonTerm.NonTermType.constexpr -> when(tokenStream[i].type) {
                        Token.TokenType.number -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(Token(Token.TokenType.number))
                            p.children.add(stack.last())
                        }
                        Token.TokenType.minus -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(Token(Token.TokenType.number))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.minus))
                            p.children.add(stack.last())
                        }
                    }

                    NonTerm.NonTermType.binexpr -> when(tokenStream[i].type) {
                        Token.TokenType.ob -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(Token(Token.TokenType.cb))
                            p.children.add(stack.last())
                            stack.add(NonTerm(NonTerm.NonTermType.expr))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.operation))
                            p.children.add(stack.last())
                            stack.add(NonTerm(NonTerm.NonTermType.expr))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.ob))
                            p.children.add(stack.last())
                        }
                    }
                }
            }
        }
    }
}