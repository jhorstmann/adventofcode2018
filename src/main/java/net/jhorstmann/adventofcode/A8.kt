package net.jhorstmann.adventofcode

import java.lang.IllegalStateException

data class Node(val children: MutableList<Node> = mutableListOf(), val metadata : MutableList<Int> = mutableListOf()) {
    fun recursiveMetadata() : Sequence<Int> {
        return metadata.asSequence() + children.asSequence().flatMap { it.recursiveMetadata() }
    }

    fun recursiveMetadataSum() : Int {
        return metadata.sum() + children.asSequence().map { it.recursiveMetadataSum() }.sum()
    }

    fun recursiveValue() : Int {
        if (children.isEmpty()) {
            return metadata.sum()
        } else {
            return metadata.asSequence()
                    .map { it - 1 }
                    .filter { it >= 0 && it < children.size }
                    .map { children[it].recursiveValue() }
                    .sum()
        }
    }
}

fun main(args: Array<String>) {
    val data = resourceLines("a8.txt")[0]
    //val data = "2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2"
    val rx = "[0-9]+".toRegex()

    val iterator = rx.findAll(data).map { it.value.toInt() }.iterator()
    val node = buildTree(iterator)

    if (iterator.hasNext()) {
        throw IllegalStateException("Did not consume whole input")
    }

    println(node)
    println(node.recursiveMetadata().sum())
    println(node.recursiveMetadataSum())
    println(node.recursiveValue())
}



private fun buildTree(data : Iterator<Int>) : Node {
    val node = Node()
    val childCount = data.next()
    val metadataCount = data.next()
    for (i in 0 until childCount) {
        node.children.add(buildTree(data))
    }
    for (i in 0 until metadataCount) {
        node.metadata.add(data.next())
    }
    return node;

}