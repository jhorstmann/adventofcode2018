package net.jhorstmann.adventofcode

private fun react(p: StringBuilder) {
    var changed: Boolean
    var i = 0
    do {
        changed = false
        val j = i + 1
        if (j < p.length && p[i].isLowerCase() != p[j].isLowerCase() && p[i].toLowerCase() == p[j].toLowerCase()) {
            p.replace(i, j + 1, "")
            //println(i)
            //println(p.subSequence(0, 120))
            changed = true
            if (i > 0) {
                i--
            }
        } else {
            i++
        }
    } while (changed || j < p.length)
}

fun main(args: Array<String>) {
    val data = resourceLines("a5.txt")

    val p = StringBuilder(data[0])
    react(p)
    println(p)
    println(p.length)

    val best = ('a'..'z').map {
        val rx = ("[" + it + it.toUpperCase() + "]+").toRegex()
        println(rx)
        val p = StringBuilder(data[0].replace(rx, ""))
        react(p)
        println(p)
        println(p.length)
        Pair(it, p.length)
    }.minBy { it.second }

    println(best)

}
