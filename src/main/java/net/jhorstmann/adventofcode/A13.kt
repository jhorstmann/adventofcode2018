package net.jhorstmann.adventofcode

import java.lang.IllegalStateException

data class Cart(var x : Int, var y: Int, var dir: Char, var intersectionsCrossed: Int = 0) {

    fun tick(world: CharArray, w: Int, h: Int) {
        val c = world[y * w + x]

        if (c == '+') {
            if (intersectionsCrossed % 3 == 0) {
                dir = leftTurn[dir]!!
            } else if (intersectionsCrossed % 3 == 2) {
                dir = rightTurn[dir]!!
            }
            intersectionsCrossed++
        } else {
            dir = trackRules[dir to c] ?: throw IllegalStateException("no track rules for track $c and dir $dir")
        }

        val (dx, dy) = moveRules[dir]!!
        x += dx
        y += dy


    }

    companion object {
        val rightTurn = mapOf(
                '^' to '>',
                '>' to 'v',
                'v' to '<',
                '<' to '^'
        )
        val leftTurn = mapOf(
                '^' to '<',
                '<' to 'v',
                'v' to '>',
                '>' to '^'
        )
        val moveRules = mapOf(
                '^' to (0 to -1),
                '<' to (-1 to 0),
                'v' to (0 to 1),
                '>' to (1 to 0)
        )
        val trackRules = mapOf(
                ('^' to '|') to '^',
                ('^' to '/') to '>',
                ('^' to '\\') to '<',
                ('>' to '-') to '>',
                ('>' to '/') to '^',
                ('>' to '\\') to 'v',
                ('v' to '|') to 'v',
                ('v' to '/') to '<',
                ('v' to '\\') to '>',
                ('<' to '-') to '<',
                ('<' to '/') to 'v',
                ('<' to '\\') to '^'
        )

    }
}

fun main(args: Array<String>) {

    val example1 = """
                /->-\
                |   |  /----\
                | /-+--+-\  |
                | | |  | v  |
                \-+-/  \-+--/
                  \------/
            """.trimIndent().split("\r?\n".toRegex()
    )

    val example2 = """
        />-<\
        |   |
        | /<+-\
        | | | v
        \>+</ |
          |   ^
          \<->/
    """.trimIndent().split("\r?\n".toRegex())

    //val lines =example2
    val lines = resourceLines("a13.txt")

    val w = lines.map { it.length }.max()!!
    val h = lines.size

    val world = CharArray(w * h) { ' ' }
    val carts = mutableListOf<Cart>()

    val transform = mapOf(
            '<' to '-',
            '>' to '-',
            '^' to '|',
            'v' to '|'
    )

    lines.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            val cart = transform[c]
            if (cart != null) {
                world[y * w + x] = cart
                carts += Cart(x, y, c)
            } else {
                world[y * w + x] = c
            }
        }
    }

    println(carts)

    //println(world.asSequence().chunked(w).joinToString("\n", transform = {it.joinToString("")}))
    printWorld(world, w, h, carts)
    println()

    val comparator = compareBy<Cart> { it.y }.thenBy { it.x }

    outer@while (true) {

        //printWorld(world, w, h, carts)

        for (cart in carts.sortedWith(comparator)) {
            //val (x, y, dir) = cart
            cart.tick(world, w, h)



            //println("$x, $y $dir -> ${cart.x}, ${cart.y} ${cart.dir}")

            val collisions = carts.groupBy { it.x to it.y }.values.filter { it.size > 1 }.flatten()
            for (collision in collisions) {
                println(collision.x to collision.y)

                carts.remove(collision)

                //break@outer // part 1

            }
        }
        if (carts.size == 1) {
            println(carts[0].x to carts[0].y)
            break@outer // part 2
        }
    }

}

private fun printWorld(world: CharArray, w: Int, h: Int, carts: List<Cart>) {
    val cartsByPos = carts.groupBy { it.x to it.y }
    for (y in 0 until h) {
        for (x in 0 until w) {
            val c = world[y*w + x]
            val cart = cartsByPos[x to y]
            when (cart?.size ?: 0) {
                0 -> print(c)
                1 -> print(cart!![0].dir)
                else -> print('X')
            }
        }
        println()
    }
}