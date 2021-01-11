package com.github.ajsnarr98.smartdoorcloser

import org.apache.logging.log4j.LogManager
import software.amazon.awssdk.crt.mqtt.MqttClientConnection
import software.amazon.awssdk.iot.iotshadow.IotShadowClient
import java.util.concurrent.CompletableFuture

/**
 * Represents this IoT device's shadow.
 */
class ShadowHandler(connection: MqttClientConnection, private val config: Config) {

    companion object {
        private val log = LogManager.getLogger()
    }

    private val shadowClient = IotShadowClient(connection)
    private val connected: CompletableFuture<Boolean> = connection.connect()

    init {
        try {
            val sessionPresent = connected.get()
            log.info("Connected to " + (if (!sessionPresent) "clean" else "existing") + " session!")
        } catch (ex: Exception) {
            throw RuntimeException("Exception occurred during connect", ex)
        }
    }
}