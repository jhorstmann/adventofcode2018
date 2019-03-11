package net.jhorstmann.adventofcode

import it.unimi.dsi.fastutil.ints.IntArrayList
import java.lang.IllegalStateException
import java.util.NoSuchElementException

fun main(args: Array<String>) {
    //410 players; last marble is worth 72059 points
    val players = 410
    val lastmarble = 72059 * 100

    runCircularList(players, lastmarble)
    //runStandardList(players, lastmarble)
}

data class Marble(var value : Long, var next: Marble?, var prev: Marble?) : Iterable<Marble> {

    fun clockwise(steps : Int = 1) : Marble {
        var res : Marble = this
        for (i in 0 until steps) {
            res = res.next!!
        }
        return res
    }

    fun counterClockwise(steps : Int = 1) : Marble {
        var res : Marble = this
        for (i in 0 until steps) {
            res = res.prev!!
        }
        return res;
    }

    fun remove() : Long {
        if (next === this || prev == this) {
            throw IllegalStateException("Can not remove final Marble")
        }

        val currentNext = next!!
        val currentPrev = prev!!

        prev!!.next = currentNext
        next!!.prev = currentPrev

        return value
    }

    fun append(value : Long) : Marble {
        val currentNext = next!!
        val currentPrev = prev!!
        val newMarble = Marble(value, currentNext, this)

        next = newMarble
        currentNext.prev = newMarble

        if (currentPrev == this) {
            prev = newMarble
        }

        return newMarble
    }

    fun insert(value : Long) : Marble {
        val current = this
        val currentPrev = prev!!

        val newMarble = Marble(value, current, currentPrev)

        currentPrev.next = newMarble
        if (currentPrev.prev === this) {
            currentPrev.prev = newMarble
        }

        return newMarble
    }

    override fun iterator() : Iterator<Marble> {
        val start = this
        var current : Marble? = null
        return object : Iterator<Marble> {
            override fun hasNext(): Boolean {
                return current !== start
            }

            override fun next(): Marble {
                val res = current ?: start
                current = res.next ?: throw NoSuchElementException()

                return res
            }
        }
    }

    override fun toString() : String {
        return Sequence { this.iterator() }.toString()
    }
}

private fun runCircularList(players: Int, lastmarble: Int) {
    val scores = LongArray(players)

    var current = Marble(0,null, null)
    current.next = current
    current.prev = current

    val marbles = current

    for (i in 1..lastmarble) {

        if (i % 23 == 0) {
            val p = i % players
            val toBeRemoved = current.counterClockwise(7)
            current = toBeRemoved.clockwise(1)
            val removed = toBeRemoved.remove()
            scores[p] += (i + removed)
        } else {
            current = current.clockwise(1).append(i.toLong())
        }

        //println("" + i + "\t" + marbles.map { m -> if (m === current) "(${m.value})" else m.value }.joinToString(" "))
        //println("" + i + "\t" + current.map { m -> if (m === current) "(${m.value})" else m.value }.joinToString(" "))
        if (i % 1000 == 0) {
            print('.')
        }
        if (i % 100_000 == 0) {
            println()
        }

    }

    println()

    println(scores.toList())
    println(scores.max())
}

private fun runStandardList(players: Int, lastmarble: Int) {
    val circle = IntArrayList(lastmarble)
    val scores = LongArray(players)


    circle.add(0)
    var currentIndex = 0
    for (i in 1..lastmarble) {

        if (i % 23 == 0) {
            val p = i % players
            val removeIdx = (currentIndex + circle.size - 7) % (circle.size)
            val removed = circle.removeAt(removeIdx)
            scores[p] += (i + removed).toLong()
            currentIndex = removeIdx
        } else {
            val idx = (currentIndex + 1) % (circle.size) + 1
            circle.add(idx, i)
            currentIndex = idx
        }

        if (i % 1000 == 0) {
            print('.')
        }
        if (i % 100_000 == 0) {
            println()
        }

        //println("" + i + "\t" + circle.mapIndexed { j, v -> if (j == currentIndex) "($v)" else v }.joinToString(" "))
    }
    println()

    println(scores.toList())
    println(scores.max())
}