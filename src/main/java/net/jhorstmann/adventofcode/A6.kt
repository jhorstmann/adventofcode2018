package net.jhorstmann.adventofcode

import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


private val testDataA6 = listOf<String>(
        "1, 1",
        "1, 6",
        "8, 3",
        "3, 4",
        "5, 5",
        "8, 9"
)

private val chars = ('.'..'.').plus ('a' .. 'z').plus('A' .. 'Z')

private fun IntArray.printGrid(w: Int) {
    println(this.toList().chunked(w) {
        c -> c.map { chars[it+1] }.joinToString ("")
    }.joinToString("\n"))
}

fun main(args: Array<String>) {
    val coordinates =
    //testDataA6
            resourceLines("a6.txt")
                    .map { line -> line.split(",\\s+".toRegex()).let { Pair(it[0].toInt(), it[1].toInt()) } }


    val minX = coordinates.map { it.first }.min()!!
    val minY = coordinates.map { it.second }.min()!!
    val maxX = coordinates.map { it.first }.max()!!
    val maxY = coordinates.map { it.second }.max()!!

    val w = max(maxX, maxY) + 1
    val h = max(maxX, maxY) + 1

    println(Pair(w, h))

    //part1(coordinates, w, h)
    part2(coordinates, w, h)



}

fun part2(coordinates: List<Pair<Int, Int>>, w: Int, h: Int) {
    val world = IntArray(w * h) { -1 }

    for (y in 0 until h) {
        for (x in 0 until w) {
            val sumDistance = coordinates.map { c -> abs(c.first - x) + abs(c.second - y) }
                    .sum()
            world[y * w + x] = if (sumDistance < 10_000) 0 else -1
        }
    }

    world.printGrid(w)
    println(world.count { it == 0 })
}

fun part1(coordinates: List<Pair<Int, Int>>, w: Int, h: Int) {
    val world = IntArray(w * h) { -1 }

    for (y in 0 until h) {
        for (x in 0 until w) {
            val distances = coordinates.mapIndexed { i, c -> Pair(i, abs(c.first - x) + abs(c.second - y)) }
            val min = distances.minBy { it.second }!!

            val closest = distances.filter { it.second == min.second }

            if (closest.size == 1) {
                world[y * w + x] = closest[0].first
            } else {
                world[y * w + x] = -1
            }
        }
    }

    world.printGrid(w)

    val coordinatesById = coordinates.mapIndexed { i, c -> Pair(i, c) }
            .associate { it }
            .toMutableMap()

    for (x in 0 until w) {
        coordinatesById.remove(world[x])
        coordinatesById.remove(world[(h - 1) * w + x])
    }

    for (y in 0 until h) {
        coordinatesById.remove(world[y * w])
        coordinatesById.remove(world[y * w + w - 1])
    }

    println()
    println(coordinatesById)

    val biggest = coordinatesById.entries.map { entry ->
        world.count { it == entry.key }
    }

    println(biggest.max())
}