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
            NonTermType.ifexpr -> {
                var cond = children[9].visit()
                if(cond != "0")
                    return children[5].visit()
                else return children[1].visit()
            }
            NonTermType.fundef -> {
                var f = Function()
                var param = children[5]
                while (param.children.size != 1) {
                    ++f.numofparams
                    f.paramnames.add((param.children[2] as Token).value)
                    param = param.children[0]
                }
                ++f.numofparams
                f.paramnames.add((param.children[0] as Token).value)
                f.funref = this
                funnames.values[(children[7] as Token).value] = f
            }
        }
        return ""
    }

    override var children: MutableList<GraphItem> = mutableListOf()

    enum class NonTermType {
        program, expr, constexpr, binexpr, ifexpr, fundef, paramlist, arglist
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
                        Token.TokenType.number, Token.TokenType.minus, Token.TokenType.ob, Token.TokenType.osb -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(NonTerm(NonTerm.NonTermType.expr))
                            p.children.add(stack.last())
                        }
                        Token.TokenType.identifier -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(NonTerm(NonTerm.NonTermType.fundef))
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
                        Token.TokenType.identifier -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(Token(Token.TokenType.identifier))
                            p.children.add(stack.last())
                        }
                        Token.TokenType.ob -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(NonTerm(NonTerm.NonTermType.binexpr))
                            p.children.add(stack.last())
                        }
                        Token.TokenType.osb -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(NonTerm(NonTerm.NonTermType.ifexpr))
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

                    NonTerm.NonTermType.ifexpr -> when(tokenStream[i].type){
                        Token.TokenType.osb -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(Token(Token.TokenType.cb))
                            p.children.add(stack.last())
                            stack.add(NonTerm(NonTerm.NonTermType.expr))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.ob))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.colon))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.cb))
                            p.children.add(stack.last())
                            stack.add(NonTerm(NonTerm.NonTermType.expr))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.ob))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.qm))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.csb))
                            p.children.add(stack.last())
                            stack.add(NonTerm(NonTerm.NonTermType.expr))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.osb))
                            p.children.add(stack.last())
                        }
                    }

                    NonTerm.NonTermType.fundef -> when(tokenStream[i].type) {
                        Token.TokenType.identifier -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            stack.add(Token(Token.TokenType.ccb))
                            p.children.add(stack.last())
                            stack.add(NonTerm(NonTerm.NonTermType.expr))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.ocb))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.assign))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.cb))
                            p.children.add(stack.last())
                            stack.add(NonTerm(NonTerm.NonTermType.paramlist))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.ob))
                            p.children.add(stack.last())
                            stack.add(Token(Token.TokenType.identifier))
                            p.children.add(stack.last())
                        }
                    }

                    NonTerm.NonTermType.paramlist -> when(tokenStream[i].type) {
                        Token.TokenType.identifier -> {
                            var p = stack.last()
                            stack.removeAt(stack.lastIndex)
                            if(tokenStream[i + 1].type == Token.TokenType.comma) {
                                stack.add(NonTerm(NonTerm.NonTermType.paramlist))
                                p.children.add(stack.last())
                                stack.add(Token(Token.TokenType.comma))
                                p.children.add(stack.last())
                                stack.add(Token(Token.TokenType.identifier))
                                p.children.add(stack.last())
                            }
                            else {
                                stack.add(Token(Token.TokenType.identifier))
                                p.children.add(stack.last())
                            }
                        }
                    }
                }
            }
        }
    }
}