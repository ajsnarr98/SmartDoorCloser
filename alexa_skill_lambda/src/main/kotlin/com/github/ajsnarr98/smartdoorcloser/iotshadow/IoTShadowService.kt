package com.github.ajsnarr98.smartdoorcloser.iotshadow

import okhttp3.Call
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Represents a service for making http calls to the AWS IoT shadow API.
 */
interface IoTShadowService {
    @POST("things/{thingName}/shadow")
    fun updateShadow(@Path("thingName") thingName: String, @Body stateUpdate: RequestBody): Call<>
}