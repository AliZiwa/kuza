package com.kuza.app.domain

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScriptedFriendTest {

    private val friend = ScriptedFriend(lines = listOf("a", "b"))

    private fun you(id: Long, text: String) = Message(id, Author.You, text, sentAt = id)
    private fun them(id: Long, text: String) = Message(id, Author.Friend, text, sentAt = id)

    @Test
    fun `stays quiet on an empty chat`() = runTest {
        assertNull(friend.reply(emptyList()))
    }

    @Test
    fun `does not answer itself`() = runTest {
        assertNull(friend.reply(listOf(you(1, "hi"), them(2, "a"))))
    }

    @Test
    fun `cycles through its lines once per user message`() = runTest {
        assertEquals("a", friend.reply(listOf(you(1, "one"))))
        assertEquals("b", friend.reply(listOf(you(1, "one"), them(2, "a"), you(3, "two"))))
        assertEquals("a", friend.reply(listOf(you(1, "one"), them(2, "a"), you(3, "two"), them(4, "b"), you(5, "three"))))
    }
}
