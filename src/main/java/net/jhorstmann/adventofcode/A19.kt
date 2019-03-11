package net.jhorstmann.adventofcode

import kotlin.math.min


private typealias Operation2 = (Machine2, Int, Int, Int) -> Unit

private data class Op(val opcode: String, val op: Operation2, val a: Int, val b: Int, val c: Int)

private data class Machine2(val registers: IntArray = IntArray(6), val ipreg : Int) {
    fun execute(ops: Map<Int, Op>) : Int {
        while (true) {
            val ip = registers[ipreg]

            //println(ip)
            val op = ops[ip] ?: break

            //println(ip to op.opcode)
            //if (op.opcode == "mulr" && op.a == 3 && op.b == 5 && op.c == 4) {
            //}
            op.op.invoke(this, op.a, op.b, op.c)
            /*
            if (m.registers[ipreg] != ip) {
                println("jump $ip -> ${m.registers[ipreg]}")
            }
            */
            //registers[ipreg]++

            //m.registers[m.ip]++
            //println(m.registers[m.ip] to m.registers[0])
        }

        println(registers.toList())

        return registers[0]
    }
}


private val opcodes : Map<String, Operation2> = mapOf<String, Operation2>(
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
).mapValues {
    fun(m: Machine2, a: Int, b: Int, c: Int) {
        it.value.invoke(m, a, b, c)
        m.registers[m.ipreg]++
    }
}



private val jumpOpcodes : Map<String, Operation2> = mapOf(
        "jmpi" to {m, _, _, c -> m.registers[m.ipreg] = c},
        "jmpr" to {m, _, _, c -> m.registers[m.ipreg] = m.registers[c] + 1},
        "jmpri" to {m, a, b, c -> m.registers[m.ipreg] = m.registers[a] + c},
        "ret"   to {m, _, _, _ -> m.registers[m.ipreg] = -1},
        "jmplerri" to {m, a, b, c -> if (m.registers[a] <= m.registers[b]) m.registers[m.ipreg] = c else m.registers[m.ipreg]+=3},
        "jmpnerri" to {m, a, b, c -> if (m.registers[a] != m.registers[b]) m.registers[m.ipreg] = c else m.registers[m.ipreg]+=3}
        //"jmpeqrri" to {m, a, b, c -> if (m.registers[a] == m.registers[b]) m.registers[m.ipreg] = c else Unit},
        //"jmpeqrrr" to {m, a, b, c -> if (m.registers[a] == m.registers[b]) m.registers[m.ipreg] = m.registers[c] else Unit},
)

private val extendedOpcodes : MutableMap<String, Operation2> = opcodes.mapValues {
    fun(m: Machine2, a: Int, b: Int, c: Int) {
        it.value.invoke(m, a, b, c)
        m.registers[m.ipreg]++
    }
}.toMutableMap().also { it.putAll(jumpOpcodes) }



fun main(args: Array<String>) {

    var ipreg = -1
    val originalOperations = resourceLines("a19.txt")
            .mapNotNull {
                if (it.startsWith("#ip ")) {
                    //Input2.SetIp(it.substringAfter("#ip ").toInt())
                    ipreg = it.substringAfter("#ip ").toInt()
                    null
                } else {
                    val (op, a, b, c) = it.split(" ")
                    Op(op, opcodes[op]!!, a.toInt(), b.toInt(), c.toInt())
                }
            }
    val operations = originalOperations
            // replace usage of instruction pointer with constants
            .mapIndexed {i, it ->
                if (it.opcode == "setr" && it.a == ipreg) {
                    Op("seti", opcodes["seti"]!!, i, 0, it.c)
                } else if (it.opcode == "addr" && it.a == ipreg) {
                    Op("addi", opcodes["addi"]!!, it.b, i, it.c)
                } else if (it.opcode == "addr" && it.b == ipreg) {
                    Op("addi", opcodes["addi"]!!, it.a, i, it.c)
                } else if (it.opcode == "mulr" && it.a == ipreg) {
                    Op("muli", opcodes["muli"]!!, it.b, i, it.c)
                } else if (it.opcode == "mulr" && it.b == ipreg) {
                    Op("muli", opcodes["muli"]!!, it.a, i, it.c)
                } else {
                    it
                }
            }
            .mapIndexed { i, it ->
                if (it.opcode == "seti" && it.c == ipreg) {
                    Op("jmpi", jumpOpcodes["jmpi"]!!, it.a+1, it.a+1, it.a+1)
                } else if (it.opcode == "setr" && it.c == ipreg) {
                    Op("jmpri", jumpOpcodes["jmpri"]!!, it.a, 1, 1)
                } else if (it.opcode == "addi" && it.a == ipreg && it.c == ipreg) {
                        Op("jmpi", jumpOpcodes["jmpi"]!!, i+it.b+1, i+it.b+1, i+it.b+1)
                //} else if (it.opcode == "addi" && it.b == ipreg && it.c == ipreg) {
                //    Op("jmpi", jumpOpcodes["jmpi"]!!, i + it.a + 1, i + it.a + 1, i + it.a + 1)
                } else if (it.opcode == "addi" && it.c == ipreg) {
                    Op("jmpri", jumpOpcodes["jmpri"]!!, it.a, it.b+1, it.b+1)
                } else if (it.opcode == "muli" && it.a == ipreg && i * it.b >= originalOperations.size) {
                    Op("ret", jumpOpcodes["ret"]!!, 0, 0, 0)
                } else {
                    it
                }
            }

    println(operations.withIndex().joinToString("\n") {
        "${it.index}:\t${it.value.opcode} ${it.value.a} ${it.value.b} ${it.value.c}"} )

    println()

    //val operationsWithExplicitJumps = operations.withIndex().associateBy({it.index}, {it.value})

    val operationsWithExplicitJumps = mutableMapOf<Int, Op>()
    var i = 0
    while (i < operations.size) {
        val ops = operations.subList(i, min(operations.size, i + 3))
        if (ops.size == 3 && ops[0].opcode in setOf("gtrr", "eqrr") && ops[1].opcode == "jmpri" && ops[2].opcode == "jmpi" && ops[0].c == ops[1].a && ops[1].b == i + 2) {
            if (ops[0].opcode == "gtrr") {
                operationsWithExplicitJumps[i] = Op("jmplerri", jumpOpcodes["jmplerri"]!!, ops[0].a, ops[0].b, ops[2].c)
            } else {
                operationsWithExplicitJumps[i] = Op("jmpnerri", jumpOpcodes["jmpnerri"]!!, ops[0].a, ops[0].b, ops[2].c)
            }
            i += 3
        } else {
            val it = operations[i]
            operationsWithExplicitJumps[i] = it
            i++
        }
    }

    println(operationsWithExplicitJumps.entries.joinToString("\n") {
        "${it.key}:\t${it.value.opcode} ${it.value.a} ${it.value.b} ${it.value.c}"} )

    //runPart(ipreg, operations)


    //val m = Machine2(ipreg = ipreg)
    //m.registers[0] = 1
    //println(m.execute(operationsWithExplicitJumps))

    //println(sumFactors(892))
    //println(sumFactors(10551292))
}

private fun sumFactors(a: Int) : Int {
    var sum = 0
    for (i in 1..a) {
        if (a % i == 0) {
            sum += i
        }
    }
    return sum
}