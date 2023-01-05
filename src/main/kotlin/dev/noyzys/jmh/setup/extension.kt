package dev.noyzys.jmh.setup

fun Any.getResourceAsString(name: String): String = this::class.java.classLoader.getResourceAsStream(name)!!
    .bufferedReader()
    .use { it.readText() }
