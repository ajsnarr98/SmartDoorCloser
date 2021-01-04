package com.github.ajsnarr98.smartdoorcloser.hardware

import com.ajsnarr.hauntedgameboard.hardware.GPIOUser
import cz.adamh.utils.NativeUtils
import org.apache.logging.log4j.Level
import java.io.IOException

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.Closeable

/**
 * A class containing native methods for communicating with raspberry pi gpio pins.
 */
object GPIO : Closeable {

    @JvmStatic
    val log: Logger = LogManager.getLogger()
    var initialized: Boolean = false

    enum class Level(val value: Int) {
        ON(1), OFF(0), ERROR(-1);
    }

    enum class Mode(val value: Int) {
        PI_INPUT(0), PI_OUTPUT(1), PI_ALT0(4), PI_ALT1(5),
        PI_ALT2(6), PI_ALT3(7), PI_ALT4(3), PI_ALT5(2);
    }

    init {
        try {
            NativeUtils.loadLibraryFromJar("/lib/gpio.so")
        } catch (e: IOException) {
            throw RuntimeException(e.toString())
        }
        setLogLevel(log.level)
    }

    val users = mutableListOf<GPIOUser>()

    /**
     * Registers a user of this library to be "cleaned up" later.
     */
    fun register(user: GPIOUser) {
        users.add(user)
    }

    /**
     * Cleanup a specific user and remove it from tracked list.
     */
    fun cleanup(user: GPIOUser) {
        if (users.remove(user)) user.onShutdown()
    }

    /**
     * Cleans up all users and terminates lib.
     */
    override fun close() {
        terminate()
    }

    /**
     * Sets the log level within native code.
     */
    fun setLogLevel(logLevel: org.apache.logging.log4j.Level) {
        _setLogLevel(when (logLevel) {
            org.apache.logging.log4j.Level.ALL, org.apache.logging.log4j.Level.TRACE -> 0
            org.apache.logging.log4j.Level.DEBUG -> 1
            org.apache.logging.log4j.Level.INFO -> 2
            org.apache.logging.log4j.Level.WARN, org.apache.logging.log4j.Level.ERROR, org.apache.logging.log4j.Level.FATAL -> 3
            else -> 4
        })
    }

    private external fun _setLogLevel(logLevel: Int): Int

    /**
     * Initialises the pigpio library. Must call before using other functions.
     *
     * @return true if successful, false otherwise.
     */
    fun initialize(): Boolean {
        return if (!initialized) {
            log.info("Initializing pigpio")
            initialized = (_initialize() >= 0)
            return initialized
        } else {
            false
        }
    }

    private external fun _initialize(): Int

    /**
     * Cleans all users and terminates the pigpio library. Must be called
     * before quiting.
     *
     * @return true if successful, false otherwise.
     */
    fun terminate(): Boolean {
        return if (initialized) {
            for (user in users) {
                user.onShutdown()
            }
            log.info("Terminating pigpio")
            // finally, terminate pigpio
            (_terminate() >= 0).also { termSuccess ->
                initialized = !termSuccess
            }
        } else {
            false
        }
    }

    private external fun _terminate(): Int

    /**
     * Sets the GPIO mode, typically input or output.
     *
     * @param gpio gpio pin to set the mode for
     * @param mode mode to set
     * @return true if successful, false otherwise.
     */
    fun setMode(gpio: Int, mode: Mode): Boolean {
        validateGpioPinNum(gpio)
        log.debug("Calling gpioSetMode")
        return _setMode(gpio, mode.value) >= 0
    }

    private external fun _setMode(gpio: Int, mode: Int): Int

    /**
     * Gets the GPIO mode.
     *
     * @param gpio gpio pin to get mode for
     * @return mode of the given pin
     */
    fun getMode(gpio: Int): Mode {
        validateGpioPinNum(gpio)
        log.debug("Calling gpioGetMode")
        val modeVal = _getmode(gpio)
        for (mode in Mode.values()) {
            if (mode.value == modeVal) {
                return mode
            }
        }
        throw IllegalStateException("Unknown mode returned from native _getMode")
    }

    private external fun _getmode(gpio: Int): Int

    /**
     * Reads the GPIO level, on or off.
     *
     * @param gpio gpio pin to read
     * @return on, off, or error.
     */
    fun read(gpio: Int): Level {
        validateGpioPinNum(gpio)
        log.debug("Calling gpioRead")
        val level = _read(gpio)
        return if (level > 0) {
            Level.ON
        } else if (level == 0) {
            Level.OFF
        } else {
            Level.ERROR
        }
    }

    private external fun _read(gpio: Int): Int

    /**
     * Sets the GPIO level, on or off.
     *
     * @param gpio gpio pin to write to
     * @param level level to set. Must be ON or OFF
     * @return true if successful, false otherwise.
     */
    fun write(gpio: Int, level: Level): Boolean {
        validateGpioPinNum(gpio)
        require(level != Level.ERROR) { "Must write either ON or OFF" }
        log.debug("Calling gpioWrite")
        return _write(gpio, level.value) >= 0
    }

    private external fun _write(gpio: Int, level: Int): Int

    /**
     * Clears all waveforms and any related data and stops the current waveform.
     *
     * @return true if successful, false otherwise.
     */
    fun waveClear(): Boolean {
        log.debug("Calling gpioWaveClear")
        return _waveClear() >= 0
    }

    private external fun _waveClear(): Int

    /**
     * Generate a ramp of waveforms for specified number of steps at the given
     * frequencies.
     *
     * @param gpio gpio pin to write to
     * @param rampFrequencies array of frequencies for each set of steps (in order)
     * @param rampNSteps parallel array of steps (in order)
     * @return true if successful, false otherwise.
     */
    fun waveRamps(gpio: Int, rampFrequencies: IntArray, rampNSteps: IntArray): Boolean {
        validateGpioPinNum(gpio)
        require(rampFrequencies.size == rampNSteps.size) { "Both array args must have same length." }
        require(rampFrequencies.isNotEmpty()) { "Passed waveRamp arrays cannot be empty" }
        log.debug("Calling waveRamps")
        return _waveRamps(gpio, rampFrequencies, rampNSteps) >= 0
    }

    private external fun _waveRamps(gpio: Int, rampFrequencies: IntArray, rampNSteps: IntArray): Int

    /**
     * Return whether or not a waveform is being transmitted.
     * @return true if a waveform is being transmitted, false otherwise.
     */
    fun waveIsBusy(): Boolean {
        log.debug("Calling gpioWaveTxBusy")
        return _waveIsBusy()
    }

    private external fun _waveIsBusy(): Boolean

    /**
     * Makes sure gpio is valid
     * @param gpio gpio pin number
     */
    @Throws(IllegalArgumentException::class)
    private fun validateGpioPinNum(gpio: Int) {
        require(!(gpio < 0 || gpio > 27)) { "Invalid GPIO pin number: $gpio" }
    }
}