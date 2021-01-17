package com.github.ajsnarr98.smartdoorcloser

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.github.ajsnarr98.smartdoorcloser.response.AuthorizationResponse
import com.github.ajsnarr98.smartdoorcloser.response.DiscoveryResponse
import com.github.ajsnarr98.smartdoorcloser.response.ToggleControllerResponse
import com.google.gson.GsonBuilder
import java.io.InputStream
import java.io.OutputStream

class AlexaHandler : RequestStreamHandler {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    // load config
    private val config = run {
        Config.instance.verify()
        return@run Config.instance
    }

    override fun handleRequest(input: InputStream?, output: OutputStream?, context: Context?) {
        if (input == null || output == null) throw NullPointerException("Bad input. Input to request handler should not be null")
        output.write(handleRequest(getRequestString(input), context).toByteArray(Charsets.UTF_8))
    }

    /**
     * Handles the given json request.
     */
    private fun handleRequest(request: String, context: Context?): String {
        val logger = LoggerNonNull(context?.logger)

        val parsedRequest = SmartHomeRequest.newInstance(gson, request)
        val directive = parsedRequest.directive
        val responseObj = when (directive?.header?.namespace) {
            "Alexa.Authorization" -> AuthorizationResponse.newInstance(parsedRequest, logger)
            "Alexa.Discovery" -> DiscoveryResponse.newInstance(parsedRequest, logger, config)
            "Alexa.ToggleController" -> ToggleControllerResponse.newInstance(parsedRequest, logger, config, gson)
            else -> throw IllegalArgumentException("Unknown directive: ${directive?.header?.namespace}")
        }

        return responseObj.toJson(gson)
    }
}