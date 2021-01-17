package com.github.ajsnarr98.smartdoorcloser

import java.io.InputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Converts the given [InputStream] to a string.
 */
fun getRequestString(stream: InputStream): String {
    val s = Scanner(stream).useDelimiter("\\A")
    return if (s.hasNext()) s.next() else ""
}

/**
 * Gets the current time (UTC) in the format '2017-02-03T16:20:50.52Z' .
 */
fun getTimeNow(): String {
    val formatter = DateTimeFormatter.ISO_INSTANT
    return LocalDate.now(ZoneOffset.UTC).format(formatter)
}
