package com.github.ajsnarr98.smartdoorcloser.util

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.abs

/**
 * Returns the number in the given list that is closest to this number.
 */
fun <T : Number> T.closest(numsToCheck: List<T>): T {
    if (numsToCheck.size == 0) throw IllegalArgumentException("given list cannot be empty")

    var closest = numsToCheck[0]
    var closestDist = abs(numsToCheck[0] - this)
    for (curNum in numsToCheck) {
        val dist = abs(curNum - this)
        if (closestDist > dist) {
            closest = curNum
        }
    }
    return closest
}

/**
 * Gets the absolute value of a type of Number.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Number> abs(num: T): T {
    // call respective built in Kotlin func
    return when (num) {
        is Int -> abs(num) as T
        is Long -> abs(num) as T
        is Float -> abs(num) as T
        is Double -> abs(num) as T
        is BigDecimal -> num.abs() as T
        is BigInteger -> num.abs() as T
        else -> throw IllegalStateException("Unknown Number type ${num.javaClass}")
    }
}

/**
 * Abstract (less efficient) overloading of subtraction operator.
 */
@Suppress("UNCHECKED_CAST")
operator fun <T : Number> Number.minus(dec: T): T {
    // call respective built in Kotlin func
    return when (this) {
        is Int -> if (dec is Int) (this - dec) as T else throw java.lang.IllegalArgumentException("Number types must match")
        is Long -> if (dec is Long) (this - dec) as T else throw java.lang.IllegalArgumentException("Number types must match")
        is Float -> if (dec is Float) (this - dec) as T else throw java.lang.IllegalArgumentException("Number types must match")
        is Double -> if (dec is Double) (this - dec) as T else throw java.lang.IllegalArgumentException("Number types must match")
        is BigDecimal -> (this - dec)
        is BigInteger -> (this - dec)
        else -> throw IllegalStateException("Unknown Number type ${this.javaClass}")
    }
}

/**
 * Abstract (less efficient) overloading of greater-than (and more) operator.
 */
operator fun <T : Number> Number.compareTo(dec: T): Int {
    // call respective built in Kotlin func
    // TODO - figure out if this method implementation causes segfault
    return when (this) {
        is Int -> if (dec is Int) this.compareTo(dec) else throw java.lang.IllegalArgumentException("Number types must match")
        is Long -> if (dec is Long) this.compareTo(dec) else throw java.lang.IllegalArgumentException("Number types must match")
        is Float -> if (dec is Float) this.compareTo(dec) else throw java.lang.IllegalArgumentException("Number types must match")
        is Double -> if (dec is Double) this.compareTo(dec) else throw java.lang.IllegalArgumentException("Number types must match")
        is BigDecimal -> this.compareTo(dec)
        is BigInteger -> this.compareTo(dec)
        else -> throw IllegalStateException("Unknown Number type ${this.javaClass}")
    }
}

