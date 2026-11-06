package com.kuza.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The gates on either side of the model, tested without the model. */
class SuggestionRulesTest {

    private fun you(id: Long, text: String) = Message(id, Author.You, text, sentAt = id)
    private fun them(id: Long, text: String) = Message(id, Author.Friend, text, sentAt = id)

    @Test
    fun `no window on an empty chat`() {
        assertNull(SuggestionRules.window(emptyList()))
    }

    @Test
    fun `no window when you spoke last`() {
        assertNull(SuggestionRules.window(listOf(them(1, "hi"), you(2, "hello"))))
    }

    @Test
    fun `window is the last ten messages, oldest first`() {
        val chat = (1L..25L).map { if (it % 2 == 0L) them(it, "t$it") else you(it, "y$it") } + them(26, "last")
        val window = SuggestionRules.window(chat)!!
        assertEquals(SuggestionRules.WINDOW, window.size)
        assertEquals(17L, window.first().id)
        assertEquals(26L, window.last().id)
    }

    @Test
    fun `clean trims, drops blanks and long lines, dedupes, and caps at three`() {
        val raw = listOf("  Sure!  ", "", "sure!", "I will be there in about forty five minutes, okay?", "On my way", "Ok", "Later")
        assertEquals(listOf("Sure!", "On my way", "Ok"), SuggestionRules.clean(raw))
    }
}
