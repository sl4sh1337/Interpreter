import kotlin.system.exitProcess

class Function {
    var numofparams : Int = 0
    var paramnames : MutableList<String> = mutableListOf()
    var funref : GraphItem? = null
}

object funnames {
    var values : HashMap<String, Function> = hashMapOf()
}

fun main() {
    while (true) {
        var lexer = Lexer()
        lexer.source = readLine()!!
        lexer.lex()
        var parser = Parser()
        parser.tokenStream = lexer.tokenStream
        parser.parse()
        if((parser.root?.children?.get(0) as NonTerm).type != NonTerm.NonTermType.fundef){
            print(parser.root?.visit())
            exitProcess(0)
        }
    }
}