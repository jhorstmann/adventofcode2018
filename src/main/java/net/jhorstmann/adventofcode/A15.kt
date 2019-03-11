package net.jhorstmann.adventofcode

import java.lang.Exception
import kotlin.math.abs

sealed class Action() {
    data class Move(val x: Int, val y: Int) : Action()
    data class Attack(val targets: List<Combatant>): Action()
    object None : Action()
}



data class Combatant(var x: Int, var y: Int, val type: Char, var hp: Int = 200, val power : Int = 3) {

    companion object {
        val moves = listOf(Pair(-1, 0), Pair(+1, 0), Pair(0, -1), Pair(0, +1))
    }

    fun findInRange(world: World) : List<Combatant> {

        val enemies = world.combatants.filter { it.type != type && it.hp > 0 }
        val byPos = enemies.associateBy { Pair(it.x, it.y) }

        val inAttackRange = moves
                .asSequence()
                .map { Pair(x + it.first, y + it.second) }
                .filter { it.first in 0 until world.w && it.second in 0 until world.h }
                .mapNotNull { byPos[it] }
                .sortedByDescending { it.hp }
                .toList()

        return inAttackRange
    }

    fun findMoveToNearestEnemy(world: World) : Pair<Int, Int>? {
        val enemies = world.combatants.filter { it.type != type && it.hp > 0 }
        val map = world.mapWithCombatants()

        val distances = IntArray(map.size) { Int.MAX_VALUE }
        floodfill(map, distances, world.w, world.h, x, y)

        val enemyDistances = enemies
                .mapNotNull { c ->
                    moves.asSequence()
                            .map { Pair(c.x + it.first, c.y + it.second) }
                            .filter { it.first in 0 until world.w && it.second in 0 until world.h }
                            .filter { map[it.second * world.w + it.first] == '.' }
                            .map { Pair(c, distances[it.second * world.w + it.first]) }
                            .minBy { it.second }
                }


        val nearest = enemyDistances
                .filter { it.second < Int.MAX_VALUE }
                .minWith(compareBy<Pair<Combatant, Int>> { it.second }.thenBy { it.first.y }.thenBy { it.first.x })

        if (nearest == null) {
            return null
        } else {
            val (nearestEnemy, d) = nearest
            val reverseDistances = IntArray(map.size) { d + 1 }
            floodfill(map, reverseDistances, world.w, world.h, nearestEnemy.x, nearestEnemy.y)

            //world.printWorld(reverseDistances)

            val (move, _) = moves
                    .asSequence()
                    .map { Pair(x + it.first, y + it.second) }
                    .filter { it.first in 0 until world.w && it.second in 0 until world.h }
                    .map { Pair(it, reverseDistances[it.second * world.w + it.first]) }
                    .minWith(compareBy<Pair<Pair<Int, Int>, Int>> { it.second }.thenBy { it.first.second }.thenBy { it.first.first })
                    ?: throw IllegalStateException("nearest enemy should be reachable")

            return move
        }
    }

    private fun floodfill(map: CharArray, distances: IntArray, w: Int, h: Int, x: Int, y: Int, d: Int = 0) {
        val i = y * w + x
        distances[i] = d
        if (x > 0 && map[i - 1] == '.' && d+1 < distances[i - 1]) {
            floodfill(map, distances, w, h, x-1, y, d+1)
        }
        if (x < w-1 && map[i + 1] == '.' && d+1 < distances[i + 1]) {
            floodfill(map, distances, w, h, x+1, y, d+1)
        }
        if (y > 0 && map[i - w] == '.' && d+1 < distances[i - w]) {
            floodfill(map, distances, w, h, x, y-1, d+1)
        }
        if (y < h-1 && map[i + w] == '.' && d+1 < distances[i + w]) {
            floodfill(map, distances, w, h, x, y+1, d+1)
        }
    }

}

data class World(val map: CharArray, val w: Int, val h: Int, val combatants: MutableList<Combatant>) {
    fun mapWithCombatants() : CharArray {
        val result = CharArray(map.size)
        val byPos = combatants.filter { it.hp > 0 }.associateBy { Pair(it.x, it.y) }

        for (y in 0 until h) {
            for (x in 0 until w) {
                result[y * w + x] = byPos[Pair(x, y)]?.type ?: map[y * w + x]
            }
        }
        return result
    }

    fun printWorld(distances: IntArray? = null) {
        val byPos = combatants.groupBy { Pair(it.x, it.y) }
        val byY = combatants.groupBy { it.y }

        for (y in 0 until h) {
            for (x in 0 until w) {
                val c = map[y * w + x]
                val combatant = byPos[Pair(x, y)]
                if (combatant != null) {
                    if (c == '#') {
                        throw IllegalStateException("Unit overlapping a wall")
                    }
                    if (combatant.size > 1) {
                        throw IllegalStateException("Overlapping units $combatant")
                    }
                    print(combatant[0].type)
                } else if (c == '.' && distances != null) {
                    val d = distances[y * w + x]
                    if (d <= 9) {
                        print(('0'+d))
                    } else {
                        print(c)
                    }
                } else {
                    print(c)
                }
            }
            byY[y]?.let{ c ->
                val info = c.sortedBy { it.x }.map { "${it.type}(${it.hp})" }.joinToString(", ", prefix = "   ")

                print(info)
            }
            println()
        }
        println()
    }

    private fun isDone() : Boolean {
        return combatants.groupingBy { it.type }.eachCount().size < 2
    }

    fun runPart1() {
        var rounds = 0
        while (!isDone()) {
            println("Round $rounds")
            val completed = runRoundPart1()
            printWorld()
            if (completed) {
                rounds++
            }
        }

        println(rounds * combatants.sumBy { it.hp })
    }

    fun runRoundPart1() : Boolean {
        val sorted = combatants.sortedWith( compareBy<Combatant> {it.y}.thenBy{it.x})

        for ((i, c) in sorted.withIndex()) {
            if (c.hp > 0) {
                val inRange = c.findInRange(this)
                if (inRange.isNotEmpty()) {
                    inRange.minBy { it.hp }?.let { it.hp -= c.power }
                    combatants.removeIf { it.hp <= 0 }
                    if (isDone() && i < combatants.size-1) {
                        return false
                    }
                } else {
                    val move = c.findMoveToNearestEnemy(this)
                    if (move != null) {
                        if (abs(c.x - move.first) + abs(c.y - move.second) != 1) {
                            throw java.lang.IllegalStateException("invalid move (${c.x}, ${c.y}) -> (${move.first}, ${move.second})")
                        }
                        c.x = move.first
                        c.y = move.second

                        val inRangeAfterMove = c.findInRange(this)
                        if (inRangeAfterMove.isNotEmpty()) {
                            inRangeAfterMove.minBy { it.hp }?.let { it.hp -= c.power }
                            combatants.removeIf { it.hp <= 0 }
                            if (isDone()  && i < combatants.size-1) {
                                return false
                            }
                        }

                    }
                }
            }
        }

        return true
    }

    fun runPart2() {
        val originalCombatants = combatants.map { it.copy() }
        val originalMap = map.copyOf()

        var elfPower = 3

        while (true) {
            System.arraycopy(originalMap, 0, map, 0, originalMap.size)
            combatants.clear()
            combatants.addAll(originalCombatants.map { if (it.type == 'G') it.copy() else it.copy(power = elfPower) })

            var rounds = 0

            println("Running with power of $elfPower")

            try {

                while (!isDone()) {
                    println("Round $rounds")
                    val completed = runRoundPart2()
                    printWorld()
                    if (completed) {
                        rounds++
                    }
                }

                println(rounds * combatants.sumBy { it.hp })
                break
            } catch (e : ElfDiedException) {
                println("Elf died, increasing power")
                elfPower++
            }
        }

    }

    object ElfDiedException : Exception("Elf died")

    fun runRoundPart2() : Boolean {
        val sorted = combatants.sortedWith( compareBy<Combatant> {it.y}.thenBy{it.x})

        for ((i, c) in sorted.withIndex()) {
            if (c.hp > 0) {
                val inRange = c.findInRange(this)
                if (inRange.isNotEmpty()) {
                    inRange.minBy { it.hp }?.let {
                        it.hp -= c.power
                        if (it.hp <= 0 && it.type == 'E') {
                            throw ElfDiedException
                        }
                    }
                    combatants.removeIf { it.hp <= 0 }
                    if (isDone() && i < combatants.size-1) {
                        return false
                    }
                } else {
                    val move = c.findMoveToNearestEnemy(this)
                    if (move != null) {
                        if (abs(c.x - move.first) + abs(c.y - move.second) != 1) {
                            throw java.lang.IllegalStateException("invalid move (${c.x}, ${c.y}) -> (${move.first}, ${move.second})")
                        }
                        c.x = move.first
                        c.y = move.second

                        val inRangeAfterMove = c.findInRange(this)
                        if (inRangeAfterMove.isNotEmpty()) {
                            inRangeAfterMove.minBy { it.hp }?.let {
                                it.hp -= c.power
                                if (it.hp <= 0 && it.type == 'E') {
                                    throw ElfDiedException
                                }
                            }
                            combatants.removeIf { it.hp <= 0 }
                            if (isDone()  && i < combatants.size-1) {
                                return false
                            }
                        }

                    }
                }
            }
        }

        return true
    }


    companion object {
        fun parse(name: String, attackPowers: Map<Char, Int> = emptyMap(), defaultHP : Int = 200, defaultAttackPower: Int = 3): World {
            val lines = resourceLines(name)
            val w = lines.map { it.length }.max()!!
            val h = lines.size

            val map = CharArray(w * h)
            val combatants = mutableListOf<Combatant>()

            for (y in 0 until h) {
                for (x in 0 until w) {
                    val c = lines[y][x]
                    map[y * w + x] = when (c) {
                        '#', '.' -> c
                        'E', 'G' -> {
                            combatants.add(Combatant(x, y, c, defaultHP, attackPowers[c] ?: defaultAttackPower))
                            '.'
                        }
                        else -> '.'
                    }
                }
            }

            return World(map, w, h, combatants)
        }
    }

}


fun main(args: Array<String>) {

    val world = World.parse("a15.txt")
    world.printWorld()
    world.runPart2()
}