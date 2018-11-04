package org.dean.test.paging.core

import io.reactivex.Single
import org.threeten.bp.Instant
import java.util.*

class MessageService(private val client: DownloadClient) {

    fun nextPage(page: Int): Single<List<Message>> {
        return Single.create<List<Message>> { emitter ->
            try {
                emitter.onSuccess(client.downloadNextPage(page).map {

                    val id = UUID.fromString(it.id)!!
                    val time = Instant.ofEpochMilli(it.time.toLong())!!

                    if (it.text.startsWith("https://")) {
                        ImageMessage(id, time, it.text)
                    } else {
                        TextMessage(id, time, it.text)
                    }
                })
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

    fun downloadImage(url: String): Single<ByteArray> {
        return Single.create<ByteArray> { emitter ->
            try {
                emitter.onSuccess(client.downloadImage(url))
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}

interface Message {
    val id: UUID
    val time: Instant
}

data class TextMessage(override val id: UUID,
                       override val time: Instant,
                       val msg: String): Message

data class ImageMessage(override val id: UUID,
                        override val time: Instant,
                        val url: String): Message