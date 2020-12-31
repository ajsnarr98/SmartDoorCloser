package com.github.ajsnarr98.smartdoorcloser

import com.google.gson.Gson

/**
 * Represents a json request passed to the handler.
 */
data class SmartHomeRequest constructor(
    val directive: Directive?
) {
    companion object {
        /**
         * Create a new instance from a given json string.
         */
        fun newInstance(gson: Gson, json: String): SmartHomeRequest
            = gson.fromJson(json, SmartHomeRequest::class.java)
    }

    data class Directive(
        val header: Header?,
    )

    data class Header(
        val namespace: String?,
        val name: String?,
        val payloadVersion: String?,
        val messageId: String?,
        val correlationToken: String?,
    )
}