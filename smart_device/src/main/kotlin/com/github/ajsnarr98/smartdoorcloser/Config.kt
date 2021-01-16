package com.github.ajsnarr98.smartdoorcloser

import com.google.gson.GsonBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.nio.charset.StandardCharsets

/**
 * Default filename of config file.
 */
const val CONFIG_FILENAME = "config.json"

private val gson = GsonBuilder().setPrettyPrinting().create()

/**
 * Represents config stored in a file.
 *
 * @property thingName The name of the IoT thing
 * @property clientId The client ID to use when connecting. Only one active
 *                    connection can use the same client id.
 * @property endpoint AWS IoT service endpoint hostname
 * @property certPath Path to the IoT thing certificate
 * @property keyPath Path to the IoT thing private key
 * @property rootCaPath Path to the root certificate (optional)
 */
data class Config(
    var thingName: String? = null,
    var clientId: String? = null,
    var endpoint: String? = null,
    var certPath: String? = null,
    var keyPath: String? = null,
    var rootCaPath: String? = null,
) {
    /**
     * Verifies whether or not this config instance has valid parameters.
     * Throws an [IllegalArgumentException] if any invalid parameters are found.
     */
    fun verify(): Boolean {
        val lazyMessage: (varName: String) -> (() -> String) = { varName ->
            { "Invalid value for $varName. Please check $CONFIG_FILENAME" }
        }
        require(thingName?.isNotBlank() == true, lazyMessage = lazyMessage("thingName"))
        require(clientId?.isNotBlank() == true, lazyMessage = lazyMessage("clientId"))
        require(endpoint?.isNotBlank() == true, lazyMessage = lazyMessage("endpoint"))
        require(certPath?.isNotBlank() == true, lazyMessage = lazyMessage("certPath"))
        require(keyPath?.isNotBlank() == true, lazyMessage = lazyMessage("keyPath"))
        return true
    }

    companion object {

        /**
         * Config instance.
         */
        val instance: Config by lazy {
            loadInstance()
        }

        private val log: Logger = LogManager.getLogger()

        /**
         * Loads the config from file.
         */
        private fun loadInstance(): Config {
            log.info("Loading config")
            InputStreamReader(getFileFromResourceAsStream(CONFIG_FILENAME), StandardCharsets.UTF_8).use { streamReader ->
                BufferedReader(streamReader).use { buffReader ->
                    return gson.fromJson(buffReader, Config::class.java)
                }
            }
        }

        /**
         * Get a file resource from within jar file, or IDE resource folder.
         */
        private fun getFileFromResourceAsStream(fileName: String): InputStream {
            // the stream holding the file content
            return Config::class.java.classLoader.getResourceAsStream(fileName)
                ?: throw IllegalArgumentException("File not found! $fileName")
        }
    }
}