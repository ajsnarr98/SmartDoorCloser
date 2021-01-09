package com.github.ajsnarr98.smartdoorcloser

import kotlin.reflect.KClass

/**
 * Custom try-catch that mimics the java try-with-resources statement and
 * also supports catching multiple types of exceptions in one variable.
 *
 * Only supports one catch block.
 */
fun tryWithResources(vararg resources: AutoCloseable, tryBlock: () -> Unit): Catchable {
    return Catchable(resources, tryBlock)
}

class Catchable(private val resources: Array<out AutoCloseable>, private val tryBlock: () -> Unit) {
    fun catch(vararg exceptions: KClass<out Throwable>, catchBlock: (Throwable) -> Unit) {
        try {
            tryBlock()
        } catch (e: Throwable) {
            if (e::class in exceptions) catchBlock(e) else throw e
        } finally {
            resources.forEach { it.close() }
        }
    }
}
