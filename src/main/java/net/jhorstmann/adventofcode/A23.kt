package net.jhorstmann.adventofcode

import java.lang.IllegalStateException
import kotlin.math.abs

private data class Bot(val x: Int, val y: Int, val z: Int, val r: Int)

fun main(args: Array<String>) {
    val rx = """pos=<(-?\d+),(-?\d+),(-?\d+)>, r=(\d+)""".toRegex()
    val bots = resourceLines("a23.txt")
            .map { line -> rx.matchEntire(line) ?: throw IllegalStateException("line $line did not match") }
            .map { it.groupValues.drop(1).map { it.toInt() }.let { Bot(it[0], it[1], it[2], it[3]) } }

    val l = bots.maxBy { it.r }!!

    val count = bots.count { abs(it.x - l.x) + abs(it.y - l.y) + abs(it.z - l.z) <= l.r }

    val xs = bots.map { it.x }
    val ys = bots.map { it.y }
    val zs = bots.map { it.z }

    println((xs.max()!! - xs.min()!!).toLong() * (ys.max()!! - ys.min()!!) * (zs.max()!! - zs.min()!!))

    println(listOf(bots.minBy { it.x }, bots.minBy { it.y }, bots.minBy { it.z }))
    println(listOf(bots.maxBy { it.x }, bots.maxBy { it.y }, bots.maxBy { it.z }))

    println(l)
    println(count)

}