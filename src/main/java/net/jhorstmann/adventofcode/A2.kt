package net.jhorstmann.adventofcode

private fun counts(line: String) : Map<Char, Int> {
    val counts = HashMap<Char, Int>()
    line.forEach { counts.compute(it) { _, old -> (old?:0) + 1} }
    return counts
}

private fun part1() {
    val counts = resourceLines("a2.txt")
            .map { counts(it) }

    val checksum = counts.count { it.containsValue(2) } * counts.count { it.containsValue(3) }

    println(checksum)
}

private fun part2() {
    val lines = resourceLines("a2.txt")

    for (line1 in lines) {
        for (line2 in lines) {
            if (line1 != line2 && line1.length == line2.length) {
                val zip = line1.zip(line2)
                        .filter { it.first == it.second }
                val differences = line1.length - zip.count()

                if (differences == 1) {
                    println(zip.map { it.first }.joinToString(""))
                }
            }
        }
    }


}

fun main(args: Array<String>) {
    part1()
    part2()

}
