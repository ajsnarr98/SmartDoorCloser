package com.github.ajsnarr98.smartdoorcloser

import com.amazonaws.services.lambda.runtime.LambdaLogger

/**
 * Wraps a [LambdaLogger] and if that logger is null, just prints to standard out.
 */
internal class LoggerNonNull(private val logger: LambdaLogger?) : LambdaLogger {
    override fun log(msg: ByteArray?) = logger?.log(msg) ?: println(if(msg != null) String(msg) else "null")
    override fun log(msg: String?) = logger?.log(msg) ?: println(msg)
}