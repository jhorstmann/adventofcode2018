package net.jhorstmann.adventofcode

private fun part1() {

    val fabric = IntArray(1024*1024)

    val regex = """#\d+ @ (\d+),(\d+): (\d+)x(\d+)""".toRegex()

    for (line in resourceLines("a3.txt")) {
        regex.matchEntire(line)?.let {
            val x = it.groupValues[1].toInt()
            val y = it.groupValues[2].toInt()
            val w = it.groupValues[3].toInt()
            val h = it.groupValues[4].toInt()

            for (i in x until x+w) {
                for (j in y until y+h) {
                    fabric[j*1024 + i]++
                }
            }
        }
    }

    println(fabric.filter { it > 1 }.count())

}


private data class Claim(val id: Int, val x: Int, val y: Int, val w: Int, val h: Int)

private fun part2() {

    val fabric = IntArray(1024 * 1024)

    val regex = """#(\d+) @ (\d+),(\d+): (\d+)x(\d+)""".toRegex()

    val claims = resourceLines("a3.txt")
            .asSequence()
            .map { line -> regex.matchEntire(line) }
            .filterNotNull()
            .map {
                Claim(it.groupValues[1].toInt(), it.groupValues[2].toInt(), it.groupValues[3].toInt(), it.groupValues[4].toInt(), it.groupValues[5].toInt())
            }
            .toList()

    for (claim in claims) {
        with(claim) {
            for (i in x until x + w) {
                for (j in y until y + h) {
                    fabric[j * 1024 + i]++
                }
            }
        }
    }
    outer@ for (claim in claims) {
            for (i in claim.x until claim.x + claim.w) {
                for (j in claim.y until claim.y + claim.h) {
                    if (fabric[j * 1024 + i] != 1) {
                        continue@outer
                    }
                }
            }
            println(claim.id)
    }

}

fun main(args: Array<String>) {
    part1()
    part2()
}
