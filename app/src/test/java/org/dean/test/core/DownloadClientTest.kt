package org.dean.test.core

import okhttp3.OkHttpClient
import org.junit.Test
import java.net.URL

class DownloadClientTest {

    @Test
    fun downloadPage() {

        val okHttpClient = OkHttpClient()

        val client = DownloadClient(okHttpClient)

        println(client.downloadNextPage(1))
    }

    @Test
    fun downloadImage() {
        val okHttpClient = OkHttpClient()

        val client = DownloadClient(okHttpClient)

        val jpeg = client.downloadImage(URL("https://unsplash.com/photos/2SfRAWkinpU/download"))

        println("JPEG? has ${jpeg.size} bytes")
    }
}