package com.github.ajsnarr98.smartdoorcloser

import com.github.ajsnarr98.smartdoorcloser.hardware.GPIO

fun main(args: Array<String>) {
    val isInit = GPIO.initialize();
    if (!isInit) {
        println("Failed to initialize GPIO")
        return
    } else {
        println("Initialized GPIO")
    }

    GPIO.close()
}
