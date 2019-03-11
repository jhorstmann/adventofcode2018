package net.jhorstmann.adventofcode

fun main(args: Array<String>) {

    val lines = resourceLines("a18.txt")
    val h = lines.size + 2
    val w = lines.map { it.length }.max()!! + 2

    val world = CharArray(w * h) { '.' }

    for (y in 1 until h-1) {
        for (x in 1 until w-1) {
            world[y * w + x] = lines[y-1][x-1]
        }
    }

    var current = world.clone()
    var next = CharArray(current.size)
    val indices = arrayOf(-w-1, -w, -w+1, -1, +1, +w-1, +w, +w+1)
    val seen = mutableMapOf<String, Int>()

    var iteration = 0
    val max = 1000000000
    while (iteration < max) {
        val currentStr = String(current)
        val previous = seen[currentStr]
        if (previous != null) {
            println("in iteration $iteration, seen this before in $previous")
            val diff = iteration-previous
            while (iteration < max-diff) {
                iteration += diff
            }
            //break
        }
        seen.put(currentStr, iteration)
        for (y in 1 until h-1) {
            for (x in 1 until w-1) {
                val idx = y*w +x
                val c = current[idx]
                var tree = 0
                var open = 0
                var lumber = 0
                for (i in indices) {
                    when (current[idx+i]) {
                        '|' -> tree++
                        '#' -> lumber++
                        '.' -> open++
                    }
                }
                next[idx] = when {
                    c == '.' -> if (tree >= 3) '|' else c
                    c =='|' -> if (lumber >= 3) '#' else c
                    c == '#' -> if (lumber >= 1 && tree >= 1) '#' else '.'
                    else -> throw IllegalStateException("Unknown terrain $c")
                }

                //print(next[idx])
            }
            //println()
        }
        //println()


        with (current) {
            current = next
            next = this
        }
        iteration++

    }

    for (y in 1 until h-1) {
        for (x in 1 until w - 1) {
            print(current[y*w + x])
        }
        println()
    }


    println(current.count { it == '|' } * current.count { it == '#' })

}