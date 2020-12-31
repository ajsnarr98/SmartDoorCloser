package com.github.ajsnarr98.smartdoorcloser

import com.google.gson.Gson

/**
 * Represents a json request passed to the handler.
 */
data class SmartHomeRequest constructor(
    val directive: Directive? = null
) {
    companion object {
        /**
         * Create a new instance from a given json string.
         */
        fun newInstance(gson: Gson, json: String): SmartHomeRequest
            = gson.fromJson(json, SmartHomeRequest::class.java)
    }

    data class Directive(
        val header: Header? = null,
        val scope: Scope? = null,
        val endpoint: Endpoint? = null,
    )

    data class Header(
        val namespace: String? = null,
        val name: String? = null,
        val payloadVersion: String? = null,
        val messageId: String? = null,
        val correlationToken: String? = null,
    )

    data class Payload(
        val scope: Scope? = null,
    )

    data class Endpoint(
        val scope: Scope? = null,
        val endpointId: String? = null,
    )

    data class Scope(
        val type: String? = null,
        val token: String? = null,
    )
}