package org.dean.test.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream

class DownloadClient(private val client: OkHttpClient) {

    fun downloadNextPage(index: Int): List<MessageResponse> {

        val request = Request.Builder()
                .url(endpoint(index))
                .build()

        val call = client.newCall(request)

        val resp = call.execute() //TODO wrap execute in try block to close request
        val body = resp.body()

        return if (body != null) {
            val bis = BufferedInputStream(body.byteStream())
            val bof = ByteArrayOutputStream()
            var res = bis.read()
            while (res != -1) {
                bof.write(res)
                res = bis.read()
            }

            JSON.parse(MessageResponse::class.serializer().list, bof.toString("UTF-8"))
        } else emptyList()
    }

    fun downloadImage(url: String): ByteArray {
        val request = Request.Builder()
                .url(url)
                .build()

        val body = client
                .newCall(request)
                .execute()
                .body()

        return if (body != null) {
            body.bytes()
        } else ByteArray(0)
    }


    companion object {
        fun endpoint(index: Int): String =
                "https://rawgit.com/wireapp/android_test_app/master/endpoint/$index.json"
    }
}

@Serializable
data class MessageResponse(val id: String, val time: String, val text: String)
