package com.github.ajsnarr98.smartdoorcloser.iotshadow

import com.github.ajsnarr98.smartdoorcloser.Config
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Contains retrofit instance and handles calls to the RawIotShadowService.
 */
class IoTShadowService(
    config: Config,
    gson: Gson,
) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://${config.endpoint}/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val ioTShadowService: RawIoTShadowService =
        retrofit.create(RawIoTShadowService::class.java)

    /**
     * Blocking call that sends a command to change the IoT device's shadow.
     *
     * @return true if successful, false if not
     */
    fun sendCloseDoorCommand(thingName: String): Boolean {
        val call = ioTShadowService.updateShadow(
            thingName = thingName,
            stateUpdate = ShadowUpdateRequestBody.createCloseCommand()
        )
        // make blocking call
        return call.execute().isSuccessful
    }
}