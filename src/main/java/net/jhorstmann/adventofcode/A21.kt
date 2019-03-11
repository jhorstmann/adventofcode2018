package net.jhorstmann.adventofcode

import it.unimi.dsi.fastutil.ints.IntOpenHashBigSet
import java.util.*

/*

r5 = 123
do {
    r5 &= 456
while (r5 != 72)

r5 = 0
6:
r4 = r5 | 0x10000 # r5  | 65536
r5 = 13284195
8:
r3 = r4 & 0xff
r5 += r3
r5 &= 0xffffff
r5 *= 65899
r5 &= 0xffffff
if (r4 <= 256) {
    //goto 28
    if (r5 != r0) {
        goto 6
    } else {
        return
    }
} // else goto 17
17:
r3 = 0
18:
r2 = r3 + 1
r2 *= 256
if (r2 <= r4) {
    r3 += 1
    goto 18
}
r4 = r3
goto 8

for (r3 = 0; (r3+1) * 256 <= r4; r3++) ;

 */

private typealias Operation3 = (Machine3, Int, Int, Int) -> Unit

private data class Op3(val opcode: String, val op: Operation3, val a: Int, val b: Int, val c: Int)

private data class Machine3(val registers: IntArray = IntArray(6), val ipreg : Int) {
    fun execute(ops: List<Op3>) : Int {
        val seen = BitSet()
        while (true) {
            val ip = registers[ipreg]
            if (ip < 0 || ip >= ops.size) {
                break
            }
            val op = ops[ip]

            if (ip == 17) {
                registers[4] = registers[4] / 256
                registers[ipreg] = 8
            } else {
                if (op.opcode == "eqrr" && op.a == 5 && op.b == 0) {
                    if (!seen[registers[5]]) {
                        seen[registers[5]] = true
                        println(registers[5])
                    }
                }
                op.op.invoke(this, op.a, op.b, op.c)
                registers[ipreg]++
            }
        }

        println(registers.toList())

        return registers[0]
    }
}

private val opcodes : Map<String, Operation3> = mapOf<String, Operation3>(
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

fun main(args : Array<String>) {
    //println((((13284195+1) and 0xffffff) * 65899) and 0xffffff)

    var ipreg = -1
    val operations = resourceLines("a21.txt")
            .mapNotNull {
                if (it.startsWith("#ip ")) {
                    ipreg = it.substringAfter("#ip ").toInt()
                    null
                } else {
                    val (op, a, b, c) = it.split(" ")
                    Op3(op, opcodes[op]!!, a.toInt(), b.toInt(), c.toInt())
                }
            }

    val m = Machine3(ipreg = ipreg)
    //m.registers[0] = 650
    m.execute(operations)
}