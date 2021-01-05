package com.github.ajsnarr98.smartdoorcloser.db

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.github.ajsnarr98.smartdoorcloser.hardware.Constants

const val DEVICE_TABLE_NAME = "SmartDoorCloser"
const val DEVICE_TABLE_PRIMARY_KEY = "ItemId"

class DB {
    private val dbClient = AmazonDynamoDBClientBuilder.standard().build()
    private val dynamoDB = DynamoDB(dbClient)
    private val table = dynamoDB.getTable(DEVICE_TABLE_NAME)

    /**
     * Send an update to the database about the state of this device.
     */
    fun sendDeviceUpdate(isClosed: Boolean) {
        // TODO - finish this method
        val updateItemSpec = UpdateItemSpec().apply {
            withPrimaryKey(DEVICE_TABLE_PRIMARY_KEY, Constants.DEVICE_ID)
        }

        table.updateItem(updateItemSpec)
    }
}