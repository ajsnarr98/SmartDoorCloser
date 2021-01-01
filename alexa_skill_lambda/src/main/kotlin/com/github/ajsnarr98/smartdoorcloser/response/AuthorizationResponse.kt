package com.github.ajsnarr98.smartdoorcloser.response

import com.github.ajsnarr98.smartdoorcloser.SmartHomeRequest

class AuthorizationResponse private constructor(resp: RawResponse) : Response(resp) {
    companion object {
        fun newInstance(directive: SmartHomeRequest): AuthorizationResponse {
            TODO()
        }
    }
}