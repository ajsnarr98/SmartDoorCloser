package com.github.ajsnarr98.smartdoorcloser.util

var LOG_LEVEL: LogLevel = LogLevel.DEBUG

enum class LogLevel(val value: Int) {
    VERBOSE(0),
    DEBUG(1),
    INFO(2),
    ERROR(3);
}

