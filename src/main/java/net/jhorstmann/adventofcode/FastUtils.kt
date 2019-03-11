package net.jhorstmann.adventofcode

import it.unimi.dsi.fastutil.ints.IntArrayList

operator fun IntArrayList.get(i: Int): Int {
    return this.getInt(i)
}

private fun test() {
    val list = IntArrayList(listOf(1, 2, 3))
    list.get(0)
    println(list[0])
}