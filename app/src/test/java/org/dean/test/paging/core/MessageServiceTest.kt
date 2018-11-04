package org.dean.test.paging.core

import io.mockk.every
import io.mockk.mockkClass
import org.junit.Test

class MessageServiceTest {

    val client = mockkClass(DownloadClient::class)

    @Test
    fun downloadSinglePage() {

        every { client.downloadNextPage(1) } returns listOf(
                MessageResponse("7e3132f9-6d50-44bc-b287-44eb7ec5f6ae", "1463451777280", "abc"),
                MessageResponse("f3fe9786-ca5a-4c7b-8ac5-f7d6c1d8e690", "1463470679208", "https://unsplash.com/photos/2SfRAWkinpU/download")
        )

        val service = MessageService(client)

        service.nextPage(1)
                .test()
                .await()
                .assertValue { list ->
                    println(list)
                    list.size == 2 &&
                            list[0] is TextMessage &&
                            list[1] is ImageMessage
                }
    }


}