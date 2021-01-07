package com.github.ajsnarr98.smartdoorcloser.response

import com.github.ajsnarr98.smartdoorcloser.SmartHomeRequest
import com.github.ajsnarr98.smartdoorcloser.DB

class DiscoveryResponse private constructor(resp: RawResponse) : Response(resp) {
    companion object {
        fun newInstance(request: SmartHomeRequest): DiscoveryResponse {
            val directive = request.directive

            return DiscoveryResponse(
                RawResponse().apply {
                    event = Event().apply {
                        header = Header().apply {
                            namespace = "Alexa.Discovery"
                            name = "Discover.Response"
                            messageId = directive?.header?.messageId
                            payloadVersion = directive?.header?.payloadVersion
                        }
                        payload = Payload().apply {
                            endpoints = DB().getEntries().map { entry ->
                                Endpoint().apply {
                                    endpointId = entry.itemId
                                    manufacturerName = "AJ Snarr"
                                    description = "Smart door closing device by AJ Snarr"
                                    friendlyName = entry.friendlyName
                                    displayCategories = listOf("OTHER")
                                    capabilities = listOf(
                                        EndpointCapability().apply {
                                            type = "AlexaInterface"
                                            `interface` = "Alexa.ToggleController"
                                            instance = "Door.Close"
                                            version = directive?.header?.payloadVersion
                                            properties = EndpointCapabilityProperties().apply {
                                                supported = listOf(
                                                    EndpointCapabilityPropertiesSupported().apply {
                                                        name = "toggleState"
                                                    },
                                                )
                                                proactivelyReported = false
                                                retrievable = true
                                                nonControllable = false
                                            }
                                            capabilityResources = EndpointCapabilityResources().apply {
                                                friendlyNames = listOf(
                                                    EndpointCapabilityResourcesFriendlyName().apply {
                                                        `@type` = "text"
                                                        value = EndpointCapabilityResourcesFriendlyNameValue().apply {
                                                            text = entry.friendlyName
                                                            locale = "en-US"
                                                        }
                                                    },
                                                )
                                            }
                                            semantics = Semantics().apply {
                                                actionMappings = listOf(
                                                    SemanticsActionMapping().apply {
                                                        `@type` = "ActionsToDirective"
                                                        actions = listOf("Alexa.Actions.Close")
                                                        this.directive = SemanticsActionMappingDirective().apply {
                                                            name = "TurnOff"
                                                        }
                                                    },
                                                )
                                                stateMappings = listOf(
                                                    SemanticsStateMapping().apply {
                                                        `@type` = "StatesToValue"
                                                        states = listOf("Alexa.States.Closed")
                                                        value = "OFF"
                                                    },
                                                    SemanticsStateMapping().apply {
                                                        `@type` = "StatesToValue"
                                                        states = listOf("Alexa.States.Open")
                                                        value = "ON"
                                                    },
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}