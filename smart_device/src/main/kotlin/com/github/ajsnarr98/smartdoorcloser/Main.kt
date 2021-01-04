package com.github.ajsnarr98.smartdoorcloser

import com.github.ajsnarr98.smartdoorcloser.hardware.GPIO
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val log: Logger = LogManager.getLogger()

fun main(args: Array<String>) {

    log.debug("Beginning of main method")

//    val isInit = GPIO.initialize();
//    if (!isInit) {
//        log.error("Failed to initialize GPIO")
//        return
//    } else {
//        log.info("Initialized GPIO")
//    }

//    // use gpio and close at end of use
//    GPIO.use {

//    }
    log.debug("End of main method")
}
