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
    val lexer = Lexer()
    while (true) {
        lexer.source = readLine()!!
        lexer.lex()
        var p = Parser()
        p.tokenStream = lexer.tokenStream
        p.parse()
        if((p.root?.children?.get(0) as NonTerm).type != NonTerm.NonTermType.fundef){
            print(p.root?.visit())
            exitProcess(0)
        }
    }
}