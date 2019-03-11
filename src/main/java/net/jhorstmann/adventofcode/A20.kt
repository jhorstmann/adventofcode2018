package net.jhorstmann.adventofcode

import java.io.IOException
import java.io.PushbackReader
import java.io.StringReader
import kotlin.math.abs
import kotlin.math.min

sealed class RX {

    data class Atom(val value: Char) : RX() {
        override fun toString(): String {
            return value.toString()
        }
    }
    data class Sequence(val values: List<RX>) : RX() {
        override fun toString(): String {
            return values.joinToString("")
        }
    }
    data class Choice(val choices: List<RX>) : RX() {
        override fun toString(): String {
            return choices.joinToString("|", prefix = "(", postfix = ")")
        }
    }
}

private fun parseChoice(reader: PushbackReader) : RX {
    val choices: MutableList<RX> = mutableListOf()
    reader.consume('(')
    while (true) {
        val element = parseSequence(reader)
        choices.add(element)
        val ch = reader.peek()
        if (ch == ')'.toInt()) {
            reader.consume()
            break
        } else {
            reader.consume('|')
        }
    }
    return RX.Choice(choices)
}

private fun parseSequence(reader: PushbackReader) : RX.Sequence {
    val values: MutableList<RX> = mutableListOf()

    while (true) {
        val next = reader.peek()
        if (next == -1 || next == ')'.toInt() || next == '|'.toInt()) {
            break
        } else if (next == '^'.toInt() || next == '$'.toInt()) {
            reader.consume()
            continue
        } else if (next == '('.toInt()) {
            val element: RX = parseChoice(reader)
            values.add(element)
        } else {
            reader.consume()
            values.add(RX.Atom(next.toChar()))
        }
    }

    return RX.Sequence(values)
}

private fun parseRX(reader: PushbackReader) : RX {
    return parseSequence(reader)
}

private fun PushbackReader.consume() {
    val ch = read()
    if (ch == -1) {
        throw IOException("Unexpected end of input")
    }
}
private fun PushbackReader.consume(expected: Char) {
    val ch = read()
    if (ch == -1) {
        throw IOException("Unexpected end of input")
    } else if (ch.toChar() != expected) {
        throw IOException("Unexpected char ${ch.toChar()}")
    }
}

private fun PushbackReader.isEOF() : Boolean {
    val ch = read()
    if (ch == -1) {
        return true
    } else {
        unread(ch)
        return false
    }
}

private fun PushbackReader.peek() : Int {
    val ch = read()
    if (ch == -1) {
        return -1
    } else {
        unread(ch)
        return ch
    }
}

private val directions = mapOf(
        'N' to (0 to -1),
        'W' to (-1 to 0),
        'E' to (1 to 0),
        'S' to (0 to 1)
)

private data class Location(var x: Int, var y: Int)

private data class Distance(var d: Int)

private fun walkAtom(element: RX.Atom, location: Location, d: Distance, map: MutableMap<Location, Int>) {
    val move = directions[element.value]!!

    location.x += move.first
    location.y += move.second

    d.d = map.compute(location.copy()) { c, v -> min(d.d + 1, v ?: Int.MAX_VALUE) }!!
}

private fun walkChoice(rx: RX.Choice, location: Location, d: Distance, map: MutableMap<Location, Int>) {

    for (element in rx.choices) {
        when (element) {
            is RX.Atom -> {
                walkAtom(element, location.copy(), d.copy(), map)
            }
            is RX.Choice -> {
                walkChoice(element, location.copy(), d.copy(), map)
            }
            is RX.Sequence -> {
                walkSequence(element, location.copy(), d.copy(), map)
            }
        }
    }
}

private fun walkSequence(rx : RX.Sequence, location: Location, d : Distance, map: MutableMap<Location, Int>) {

    for (element in rx.values) {
        when (element) {
            is RX.Atom -> {
                walkAtom(element, location, d, map)
            }
            is RX.Choice -> {
                walkChoice(element, location, d, map)
            }
            is RX.Sequence -> {
                walkSequence(rx, location, d, map)
            }
        }
    }
}

fun main(args: Array<String>) {
    val input = resourceLines("a20.txt")[0]
    //val input = "^WNE\$"
    //val input = "^ENWWW(NEEE|SSE(EE|N))\$"
    //val input = "^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))\$"
    val data = PushbackReader(StringReader(input))
    val rx = data.use { parseSequence(it) }

    println(rx)

    val map = mutableMapOf<Location, Int>()
    walkSequence(rx, Location(0, 0), Distance(0), map)
    println(map)

    println(map.entries.sortedByDescending { it.value })
    println(map.entries.maxBy { it.value })
    println(map.entries.count { it.value >= 1000 })

}