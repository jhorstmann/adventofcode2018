package net.jhorstmann.adventofcode

import java.lang.IllegalStateException
import kotlin.collections.HashSet

private fun part1(): Int {
    val result = resourceLines("a1.txt")
            .asSequence()
            .map { it.toInt() }
            .fold(0) { a, v -> a + v }

    return result
}

private fun part2(): Int {
    val seen = HashSet<Int>()
    var acc = 0
    seen.add(acc)
    generateSequence { resourceLines("a1.txt").asSequence() }
            .flatten()
            .map { it.toInt() }
            .forEach {
                acc += it
                if (seen.contains(acc)) {
                    return acc
                }
                seen.add(acc)
            }

    throw IllegalStateException("No duplicate frequency")

}

fun main(args: Array<String>) {
    println(part1())
    println(part2())




}
