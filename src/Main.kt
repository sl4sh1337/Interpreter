fun main() {
    val lexer = Lexer()
    lexer.source = readLine()!!
    lexer.lex()
    var p = Parser()
    p.tokenStream = lexer.tokenStream
    p.parse()
    print(p.root?.visit())
    //print(0)
}