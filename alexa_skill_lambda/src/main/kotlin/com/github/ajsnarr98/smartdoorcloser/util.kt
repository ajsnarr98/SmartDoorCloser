package com.github.ajsnarr98.smartdoorcloser

import com.google.gson.Gson
import java.io.InputStream
import java.util.*

/**
 * Convert this object to json using a [Gson] instance.
 */
fun Any.toJson(gson: Gson) = gson.toJson(this)

/**
 * Converts the given [InputStream] to a string.
 */
fun getRequestString(stream: InputStream): String {
    val s = Scanner(stream).useDelimiter("\\A")
    return if (s.hasNext()) s.next() else ""
}
