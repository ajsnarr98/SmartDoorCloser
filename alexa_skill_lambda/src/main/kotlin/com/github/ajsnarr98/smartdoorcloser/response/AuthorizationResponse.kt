package com.github.ajsnarr98.smartdoorcloser.response

import com.github.ajsnarr98.smartdoorcloser.SmartHomeRequest

/**
 * Authorization response.
 *
 * We should not need to store the given auth info for anything (I think).
 */
class AuthorizationResponse private constructor(resp: RawResponse) : Response(resp) {
    companion object {
        fun newInstance(request: SmartHomeRequest): AuthorizationResponse {
            val directive = request.directive

            return when (directive?.header?.name) {
                "AcceptGrant" -> AuthorizationResponse(
                    RawResponse().apply {
                        event = Event().apply {
                            header = Header().apply {
                                namespace = "Alexa.Authorization"
                                name = "AcceptGrant.Response"
                                messageId = directive.header.messageId
                                payloadVersion = directive.header.payloadVersion
                            }
                            payload = Payload() // empty payload
                        }
                    }
                )
                else -> throw IllegalStateException("Unknown")
            }
        }
    }
}