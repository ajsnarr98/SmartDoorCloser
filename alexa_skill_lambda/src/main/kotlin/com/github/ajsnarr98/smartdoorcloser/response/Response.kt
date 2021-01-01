package com.github.ajsnarr98.smartdoorcloser.response

import com.google.gson.Gson

abstract class Response protected constructor(private val resp: RawResponse) {

    /**
     * Checks a required field.
     */
    protected fun getField(str: String?): String
        = str ?: throw IllegalArgumentException("Required field was null.")

    fun toJson(gson: Gson): String = gson.toJson(resp)

    /**
     * Json response template.
     */
    protected data class RawResponse(
        val event: Event? = null,
        val context: Context? = null,
    )

    protected data class Event(
        val header: Header? = null,
        val endpoint: Endpoint? = null,
    )

    protected data class Header(
        val namespace: String? = null,
        val name: String? = null,
        val messageId: String? = null,
        val payloadVersion: String? = null,
    )

    protected data class Endpoint(
        val scope: Scope? = null,
        val endpointId: String? = null,
        val manufacturerName: String? = null,
        val description: String? = null,
        val friendlyName: String? = null,
        val additionalAttributes: EndpointAdditionalAttributes? = null,
        val displayCategories: List<String>? = null,
        val capabilities: List<EndpointCapability>? = null,
        val connections: List<EndpointConnection>? = null,
    )

    protected data class Scope(
        val type: String? = null,
        val token: String? = null,
    )

    protected data class EndpointAdditionalAttributes(
        val manufacturer: String? = null,
        val model: String? = null,
        val serialNumber: String? = null,
        val firmwareVersion: String? = null,
        val softwareVersion: String? = null,
        val customIdentifier: String? = null,
    )

    protected data class EndpointCapability(
        val type: String? = null,
        val `interface`: String? = null,
        val instance: String? = null,
        val version: String? = null,
        val properties: List<EndpointCapabilityProperties>? = null,
        val capabilityResources: EndpointCapabilityResources? = null,
        val semantics: Semantics? = null,
    )

    protected data class EndpointCapabilityProperties(
        val supported: List<EndpointCapabilityPropertiesSupported>? = null,
        val proactivelyReported: Boolean? = null,
        val retrievable: Boolean? = null,
        val nonControllable: Boolean? = null,
    )

    protected data class EndpointCapabilityPropertiesSupported(
        val name: String? = null,
    )

    protected data class EndpointCapabilityResources(
        val friendlyNames: List<EndpointCapabilityResourcesFriendlyName>? = null,
    )

    protected data class EndpointCapabilityResourcesFriendlyName(
        val `@type`: String? = null,
        val value: EndpointCapabilityResourcesFriendlyNameValue? = null,
    )

    protected data class EndpointCapabilityResourcesFriendlyNameValue(
        val text: String? = null,
        val locale: String? = null,
    )

    protected data class Semantics(
        val actionMappings: List<SemanticsActionMapping>? = null,
        val stateMappings: List<SemanticsStateMapping>? = null,
    )

    protected data class SemanticsActionMapping(
        val `@type`: String? = null,
        val actions: List<String>? = null,
        val directive: SemanticsActionMappingDirective? = null,
    )

    protected data class SemanticsActionMappingDirective(
        val name: String? = null,
    )

    protected data class SemanticsStateMapping(
        val `@type`: String? = null,
        val states: List<String>? = null,
        val value: String? = null,
    )

    protected data class EndpointConnection(
        val type: String? = null,
        val macAddress: String? = null,
        val value: String? = null,
        val homeId: String? = null,
        val nodeId: String? = null,
    )

    protected data class Context(
        val properties: List<ContextProperty>? = null
    )

    /**
     * @property timeOfSample example: "2017-02-03T16:20:50.52Z"
     */
    protected data class ContextProperty(
        val namespace: String? = null,
        val instance: String? = null,
        val name: String? = null,
        val value: String? = null,
        val timeOfSample: String? = null,
        val uncertaintyInMilliseconds: Int? = null,
    )
}