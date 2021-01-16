package com.github.ajsnarr98.smartdoorcloser

import java.lang.IllegalStateException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Represents this IoT device's shadow.
 *
 * @property id - device id
 * @property thingName - the thingName for this device
 * @property friendlyName - human-readable name of device (en-US)
 * @property lastCloseCmd - the unix-epoch-based time of the last command to
 *                          close the door
 * @property closeCmdCompleted - if completed, matches `lastCloseCmd`, else it
 *                               still needs to complete the command
 * @property needsUpdate - used to mark if default values were used during
 *                         initial creation of this object
 */
data class Shadow(
    var id: String,
    var thingName: String,
    var friendlyName: String,
    var lastCloseCmd: String,
    var closeCmdCompleted: String,
) {
    var needsUpdate: Boolean = false
    private val lock = ReentrantLock()

    fun <T> withLock(action: (shadow: Shadow) -> T): T {
        return lock.withLock { action(this) }
    }

    /**
     * Checks if there is an un-acted on close command
     * (lastCloseCmd != closeCmdCompleted) if need to act. If there is a
     * close command, update this shadow to say the command was completed, and
     * return true, else do nothing and return false.
     */
    fun actOnCloseCommand(): Boolean {
        return if (lastCloseCmd != closeCmdCompleted) {
            closeCmdCompleted = lastCloseCmd
            needsUpdate = true
            true
        } else {
            false
        }
    }

    /**
     * Updates this shadow's values from the given map.
     */
    fun updateFromDelta(map: Map<String, Any?>?) {
        if (map == null) return

        id = map[ID_PROPERTY] as? String ?: this.id
        thingName = map[THING_NAME_PROPERTY] as? String ?: this.thingName
        friendlyName = map[FRIENDLY_NAME_PROPERTY] as? String ?: this.friendlyName
        lastCloseCmd = map[LAST_CLOSE_CMD_PROPERTY] as? String ?: this.lastCloseCmd
        closeCmdCompleted = map[CLOSE_CMD_COMPLETED_PROPERTY] as? String ?: this.closeCmdCompleted
    }

    /**
     * Converts this shadow to a map.
     *
     * @param excludeLastCloseCmd - whether or not to exclude this property
     *                              in map
     */
    fun asHashMap(excludeLastCloseCmd: Boolean = false): HashMap<String, Any?> {
        return HashMap<String, Any?>().apply {
            put(ID_PROPERTY, id)
            put(THING_NAME_PROPERTY, thingName)
            put(FRIENDLY_NAME_PROPERTY, friendlyName)
            if (!excludeLastCloseCmd) put(LAST_CLOSE_CMD_PROPERTY, lastCloseCmd)
            put(CLOSE_CMD_COMPLETED_PROPERTY, closeCmdCompleted)
        }
    }

    companion object {
        fun buildFrom(config: Config, map: Map<String, Any>?): Shadow {
            val id: String = config.thingName ?: throw IllegalStateException("thingName should not be null")
            val thingName: String = config.thingName ?: throw IllegalStateException("thingName should not be null")
            val friendlyName: String = map?.get(FRIENDLY_NAME_PROPERTY) as? String ?: FRIENDLY_NAME_DEFAULT
            var lastCloseCmd: String = map?.get(LAST_CLOSE_CMD_PROPERTY) as? String ?: CLOSE_CMD_DEFAULT
            val closeCmdCompleted: String = map?.get(CLOSE_CMD_COMPLETED_PROPERTY) as? String ?: lastCloseCmd
            // in case closeCmdCompleted was found, but not lastCloseCmd
            if (lastCloseCmd == CLOSE_CMD_DEFAULT && closeCmdCompleted != CLOSE_CMD_DEFAULT) {
                lastCloseCmd = closeCmdCompleted
            }
            return Shadow(
                id = id,
                thingName = thingName,
                friendlyName = friendlyName,
                lastCloseCmd = lastCloseCmd,
                closeCmdCompleted = closeCmdCompleted,
            ).apply {
                needsUpdate = map == null
                    || id != map[ID_PROPERTY]
                    || thingName != map[THING_NAME_PROPERTY]
                    || friendlyName != map[FRIENDLY_NAME_PROPERTY]
                    || lastCloseCmd != map[LAST_CLOSE_CMD_PROPERTY]
                    || closeCmdCompleted != map[CLOSE_CMD_COMPLETED_PROPERTY]
            }
        }

        private const val ID_PROPERTY = "id"
        private const val THING_NAME_PROPERTY = "thingName"
        private const val FRIENDLY_NAME_PROPERTY = "friendly_name"
        private const val LAST_CLOSE_CMD_PROPERTY = "last_close_cmd"
        private const val CLOSE_CMD_COMPLETED_PROPERTY = "close_cmd_completed"

        private const val FRIENDLY_NAME_DEFAULT = "door"
        private const val CLOSE_CMD_DEFAULT = "0"
    }
}