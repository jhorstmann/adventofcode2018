package net.jhorstmann.adventofcode

private fun powerLevel(x: Int, y: Int, serial: Int): Int {
    val rack = x + 10
    return ((rack * y + serial) * rack / 100 % 10) - 5
}

fun main(args : Array<String>) {
    val w = 300
    val h = 300
    val grid = IntArray(w*h)
    val serial = 18

    for (y in 0 until h) {
        for (x in 0 until w) {
            grid[y*w + x] = powerLevel(x+1, y+1, serial)
        }
    }

    //println(powerLevel(3, 5, 8))
    //println(powerLevel(122, 79, 57))
    //println(powerLevel(217, 196, 39))

    val gridSum = IntArray(w*h)
    var sum = 0
    for (y in 0 until h) {
        for (x in 0 until w) {
            sum += grid[y*w + x]
            gridSum[y*w + x] = sum
        }
    }

    var max = Integer.MIN_VALUE
    var maxX = -1
    var maxY = -1

    for (y in 0 until h - 3) {
        for (x in 0 until w-3) {
            val sum = gridSum[(y+0)*w + x + 3] - gridSum[(y+0)*w + x]
                    + gridSum[(y+1)*w + x + 3] - gridSum[(y+1)*w + x]
                    + gridSum[(y+2)*w + x + 3] - gridSum[(y+2)*w + x]
            if (sum > max) {
                max = sum
                maxX = x + 1
                maxY = y + 1
            }
        }
    }
    println(maxX)
    println(maxY)
    println(max)






        }