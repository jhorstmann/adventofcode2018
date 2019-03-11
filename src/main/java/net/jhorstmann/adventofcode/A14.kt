package net.jhorstmann.adventofcode

import it.unimi.dsi.fastutil.ints.IntArrayList

private fun IntArrayList.lastElementsEquals(list: IntArrayList) : Boolean {
    return size > list.size && subList(size-list.size, size) == list
}



fun main(args: Array<String>) {
    part1()

    part2()

}

private fun part2() {
    val recipes = IntArrayList(listOf(3, 7))
    val elves = arrayOf(0, 1)
    val scores = IntArray(elves.size)
    val target = IntArrayList("920831".map { it.toInt() - '0'.toInt() })

    println(target)

    while (true) {
        //println(elves.toList())
        /*
        println(recipes.withIndex().joinToString("", transform = {
            when (it.index) {
                elves[0] -> "(${it.value})"
                elves[1] -> "[${it.value}]"
                else -> " ${it.value} "
            }
        }).trimEnd(' '))
        */

        for (e in 0 until elves.size) {
            scores[e] = recipes[elves[e]]
        }
        val sum = scores.sum()
        if (sum >= 10) {
            recipes.add(sum / 10)

            if (recipes.lastElementsEquals(target)) {
                break
            }
        }
        recipes.add(sum % 10)

        if (recipes.lastElementsEquals(target)) {
            break
        }

        for (e in 0 until elves.size) {
            elves[e] = (elves[e] + 1 + scores[e]) % recipes.size
        }
    }

    println(recipes.size - target.size)
}

private fun part1() {
    val recipes = IntArrayList(listOf(3, 7))
    val elves = arrayOf(0, 1)
    val scores = IntArray(elves.size)
    val iterations = 920831

    while (true) {
        //println(elves.toList())
        /*
        println(recipes.withIndex().joinToString("", transform = {
            when (it.index) {
                elves[0] -> "(${it.value})"
                elves[1] -> "[${it.value}]"
                else -> " ${it.value} "
            }
        }).trimEnd(' '))
        */

        for (e in 0 until elves.size) {
            scores[e] = recipes[elves[e]]
        }
        val sum = scores.sum()
        if (sum >= 10) {
            recipes.add(sum / 10)

            if (recipes.size == iterations + 10) {
                break
            }
        }
        recipes.add(sum % 10)

        if (recipes.size == iterations + 10) {
            break
        }

        for (e in 0 until elves.size) {
            elves[e] = (elves[e] + 1 + scores[e]) % recipes.size
        }
    }

    println(recipes.takeLast(10).joinToString("", transform = { it.toString() }))
}