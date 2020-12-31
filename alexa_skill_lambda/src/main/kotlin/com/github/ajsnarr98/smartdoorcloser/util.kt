package com.github.ajsnarr98.smartdoorcloser

import java.io.InputStream
import java.util.*

/**
 * Converts the given [InputStream] to a string.
 */
fun getRequestString(stream: InputStream): String {
    val s = Scanner(stream).useDelimiter("\\A")
    return if (s.hasNext()) s.next() else ""
}
