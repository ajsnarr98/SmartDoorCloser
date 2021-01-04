package com.github.ajsnarr98.smartdoorcloser

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
