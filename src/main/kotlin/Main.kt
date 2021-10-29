import com.github.javaparser.ParseProblemException
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Node
import java.io.File

fun inLine(node: Node, lineNr: Int): Boolean {
    val beginPos = node.begin.orElse(null) ?: return false
    val endPos = node.end.orElse(null) ?: return false
    return beginPos.line == lineNr && endPos.line == lineNr
}

fun truncate(file: File, lineNr: Int): List<Int> {
    val ast = try {
        StaticJavaParser.parse(file)
    } catch (e: ParseProblemException) {
        return emptyList()
    }
    val nodes = ast.findAll(Node::class.java, { inLine(it, lineNr) })
    return nodes.flatMap { listOf(it.begin.get().column, it.end.get().column) }.distinct()
}

fun main(args: Array<String>) {
    if(args[0] == "--bulk") {
        val dataFile = args[1]
        val prefix = args[2]

        File(dataFile).readLines()
            .map { it.split(" ") }
            .map { Triple(it[0], it[1], truncate(File(prefix, it[0]), it[1].toInt())) }
            .forEach { println("${it.first} ${it.second} ${it.third.joinToString(",")}") }
    } else {
        val filePath = args[0]
        val lineNr = args[1].toInt()

        println(truncate(File(filePath), lineNr).joinToString { "," })
    }
}
