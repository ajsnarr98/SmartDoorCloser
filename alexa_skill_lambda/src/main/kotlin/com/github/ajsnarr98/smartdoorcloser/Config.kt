package com.github.ajsnarr98.smartdoorcloser

import com.google.gson.GsonBuilder
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
 * @property endpoint AWS IoT service endpoint hostname
 */
data class Config(
    var endpoint: String? = null,
    var table: String? = null,
    var tableId: String? = null,
) {
    /**
     * Verifies whether or not this config instance has valid parameters.
     * Throws an [IllegalArgumentException] if any invalid parameters are found.
     */
    fun verify(): Boolean {
        val lazyMessage: (varName: String) -> (() -> String) = { varName ->
            { "Invalid value for $varName. Please check $CONFIG_FILENAME" }
        }
        require(endpoint?.isNotBlank() == true, lazyMessage = lazyMessage("endpoint"))
        require(table?.isNotBlank() == true, lazyMessage = lazyMessage("table"))
        require(tableId?.isNotBlank() == true, lazyMessage = lazyMessage("tableId"))
        return true
    }

    companion object {

        /**
         * Config instance.
         */
        val instance: Config by lazy {
            loadInstance()
        }

        /**
         * Loads the config from file.
         */
        private fun loadInstance(): Config {
            println("Loading config")
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