package com.github.ajsnarr98.smartdoorcloser

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val log: Logger = LogManager.getLogger()

fun main(args: Array<String>) {

    log.debug("Beginning of main method")

//    if (GPIO.initialize()) {
//        log.info("Initialized GPIO")
//    } else {
//        log.fatal("Failed to initialize GPIO")
//        return
//    }

//    // use gpio and close at end of use
//    GPIO.use {

//    }
    log.debug("End of main method")
}
