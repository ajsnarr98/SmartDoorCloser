package com.github.ajsnarr98.smartdoorcloser

import com.github.ajsnarr98.smartdoorcloser.hardware.GPIO
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

//    // use gpio and close at end of use
//    GPIO.use {
    useMqttClientConnection(
        thingName = "",
        clientId = "",
        endpoint = "",
        certPath = "",
        keyPath = "",
        onError = { exception -> log.error("Exception encountered: $exception") },
    ) { connection ->

    }
//    }
    log.debug("End of main method")
}

/**
 * Initializes necessary objects for creating the connection in a try block,
 * and closes everything afterward.
 *
 * @param thingName The name of the IoT thing
 * @param clientId The client ID to use when connecting
 * @param endpoint AWS IoT service endpoint hostname
 * @param certPath Path to the IoT thing certificate
 * @param keyPath Path to the IoT thing private key
 * @param rootCaPath Path to the root certificate (optional)
 * @param usingConnection Code block using connection
 * @param onError Code block called on relevant errors
 */
private fun useMqttClientConnection(
    thingName: String,
    clientId: String,
    endpoint: String,
    certPath: String,
    keyPath: String,
    rootCaPath: String? = null,
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
        builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)

        if (rootCaPath != null) {
            builder.withCertificateAuthorityFromPath(null, rootCaPath);
        }

        builder.withClientId(clientId)
            .withEndpoint(endpoint)
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
