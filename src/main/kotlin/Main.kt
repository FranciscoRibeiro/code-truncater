import com.github.javaparser.ParseProblemException
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.StringLiteralExpr
import java.io.File

fun idxsOfCamelCaseStr(str: String): List<Int> {
    val res = mutableListOf<Int>()
    for(i in 1 until str.length){
        if(str[i-1].isLowerCase() && str[i].isUpperCase()){
            res.add(i)
        }
    }
    return res
}

fun idxsOfCamelCaseNode(node: Node): List<Int> {
    return when(node) {
        is SimpleName, is StringLiteralExpr -> idxsOfCamelCaseStr(node.toString()) + idxsUnderscore(node.toString())
        else -> emptyList()
    }.map { it + node.begin.get().column-1 }
}

fun idxsUnderscore(str: String): List<Int> {
    return str.mapIndexedNotNull { i,c -> if(c == '_') listOf(i,i+1) else null }.flatten()
}

fun columns(node: Node): List<Int> {
    return idxsOfCamelCaseNode(node) + listOf(node.begin.get().column-1, node.end.get().column)
}

fun inLine(node: Node, lineNr: Int): Boolean {
    val beginPos = node.begin.orElse(null) ?: return false
    val endPos = node.end.orElse(null) ?: return false
    return beginPos.line == lineNr && endPos.line == lineNr
}

fun hasColNr(node: Node, colNr: Int): Boolean {
    val beginPos = node.begin.orElse(null) ?: return false
    val endPos = node.end.orElse(null) ?: return false
    return beginPos.column <= colNr && endPos.column >= colNr
}

fun parse(file: File): CompilationUnit? {
    return try {
        StaticJavaParser.parse(file)
    } catch (e: ParseProblemException) {
        null
    }
}

fun truncate(file: File, lineNr: Int): List<Int> {
    val ast = parse(file) ?: return emptyList()
    val nodes = ast.findAll(Node::class.java, { inLine(it, lineNr) })
    return nodes.flatMap { columns(it) }.distinct()
}

fun bugInStr(file: File, lineNr: Int, colNr: Int): Boolean {
    val ast = parse(file) ?: return false
    val strings = ast.findAll(StringLiteralExpr::class.java, { inLine(it, lineNr) && hasColNr(it, colNr) })
    return strings.isNotEmpty()
}

fun main(args: Array<String>) {
    if(args[0] == "--bulk") {
        val dataFile = args[1]
        val prefix = args[2]

        File(dataFile).readLines()
            .map { it.split(" ") }
            .map { Triple(it[0], it[1], truncate(File(prefix, it[0]), it[1].toInt())) }
            .forEach { println("${it.first} ${it.second} ${it.third.joinToString(",")}") }
    } else if(args[0] == "--is-str") {
        val dataFile = args[1]
        val prefix = args[2]

        File(dataFile).readLines()
            .map { it.split(" ") }
            .forEach { if(bugInStr(File(prefix, it[0]), it[1].toInt(), it[2].toInt())) println(it[0]) }
    } else {
        val filePath = args[0]
        val lineNr = args[1].toInt()

        println(truncate(File(filePath), lineNr).joinToString(","))
    }
}
