package com.github.ajsnarr98.smartdoorcloser.response

import com.github.ajsnarr98.smartdoorcloser.Config
import com.github.ajsnarr98.smartdoorcloser.SmartHomeRequest
import com.github.ajsnarr98.smartdoorcloser.getTimeNow
import com.github.ajsnarr98.smartdoorcloser.iotshadow.IoTShadowService
import com.google.gson.Gson

class ToggleControllerResponse private constructor(resp: RawResponse) : Response(resp) {
    companion object {
        fun newInstance(request: SmartHomeRequest, config: Config, gson: Gson): ToggleControllerResponse {
            val directive = request.directive
            val thingName: String? = directive?.endpoint?.endpointId

            // send close command to IoT device
            var success = false
            var invalidDirective = false
            if (thingName != null) {
                val iotService = IoTShadowService(config, gson)
                success = iotService.sendCloseDoorCommand(thingName)
            } else {
                success = false
                invalidDirective = true
            }

            return if (success) {
                ToggleControllerResponse(
                    RawResponse().apply {
                        event = Event().apply {
                            header = Header().apply {
                                namespace = "Alexa"
                                name = "Response"
                                messageId = directive?.header?.messageId
                                payloadVersion = directive?.header?.payloadVersion
                            }
                            endpoint = Endpoint().apply {
                                scope = Scope().apply {
                                    type = directive?.endpoint?.scope?.type
                                    token = directive?.endpoint?.scope?.token
                                }
                                endpointId = directive?.endpoint?.endpointId
                            }
                            payload = Payload()
                        }
                        context = Context().apply {
                            properties = listOf(
                                ContextProperty().apply {
                                    namespace = "Alexa.ToggleController"
                                    instance = directive?.header?.instance
                                    name = "toggleState"
                                    value = when (directive?.header?.name) {
                                        "TurnOn" -> "ON"
                                        "TurnOff" -> "OFF"
                                        else -> throw IllegalArgumentException("Unknown toggle directive")
                                    }
                                    timeOfSample = getTimeNow()
                                    uncertaintyInMilliseconds = 500
                                }
                            )
                        }
                    }
                )
            } else {
                ToggleControllerResponse(
                    RawResponse().apply {
                        event = Event().apply {
                            header = Header().apply {
                                namespace = "Alexa"
                                name = "ErrorResponse"
                                messageId = directive?.header?.messageId
                                payloadVersion = "3"
                            }
                            endpoint = Endpoint().apply {
                                endpointId = directive?.endpoint?.endpointId
                            }
                            payload = Payload().apply {
                                if (invalidDirective) {
                                    type = "INVALID_DIRECTIVE"
                                    message = "No valid endpoint given"
                                } else {
                                    type = "ENDPOINT_UNREACHABLE"
                                    message = "error occurred while sending close command"
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}