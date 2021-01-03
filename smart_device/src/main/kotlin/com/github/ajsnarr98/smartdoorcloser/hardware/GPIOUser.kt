package com.ajsnarr.hauntedgameboard.hardware

/**
 * Denotes any class that uses GPIO.
 */
interface GPIOUser {
    fun onShutdown()
}