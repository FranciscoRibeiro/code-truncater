import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Node
import java.io.File

fun truncate(filePath: String, lineNr: Int) {
    val ast = StaticJavaParser.parse(File(filePath))
    val nodes = ast.findAll(Node::class.java, { it.begin.get().line == lineNr && it.end.get().line == lineNr })
//    nodes.forEach { println(it.javaClass.simpleName); println(it) }
    val colNrs = nodes.flatMap { listOf(it.range.get().begin.column, it.range.get().end.column) }.distinct()
//    nodes.forEach { print("$it -> "); print("${it.range.get().begin.column} "); println(it.range.get().end.column) }
    colNrs.forEach { print("$it ") }
}

fun main(args: Array<String>) {
    val filePath = args[0]
    val lineNr = args[1].toInt()

    truncate(filePath, lineNr)
}
