package com.github.ajsnarr98.smartdoorcloser.iotshadow

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Represents a service for making http calls to the AWS IoT shadow API.
 *
 * Meant to be implemented by retrofit.
 */
interface RawIoTShadowService {
    @POST("things/{thingName}/shadow")
    fun updateShadow(@Path("thingName") thingName: String, @Body stateUpdate: ShadowUpdateRequestBody): Call<ResponseBody>
}