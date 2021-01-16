package com.github.ajsnarr98.smartdoorcloser

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import software.amazon.awssdk.crt.CrtRuntimeException
import software.amazon.awssdk.crt.io.ClientBootstrap
import software.amazon.awssdk.crt.io.EventLoopGroup
import software.amazon.awssdk.crt.io.HostResolver
import software.amazon.awssdk.crt.mqtt.MqttClientConnection
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder
import java.util.concurrent.ExecutionException
import java.util.concurrent.Semaphore


const val EVENT_LOOP_THREADS = 1

val log: Logger = LogManager.getLogger()

fun main() {

    log.debug("Beginning of main method")

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

    // load config
    Config.instance.verify()
    val config = Config.instance

//    // use gpio and close at end of use
//    GPIO.use {
    useMqttClientConnection(
        config,
        onError = { exception -> log.error("Exception encountered: $exception") },
    ) { connection ->
        // Initialize shadow client and local copy of shadow
        ShadowHandler(connection, config).use { shadowHandler ->
            val sema = Semaphore(0) // initialize with no available permits
            shadowHandler.setCloseCommandListener { sema.release() }
            while (true) {
                // block until close command is issued via listener
                log.info("waiting for close command...")
                sema.acquire()
                log.warn("I got a close command and I don't know what to do!")
            }
        }
    }
//    }
    log.debug("End of main method")
}

/**
 * Initializes necessary objects for creating the connection in a try block,
 * and closes everything afterward.
 *
 * @param config Configuration options
 * @param usingConnection Code block using connection
 * @param onError Code block called on relevant errors
 */
private fun useMqttClientConnection(
    config: Config,
    onConnectionInterrupted: (errorCode: Int) -> Unit = {},
    onConnectionResumed: (sessionPresent: Boolean) -> Unit = {},
    onError: (e: Exception) -> Unit,
    usingConnection: (connection: MqttClientConnection) -> Unit,
) {
    var eventLoopGroup: EventLoopGroup? = null
    var resolver: HostResolver? = null
    var clientBootstrap: ClientBootstrap? = null
    var builder: AwsIotMqttConnectionBuilder? = null
    var connection: MqttClientConnection? = null
    try {
        eventLoopGroup = EventLoopGroup(EVENT_LOOP_THREADS)
        resolver = HostResolver(eventLoopGroup)
        clientBootstrap = ClientBootstrap(eventLoopGroup, resolver)
        builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(config.certPath, config.keyPath)

        if (config.rootCaPath != null) {
            builder.withCertificateAuthorityFromPath(null, config.rootCaPath);
        }

        builder.withClientId(config.clientId)
            .withEndpoint(config.endpoint)
            .withCleanSession(true)
            .withConnectionEventCallbacks(object : MqttClientConnectionEvents {
                override fun onConnectionInterrupted(errorCode: Int) {
                    onConnectionInterrupted(errorCode)
                }

                override fun onConnectionResumed(sessionPresent: Boolean) {
                    onConnectionResumed(sessionPresent)
                }

            })
            .withBootstrap(clientBootstrap);

        connection = builder.build()
        usingConnection(connection)

    } catch (e: Exception) {
        when (e) {
            is CrtRuntimeException, is InterruptedException, is ExecutionException -> onError(e)
            else -> throw e
        }
    } finally {
        eventLoopGroup?.close()
        resolver?.close()
        clientBootstrap?.close()
        builder?.close()
        connection?.close()
    }
}
