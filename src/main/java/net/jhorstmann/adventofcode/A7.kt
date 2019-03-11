package net.jhorstmann.adventofcode

import java.lang.IllegalStateException
import java.util.*

val testData = listOf<String>(
        "Step C must be finished before step A can begin.",
        "Step C must be finished before step F can begin.",
        "Step A must be finished before step B can begin.",
        "Step A must be finished before step D can begin.",
        "Step B must be finished before step E can begin.",
        "Step D must be finished before step E can begin.",
        "Step F must be finished before step E can begin."
)


fun main(args: Array<String>) {
    val rx = """Step (\w) must be finished before step (\w) can begin\.""".toRegex()

    val dependencies = TreeMap<Char, Set<Char>>()
    val prerequisites = TreeMap<Char, Set<Char>>()
    val steps = TreeSet<Char>()

    resourceLines("a7.txt")
            //testData
            .forEach { line ->
                val match = rx.matchEntire(line) ?: throw IllegalStateException("Line $line did not match")

                val first = match.groupValues[1][0]
                val second = match.groupValues[2][0]

                steps.add(first)
                steps.add(second)

                dependencies.compute(first) { ch, set ->
                    (set ?: TreeSet()).plus(second)
                }
                prerequisites.compute(second) { ch, set ->
                    (set ?: TreeSet()).plus(first)
                }
            }

    println(dependencies)
    println(prerequisites)

    part1(steps, prerequisites)

    part2(steps, prerequisites)
    // expected 991


}

fun part2(steps: TreeSet<Char>, prerequisites: TreeMap<Char, Set<Char>>) {
    val remaining = TreeSet<Char>(steps)
    val started = mutableListOf<Char>()
    val running = mutableMapOf<Char, Int>()
    val finished = mutableSetOf<Char>()

    val workers = 5
    val timePerStep = 61

    var t = 0

    outer@while (!remaining.isEmpty() || !running.isEmpty()) {

        println(remaining)
        println(started)
        println(running)
        println(t)

        if (!running.isEmpty()) {
            val min = running.minBy { it.value }!!.value
            val tmp = running.filterValues { it == min }
            running.keys.removeAll(tmp.keys)
            finished.addAll(tmp.keys)
            t = min
        }

        while (!remaining.isEmpty() && running.size < workers) {
            val next = remaining.find { step ->
                finished.containsAll<Char>(prerequisites[step] ?: emptySet())
            } ?: continue@outer

            running.put(next, t + next.toInt() - 'A'.toInt() + timePerStep)

            remaining.remove(next)
            started.add(next)
        }
    }

    println(t)
}

private fun part1(steps: TreeSet<Char>, prerequisites: TreeMap<Char, Set<Char>>) {
    val remaining = TreeSet<Char>(steps)
    val started = mutableListOf<Char>()

    while (!remaining.isEmpty()) {
        val next = remaining.find { step ->
            started.containsAll<Char>(prerequisites[step] ?: emptySet())
        } ?: throw IllegalStateException("Could not find next step after $started")

        remaining.remove(next)
        started.add(next)
    }

    println(started.joinToString(""))
}