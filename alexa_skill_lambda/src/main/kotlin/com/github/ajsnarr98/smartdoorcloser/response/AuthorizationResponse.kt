package com.github.ajsnarr98.smartdoorcloser.response

import com.github.ajsnarr98.smartdoorcloser.SmartHomeRequest

class AuthorizationResponse : Response() {
    companion object {
        fun newInstance(directive: SmartHomeRequest): AuthorizationResponse {
            TODO()
        }
    }
}