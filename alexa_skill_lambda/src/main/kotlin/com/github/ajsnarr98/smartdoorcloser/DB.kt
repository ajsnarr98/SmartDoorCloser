package com.github.ajsnarr98.smartdoorcloser

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.ItemCollection
import com.amazonaws.services.dynamodbv2.document.QueryOutcome
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec

const val DEVICE_TABLE_NAME = "SmartDoorCloser"
const val DEVICE_TABLE_ID = "ItemId"
const val DEVICE_TABLE_FRIENDLY_NAME = "friendlyName"

class DB {
    private val client = AmazonDynamoDBClientBuilder.standard().build()
    private val dynamoDB = DynamoDB(client)
    private val table = dynamoDB.getTable(DEVICE_TABLE_NAME)

    fun getEntries(): List<Entry> {
        val querySpec = QuerySpec()
        val results: ItemCollection<QueryOutcome> = try {
            table.query(querySpec)
        } catch (e: Exception) {
            // The docs I read are not specific about what types of exceptions
            null
        } ?: return listOf()

        return results.mapNotNull { item -> try { Entry(item) } catch (e: IllegalArgumentException) { null } }
    }

    data class Entry(
        val itemId: String,
        val friendlyName: String? = null,
    ) {
        constructor(item: Item) : this(
            itemId = requireNotNull(item.getString(DEVICE_TABLE_ID)),
            friendlyName=item.getString(DEVICE_TABLE_FRIENDLY_NAME),
        )
    }
}