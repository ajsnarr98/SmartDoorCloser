package com.github.ajsnarr98.smartdoorcloser

import org.apache.logging.log4j.LogManager
import software.amazon.awssdk.crt.mqtt.MqttClientConnection
import software.amazon.awssdk.iot.iotshadow.IotShadowClient
import java.util.concurrent.CompletableFuture

import software.amazon.awssdk.crt.mqtt.QualityOfService
import software.amazon.awssdk.iot.iotshadow.model.*

import java.io.Closeable

import software.amazon.awssdk.iot.iotshadow.model.ShadowState

import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowRequest

/**
 * Represents this IoT device's shadow.
 */
class ShadowHandler(private val connection: MqttClientConnection, private val config: Config): AutoCloseable, Closeable {

    companion object {
        private val log = LogManager.getLogger()
    }

    private val shadowClient = IotShadowClient(connection)
    private val connected: CompletableFuture<Boolean> = connection.connect()
    private var gotShadow: CompletableFuture<Shadow> = CompletableFuture()
    private var closeCommandListener: () -> Unit = {}

    @Volatile
    private var localShadow: Shadow? = null

    override fun close() {
        val disconnected: CompletableFuture<Void> = connection.disconnect()
        disconnected.get()
    }

    /**
     * Adds a listener for when a close command is requested.
     */
    fun setCloseCommandListener(onCloseCommand: () -> Unit) {
        closeCommandListener = onCloseCommand
    }

    init {
        try {
            val isSessionPresent = connected.get()
            log.info("Connected to " + (if (!isSessionPresent) "clean" else "existing") + " session!")
        } catch (ex: Exception) {
            throw RuntimeException("Exception occurred during connect", ex)
        }

        // -------------- subscribe to shadow delta events ----------
        log.info("Subscribing to shadow delta events...")
        val requestShadowDeltaUpdated = ShadowDeltaUpdatedSubscriptionRequest()
        requestShadowDeltaUpdated.thingName = config.thingName
        val subscribedToDeltas: CompletableFuture<Int> = shadowClient.SubscribeToShadowDeltaUpdatedEvents(
            requestShadowDeltaUpdated,
            QualityOfService.AT_LEAST_ONCE,
            this::onShadowDeltaUpdated
        )
        subscribedToDeltas.get()

        // ----------------- subscribe to shadow updates ------------
        log.info("Subscribing to update responses...")
        val requestUpdateShadow = UpdateShadowSubscriptionRequest()
        requestUpdateShadow.thingName = config.thingName
        val subscribedToUpdateAccepted: CompletableFuture<Int> =
            shadowClient.SubscribeToUpdateShadowAccepted(
                requestUpdateShadow,
                QualityOfService.AT_LEAST_ONCE,
                this::onUpdateShadowAccepted
            )
        val subscribedToUpdateRejected: CompletableFuture<Int> =
            shadowClient.SubscribeToUpdateShadowRejected(
                requestUpdateShadow,
                QualityOfService.AT_LEAST_ONCE,
                this::onUpdateShadowRejected
            )
        subscribedToUpdateAccepted.get()
        subscribedToUpdateRejected.get()

        // ---------------- subscribe to get responses --------------
        log.info("Subscribing to get responses...")
        val requestGetShadow = GetShadowSubscriptionRequest()
        requestGetShadow.thingName = config.thingName
        val subscribedToGetShadowAccepted: CompletableFuture<Int> =
            shadowClient.SubscribeToGetShadowAccepted(
                requestGetShadow,
                QualityOfService.AT_LEAST_ONCE,
                this::onGetShadowAccepted
            )
        val subscribedToGetShadowRejected: CompletableFuture<Int> =
            shadowClient.SubscribeToGetShadowRejected(
                requestGetShadow,
                QualityOfService.AT_LEAST_ONCE,
                this::onGetShadowRejected
            )
        subscribedToGetShadowAccepted.get()
        subscribedToGetShadowRejected.get()

        // ---------------- get shadow initial state --------------
        log.info("Requesting current shadow state...")
        val getShadowRequest = GetShadowRequest()
        getShadowRequest.thingName = config.thingName
        val publishedGetShadow: CompletableFuture<Int> = shadowClient.PublishGetShadow(
            getShadowRequest,
            QualityOfService.AT_LEAST_ONCE
        )
        publishedGetShadow.get()
        localShadow = gotShadow.get()
        updateShadow() // updates shadow if needed
    }

    /**
     * Updates the shadow using the local shadow synchronously.
     *
     * Only updates if shadow is marked for update.
     */
    private fun updateShadow(): CompletableFuture<Void?> {
        return localShadow?.withLock { shadow ->
            if (!shadow.needsUpdate) {
                log.debug("shadow not marked for update... returning")
                return@withLock CompletableFuture.completedFuture(null)
            }
            shadow.needsUpdate = false // updating shadow, so now can reset this field

            log.debug("Updating shadow value")
            // build a request to let the service know our current value
            // and desired value, and that we only want to update if the
            // version matches the version we know about
            val request = UpdateShadowRequest().apply {
                thingName = config.thingName
                state = ShadowState().apply {
                    reported = shadow.asHashMap()
                    desired = shadow.asHashMap()
                }
            }

            // Publish the request
            return@withLock shadowClient.PublishUpdateShadow(request, QualityOfService.AT_LEAST_ONCE)
                .thenRun { log.debug("Update request published") }
                .exceptionally { ex ->
                    log.error("Update request failed: $ex")
                    null
                }
        } ?: CompletableFuture.completedFuture(null)
    }

    /**
     * The Device Shadow service sends messages to this topic when a difference
     * is detected between the reported and desired sections of a shadow.
     */
    private fun onShadowDeltaUpdated(response: ShadowDeltaUpdatedEvent) {
        log.info("Shadow delta updated")
        var hasNewCloseCommand = false
        localShadow?.withLock { shadow ->
            shadow.updateFromDelta(response.state)
            // check if the close command value has changed
            hasNewCloseCommand = shadow.actOnCloseCommand()
        }
        // if a close command has been issued, respond
        if (hasNewCloseCommand) {
            log.info("close command received")
            updateShadow()
            closeCommandListener()
        }
    }

    /**
     * The Device Shadow service sends messages to this topic when a request
     * for a shadow is made successfully.
     */
    private fun onGetShadowAccepted(response: GetShadowResponse) {
        log.info("Received initial shadow state")
        // store initial shadow
        // if given state is null or invalid, this will pass default initial shadow
        gotShadow.complete(Shadow.buildFrom(config, response.state?.desired))
    }

    /**
     * The Device Shadow service sends messages to this topic when a request
     * for a shadow is rejected.
     */
    private fun onGetShadowRejected(response: ErrorResponse) {
        if (response.code == 404) {
            log.info("Thing has no shadow document to start... creating new one...")
        } else {
            log.error("GetShadow request was rejected: code: ${response.code} message: ${response.message}")
        }
        // store default initial shadow
        gotShadow.complete(Shadow.buildFrom(config,null))
    }

    /**
     * The Device Shadow service sends messages to this topic when an update is
     * successfully made to a shadow.
     */
    private fun onUpdateShadowAccepted(response: UpdateShadowResponse) {
        log.info("Shadow updated successfully")
    }

    /**
     * The Device Shadow service sends messages to this topic when an update
     * to a shadow is rejected.
     */
    private fun onUpdateShadowRejected(response: ErrorResponse) {
        log.error("Shadow update was rejected: code: ${response.code} message: ${response.message}")
        // mark shadow as needing update again
        localShadow?.withLock { shadow ->
            // show that shadow does not need update
            shadow.needsUpdate = true
        }
    }
}
