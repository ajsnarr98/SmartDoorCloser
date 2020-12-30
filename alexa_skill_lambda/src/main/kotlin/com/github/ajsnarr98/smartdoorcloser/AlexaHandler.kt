package com.github.ajsnarr98.smartdoorcloser

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class AlexaHandler : RequestHandler<Map<String,String>, String> {
    override fun handleRequest(input: Map<String, String>?, context: Context?): String {
        TODO("Not yet implemented")
    }
}