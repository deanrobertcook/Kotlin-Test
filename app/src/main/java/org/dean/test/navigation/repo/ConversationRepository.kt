package org.dean.test.navigation.repo

import io.reactivex.Maybe
import io.reactivex.Single
import org.dean.test.navigation.model.Conversation
import java.util.*

interface ConversationRepository {
    fun getConversation(convId: UUID): Maybe<Conversation>
    fun getConversations(): Single<Set<Conversation>>
}


class DummyConversationRepository: ConversationRepository {

    private val conversations = IntRange(1, 50).map { i ->
        Conversation(UUID.randomUUID(), "Conversation$i", false)
    }.toSet()

    override fun getConversation(convId: UUID): Maybe<Conversation> {
        return Maybe.create { em ->
            val conv = conversations.find { conv -> conv.id == convId }
            conv?.let { em.onSuccess(it) } ?: em.onComplete()
        }
    }

    override fun getConversations(): Single<Set<Conversation>> {
        return Single.create { em -> em.onSuccess(conversations) }
    }

    companion object {
        const val CONV_COUNT = 50
    }
}