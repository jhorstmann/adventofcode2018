package net.jhorstmann.adventofcode


fun main(args: Array<String>) {

    val lines = resourceLines("a12.txt")
    /*
    val lines = """initial state: #..#.#..##......###...###

...## => #
..#.. => #
.#... => #
.#.#. => #
.#.## => #
.##.. => #
.#### => #
#.#.# => #
#.### => #
##.#. => #
##.## => #
###.. => #
###.# => #
####. => #""".split("\r?\n".toRegex()).map { it.trim() }
*/

    val initial = lines[0].substringAfter("initial state: ").map { if (it == '#') true else false }.toBooleanArray()

    val rules = BooleanArray(1 shl 5)
    lines.subList(2, lines.size)
            .map { it.split(" => ") }
            .forEach {
                val pattern = it[0].mapIndexed { i, c -> (if (c == '#') 1 else 0) shl i }.sum()
                val result = if (it[1][0] == '#') true else false
                rules[pattern] = result

            }

    val iterations = 20
    var world = BooleanArray(2 * iterations + initial.size + 2 + 2)
    var next = BooleanArray(world.size)

    System.arraycopy(initial, 0, world, iterations, initial.size)

    println(world.joinToString("", transform = { if (it) "#" else "." }))

    for (i in 1..iterations) {
        for (x in 2 until world.size - 2) {
            val pattern = (0 until 5).map { (if (world[x - 2 + it]) 1 else 0) shl it }.sum()
            next[x] = rules[pattern]
        }
        System.arraycopy(next, 0, world, 0, world.size)
        println(world.joinToString("", transform = { if (it) "#" else "." }))
        println(world.mapIndexed { i, b -> if (b) i - iterations else 0 }.sum())
    }

    //println(world.mapIndexed { i, b -> if (b) i - iterations else 0 }.sum())

}