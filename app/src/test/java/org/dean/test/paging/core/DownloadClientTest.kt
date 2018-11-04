package org.dean.test.paging.core

import okhttp3.OkHttpClient
import org.junit.Test

class DownloadClientTest {

    @Test
    fun downloadPage() {

        val okHttpClient = OkHttpClient()

        val client = DownloadClient(okHttpClient)

        println(client.downloadNextPage(9).size)
    }

    @Test
    fun downloadImage() {
        val okHttpClient = OkHttpClient()

        val client = DownloadClient(okHttpClient)

        val jpeg = client.downloadImage("https://unsplash.com/photos/2SfRAWkinpU/download")

        println("JPEG? has ${jpeg.size} bytes")
    }
}