package com.github.ajsnarr98.smartdoorcloser.hardware

import com.github.ajsnarr98.smartdoorcloser.toHexString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.NetworkInterface

private const val ID_FILE = "id.dat"

object Constants {

    /**
     * Unique device id for this door closer. This is read once from the
     * physical address of *some* network interface, and stored in a file
     * so that it stays the same for future use.
     */
    val DEVICE_ID: String = kotlin.run {
        val file = File(ID_FILE)
        if (file.exists()) {
            FileInputStream(file).use { fileIn ->
                ObjectInputStream(fileIn).use { objIn ->
                    objIn.readObject() as? String ?: throw IllegalStateException("Expected '$ID_FILE' to contain a serialized string object")
                }
            }
        } else {
            val newID: String = generateDeviceId()
            FileOutputStream(file).use { fileOut ->
                ObjectOutputStream(fileOut).use { objOut ->
                    objOut.writeObject(newID)
                }
            }
            newID
        }
    }

    object LED {
        const val LED_PIN = 21
    }

    /**
     * Obtains a unique device id by finding a network interface that contains
     * a physical (MAC) address. If none can be found, throws an error.
     *
     * NOTE: This may not produce the same device ID every time as there is
     *       no guarantee that there is only one network interface or that
     *       that same interface is the first to be read.
     */
    private fun generateDeviceId(): String {
        for (netInterface in NetworkInterface.getNetworkInterfaces()) {
            try {
                val addr = netInterface.hardwareAddress
                if (addr != null) {
                    return addr.toHexString()
                }
            } catch (_: IOException) { }
        }
        throw IllegalStateException("Unable to generate device id")
    }
}
