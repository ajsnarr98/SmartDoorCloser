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
        var event: Event? = null,
        var context: Context? = null,
    )

    protected data class Event(
        var header: Header? = null,
        var endpoint: Endpoint? = null,
        var payload: Payload? = null,
    )

    protected data class Header(
        var namespace: String? = null,
        var name: String? = null,
        var messageId: String? = null,
        var payloadVersion: String? = null,
    )

    protected data class Payload(
        var endpoints: List<Endpoint>? = null,
    )

    protected data class Endpoint(
        var scope: Scope? = null,
        var endpointId: String? = null,
        var manufacturerName: String? = null,
        var description: String? = null,
        var friendlyName: String? = null,
        var additionalAttributes: EndpointAdditionalAttributes? = null,
        var displayCategories: List<String>? = null,
        var capabilities: List<EndpointCapability>? = null,
        var connections: List<EndpointConnection>? = null,
    )

    protected data class Scope(
        var type: String? = null,
        var token: String? = null,
    )

    protected data class EndpointAdditionalAttributes(
        var manufacturer: String? = null,
        var model: String? = null,
        var serialNumber: String? = null,
        var firmwareVersion: String? = null,
        var softwareVersion: String? = null,
        var customIdentifier: String? = null,
    )

    protected data class EndpointCapability(
        var type: String? = null,
        var `interface`: String? = null,
        var instance: String? = null,
        var version: String? = null,
        var properties: EndpointCapabilityProperties? = null,
        var capabilityResources: EndpointCapabilityResources? = null,
        var semantics: Semantics? = null,
    )

    protected data class EndpointCapabilityProperties(
        var supported: List<EndpointCapabilityPropertiesSupported>? = null,
        var proactivelyReported: Boolean? = null,
        var retrievable: Boolean? = null,
        var nonControllable: Boolean? = null,
    )

    protected data class EndpointCapabilityPropertiesSupported(
        var name: String? = null,
    )

    protected data class EndpointCapabilityResources(
        var friendlyNames: List<EndpointCapabilityResourcesFriendlyName>? = null,
    )

    protected data class EndpointCapabilityResourcesFriendlyName(
        var `@type`: String? = null,
        var value: EndpointCapabilityResourcesFriendlyNameValue? = null,
    )

    protected data class EndpointCapabilityResourcesFriendlyNameValue(
        var text: String? = null,
        var locale: String? = null,
    )

    protected data class Semantics(
        var actionMappings: List<SemanticsActionMapping>? = null,
        var stateMappings: List<SemanticsStateMapping>? = null,
    )

    protected data class SemanticsActionMapping(
        var `@type`: String? = null,
        var actions: List<String>? = null,
        var directive: SemanticsActionMappingDirective? = null,
    )

    protected data class SemanticsActionMappingDirective(
        var name: String? = null,
    )

    protected data class SemanticsStateMapping(
        var `@type`: String? = null,
        var states: List<String>? = null,
        var value: String? = null,
    )

    protected data class EndpointConnection(
        var type: String? = null,
        var macAddress: String? = null,
        var value: String? = null,
        var homeId: String? = null,
        var nodeId: String? = null,
    )

    protected data class Context(
        var properties: List<ContextProperty>? = null
    )

    /**
     * @property timeOfSample example: "2017-02-03T16:20:50.52Z"
     */
    protected data class ContextProperty(
        var namespace: String? = null,
        var instance: String? = null,
        var name: String? = null,
        var value: String? = null,
        var timeOfSample: String? = null,
        var uncertaintyInMilliseconds: Int? = null,
    )
}