package net.jhorstmann.adventofcode


private object ResourceAnchor

fun resourceLines(resourceName : String) : List<String> {
    ResourceAnchor::class.java.getResourceAsStream(resourceName).bufferedReader().use {
        return it.readLines()
    }
}