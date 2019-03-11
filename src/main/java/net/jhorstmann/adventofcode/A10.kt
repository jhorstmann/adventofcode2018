package net.jhorstmann.adventofcode

import java.lang.IllegalStateException
import kotlin.math.abs

// position=< 52484, -20780> velocity=<-5,  2>

data class LightPoint(var x: Int, var y: Int, val dx: Int, val dy: Int) {
    fun move() {
        x += dx
        y += dy
    }
}

fun main(args : Array<String>) {
    val rx = "position=<\\s*([-+]?\\d+),\\s*([-+]?\\d+)>\\s*velocity=<\\s*([-+]?\\d+),\\s*([-+]?\\d+)>".toRegex()
    val points = resourceLines("a10.txt")
            .map { line ->
                val match = rx.matchEntire(line) ?: throw IllegalStateException("Line $line did not match")
                val (x, y, dx, dy) = match.groupValues.slice(1..4).map { it.toInt() }
                LightPoint(x, y, dx, dy)
            }

    for (i in 0 until 100_000) {
        val minX = points.minBy { it.x }!!.x
        val maxX = points.maxBy { it.x }!!.x
        val minY = points.minBy { it.y }!!.y
        val maxY = points.maxBy { it.y }!!.y

        //val xhistogram = points.groupingBy { it.x }.eachCount()
        //val yhistogram = points.groupingBy { it.y }.eachCount()

        val w = abs(minX - maxX) + 1
        val h = abs(minY - maxY) + 1
        if (w < 100 && h < 50) {
            val bitmap = ByteArray(w * h)
            points.forEach {
                bitmap[(it.y - minY) * w + (it.x - minX)] = 1
            }
            bitmap.asSequence().chunked(w).forEach {
                println(it.map { if (it == 1.toByte()) '#' else ' ' }.joinToString(""))
            }
            println(i)
        }

        points.forEach { it.move() }
    }


}