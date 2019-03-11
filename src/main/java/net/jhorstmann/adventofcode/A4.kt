package net.jhorstmann.adventofcode

import java.lang.IllegalStateException


// file is presorted

val date = """\[(\d+)-(\d+)-(\d+) (\d+):(\d+)]"""
val rx = """$date (?:Guard #(\d+) begins shift|(falls asleep)|(wakes up))""".toRegex()

data class Date(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int)

private fun parseDate(match : MatchResult) : Date {
    match.groupValues.let {
        return Date(it[1].toInt(), it[2].toInt(), it[3].toInt(), it[4].toInt(), it[5].toInt())
    }
}

data class Sleep(val start: Date, val end: Date)

data class Guard(val id : Int, val sleep: MutableList<Sleep> = mutableListOf()) {
    fun sleepMinutes(): Int {
        return sleep.fold(0) { acc, sleep ->
            acc + sleep.end.minute - sleep.start.minute
        }
    }

    fun timeline(): IntArray {
        val timeline = IntArray(60)

        for (sleep in this.sleep) {
            for (i in sleep.start.minute until sleep.end.minute) {
                timeline[i]++
            }
        }
        return timeline
    }

}



fun main(args: Array<String>) {
    val resourceLines = resourceLines("a4.txt")

    var guard: Int? = 0
    val guards = HashMap<Int, Guard>()
    var start: Date? = null
    for (i in 0 until resourceLines.size) {
        val line = resourceLines[i];
        val match = rx.matchEntire(line) ?: throw IllegalStateException("line $line did not match rx")
        val beginShift = match.groups[6]
        val asleep = match.groups[7]
        val wakeeup = match.groups[8]
        if (beginShift != null) {
            guard = beginShift.value.toInt()
            guards.putIfAbsent(guard, Guard(guard))
        } else if (asleep != null) {
            start = parseDate(match)
        } else if (wakeeup != null) {
            val end = parseDate(match)
            if (guard == null) {
                throw IllegalStateException("No guard started a shift yet")
            }
            if (start == null) {
                throw IllegalStateException("Guard $guard was not asleep in line i")
            }
            guards.computeIfAbsent(guard) { Guard(guard) }.sleep.add(Sleep(start, end))
        }
    }

    println(guards)

    //println(guards.values.map { Pair(it.id, it.sleepMinutes()) }.sortedByDescending { it.second }.joinToString("\n"))

    // part1
    val sleepyGuard = guards.values.maxBy { it.sleepMinutes() }
    if (sleepyGuard != null) {

        println(sleepyGuard)

        val timeline = sleepyGuard.timeline()

        val sleepyMinute = timeline.withIndex().maxBy { it.value }?.index
        println(timeline.toList())
        println(sleepyMinute)

        println(sleepyGuard.id * (sleepyMinute?:0))
    }


    val part2 = guards.values.map { Pair(it.id, it.timeline()) }
            .flatMap { it.second.withIndex().map { t -> Pair(it.first, t) } }
            .maxBy { it.second.value }
    println(part2)
    part2?.let { println(it.first * it.second.index) }

}

