package org.dean.test.navigation.repo

import org.junit.Test

class DummyConversationRepositoryTest {

    @Test
    fun testGetConversations() {
        val repo = DummyConversationRepository()

        repo.getConversations()
                .map { convs -> convs.size }
                .test()
                .await()
                .assertResult(DummyConversationRepository.CONV_COUNT)
    }

    @Test
    fun testGetConversation() {
        val repo = DummyConversationRepository()

        repo.getConversations()
                .map { convs -> convs.first().id }
                .toMaybe()
                .flatMap { id -> repo.getConversation(id) }
                .test()
                .await()
                .assertValue { conv ->
                    conv.name == "Conversation1"
                }
    }

}