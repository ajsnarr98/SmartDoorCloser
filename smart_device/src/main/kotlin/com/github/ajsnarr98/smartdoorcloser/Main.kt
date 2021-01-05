package com.github.ajsnarr98.smartdoorcloser

import com.github.ajsnarr98.smartdoorcloser.db.DB
import com.github.ajsnarr98.smartdoorcloser.hardware.GPIO
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val log: Logger = LogManager.getLogger()

fun main(args: Array<String>) {

    log.debug("Beginning of main method")

    val db = DB()

//    if (GPIO.initialize()) {
//        log.info("Initialized GPIO")
//    } else {
//        log.fatal("Failed to initialize GPIO")
//        return
//    }

//    // add shutdown hook for making sure gpio is closed
//    Runtime.getRuntime().addShutdownHook(object : Thread() {
//        override fun run() {
//            log.info("Entering shutdown hook. Making sure GPIO is closed")
//            GPIO.close()
//        }
//    })

//    // use gpio and close at end of use
//    GPIO.use {

//    }
    log.debug("End of main method")
}
