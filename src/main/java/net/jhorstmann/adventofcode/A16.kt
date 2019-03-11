package net.jhorstmann.adventofcode

import java.util.*

private data class Machine(val registers: IntArray = IntArray(4)) {
    fun copy() = Machine(registers.clone())
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Machine

        if (!Arrays.equals(registers, other.registers)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(registers)
    }
}

private typealias Operation = (Machine, Int, Int, Int) -> Unit

private fun Operation.matchesInput(input: Input) : Boolean {
    val machine = input.before.copy()
    this(machine, input.a, input.b, input.c)
    return machine == input.after
}

private val opcodes : Map<String, Operation> = mapOf(
        "addr" to {m, a, b, c -> m.registers[c] = m.registers[a] + m.registers[b]},
        "addi" to {m, a, b, c -> m.registers[c] = m.registers[a] + b},
        "mulr" to {m, a, b, c -> m.registers[c] = m.registers[a] * m.registers[b]},
        "muli" to {m, a, b, c -> m.registers[c] = m.registers[a] * b},
        "banr" to {m, a, b, c -> m.registers[c] = m.registers[a] and m.registers[b]},
        "bani" to {m, a, b, c -> m.registers[c] = m.registers[a] and b},
        "borr" to {m, a, b, c -> m.registers[c] = m.registers[a] or m.registers[b]},
        "bori" to {m, a, b, c -> m.registers[c] = m.registers[a] or b},
        "setr" to {m, a, _, c -> m.registers[c] = m.registers[a]},
        "seti" to {m, a, _, c -> m.registers[c] = a},
        "gtir" to {m, a, b, c -> m.registers[c] = if (a > m.registers[b]) 1 else 0},
        "gtri" to {m, a, b, c -> m.registers[c] = if (m.registers[a] > b) 1 else 0},
        "gtrr" to {m, a, b, c -> m.registers[c] = if (m.registers[a] > m.registers[b]) 1 else 0},
        "eqir" to {m, a, b, c -> m.registers[c] = if (a == m.registers[b]) 1 else 0},
        "eqri" to {m, a, b, c -> m.registers[c] = if (m.registers[a] == b) 1 else 0},
        "eqrr" to {m, a, b, c -> m.registers[c] = if (m.registers[a] == m.registers[b]) 1 else 0}
)

private data class Input(val before: Machine, val after: Machine, val opcode: Int, val a: Int, val b: Int, val c: Int)

private fun parseState(line : String): Machine {
    val registers = line.substringAfter("[")
            .substringBefore("]")
            .split(", ")
            .map { it.toInt() }
            .toIntArray()
    return Machine(registers)
}

fun main(args: Array<String>) {
    val examples = resourceLines("a16_1.txt").chunked(4)
            .map {
                val (opcode, a, b, c) = it[1].split(" ").map { it.toInt() }
                Input(
                        parseState(it[0]),
                        parseState(it[2]),
                        opcode, a, b, c)
            }

    val part1 = examples.map { input -> opcodes.count { op -> op.value.matchesInput(input) } }
            .count { it >= 3 }

    println(part1)

    val examplesByCode = examples.groupBy { it.opcode }

    val opcodesById = examplesByCode.mapValuesTo(mutableMapOf()) { entry ->
        opcodes.filter { op ->
            entry.value.all { op.value.matchesInput(it) }
        }.map { it.key }.toMutableList()
    }

    while (!opcodesById.values.all { it.size == 1 }) {
        println(opcodesById)
        val unique = opcodesById.filter { it.value.size == 1 }.map { it.value[0] }
        unique.forEach { uniqueOp ->
            opcodesById.values.filter { it.size > 1 }.forEach {
                it.remove(uniqueOp)
            }
        }
    }
    println(opcodesById)
    println(opcodesById.size)

    val operationById = opcodesById.entries.associate { it.key to opcodes[it.value[0]]!! }

    val m = Machine()
    resourceLines("a16_2.txt")
            .forEach {
                val (op, a, b, c) = it.split(" ").map { it.toInt() }
                operationById[op]!!.invoke(m, a, b, c)
            }
    println(m.registers[0])

}