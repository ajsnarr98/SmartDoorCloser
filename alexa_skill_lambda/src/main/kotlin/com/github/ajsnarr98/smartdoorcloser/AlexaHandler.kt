package com.github.ajsnarr98.smartdoorcloser

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.google.gson.GsonBuilder
import java.io.InputStream
import java.io.OutputStream
import java.util.Scanner

class AlexaHandler : RequestStreamHandler {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun handleRequest(input: InputStream?, output: OutputStream?, context: Context?) {
        if (input == null || output == null) throw NullPointerException("Bad input. Input to request handler should not be null")
        output.write(handleRequest(getRequestString(input), context).toByteArray(Charsets.UTF_8))
    }

    /**
     * Handles the given json request.
     */
    fun handleRequest(request: String, context: Context?): String {
        val directive = SmartHomeRequest.newInstance(gson, request)

        val logger = LoggerNonNull(context?.logger)
        val response = "200 OK"
        // log execution details
        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()))
        logger.log("CONTEXT: " + gson.toJson(context))
        // process event
        logger.log("REQUEST: $request")
        logger.log("DECODED REQUEST: " + directive.toJson(gson))
        return response
    }
}