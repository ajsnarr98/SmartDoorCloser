package com.github.ajsnarr98.smartdoorcloser

import org.junit.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

private const val sampleUri = "https://raw.githubusercontent.com/alexa/alexa-smarthome/master/sample_messages/"

class AlexaHandlerTest {

    private val handler = AlexaHandler()

    /**
     * Gets the response for the given request string.
     *
     * @param request json request
     */
    private fun getResponse(request: String): String {
        val outputStream = object : OutputStream() {
            private val sb = StringBuilder()

            @Throws(IOException::class)
            override fun write(b: Int) {
                sb.append(b.toChar())
            }

            override fun toString(): String {
                return sb.toString()
            }
        }
        handler.handleRequest(request.byteInputStream(Charsets.UTF_8), outputStream,null)
        return outputStream.toString();
    }

    /**
     * Pulls a request string from the given url.
     */
    private fun getSampleRequest(url: String): String {
        try {
            val connection: HttpURLConnection = URL(url).openConnection().apply {
                connect()
            } as HttpURLConnection
            return when (connection.responseCode) {
                200, 201, 202 -> {
                    // use and close buffered reader afterward
                    val br = BufferedReader(InputStreamReader(connection.inputStream))
                    br.use { it.lines().toString() }
                }
                else -> ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

//    @Test
//    fun TestAuthorization() {
//        val response: JSONObject = getResponse(getSampleRequest(sampleUri + "Authorization/Authorization.AcceptGrant.request.json"))
//        val namespace: String = response.getJSONObject("event").getJSONObject("header").get("namespace").toString()
//        val name: String = response.getJSONObject("event").getJSONObject("header").get("name").toString()
//        assertEquals("Namespace should be Alexa.Authorization", "Alexa.Authorization", namespace)
//        assertEquals("Name should be AcceptGrant", "AcceptGrant", name)
//    }
//
//    @Test
//    fun TestDiscovery() {
//        val response: JSONObject = getResponse(getSampleRequest(sampleUri + "Discovery/Discovery.request.json"))
//        val namespace: String = response.getJSONObject("event").getJSONObject("header").get("namespace").toString()
//        val name: String = response.getJSONObject("event").getJSONObject("header").get("name").toString()
//        assertEquals("Namespace should be Alexa.Discovery", "Alexa.Discovery", namespace)
//        assertEquals("Name should be Discover.Response", "Discover.Response", name)
//    }
//
//    @Test
//    fun TestPowerControllerOff() {
//        val response: JSONObject = getResponse(getSampleRequest(sampleUri + "PowerController/PowerController.TurnOff.request.json"))
//        val namespace: String = response.getJSONObject("event").getJSONObject("header").get("namespace").toString()
//        val name: String = response.getJSONObject("event").getJSONObject("header").get("name").toString()
//        assertEquals("Namespace should be Alexa", "Alexa", namespace)
//        assertEquals("Name should be Response", "Response", name)
//    }
}