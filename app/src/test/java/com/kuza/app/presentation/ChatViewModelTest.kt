package com.kuza.app.presentation

import com.kuza.app.domain.Author
import com.kuza.app.domain.ScriptedFriend
import com.kuza.app.domain.Suggester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Week 2: chips appear after the friend speaks, and vanish the moment you answer. */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    /** A suggester that echoes the friend's last line back as one chip, so we can see the plumbing. */
    private val echo = Suggester { transcript ->
        val last = transcript.last()
        if (last.author == Author.Friend) listOf("You said: ${last.text}") else emptyList()
    }

    private fun viewModel() = ChatViewModel(
        replier = ScriptedFriend(lines = listOf("eh")),
        suggester = echo,
        typingBeatMs = 0,
        clock = { 0L },
        io = dispatcher,
    )

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `chips arrive after the friend replies`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onIntent(ChatIntent.DraftChanged("hello"))
        vm.onIntent(ChatIntent.Send)
        advanceUntilIdle()
        assertEquals(listOf("You said: eh"), vm.state.value.suggestions)
    }

    @Test
    fun `picking a chip sends it and clears the chips`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onIntent(ChatIntent.DraftChanged("hello"))
        vm.onIntent(ChatIntent.Send)
        advanceUntilIdle()
        vm.onIntent(ChatIntent.PickSuggestion("You said: eh"))
        // Sent immediately, chips cleared immediately, before any new reply lands.
        assertTrue(vm.state.value.suggestions.isEmpty())
        assertEquals("You said: eh", vm.state.value.messages.last { it.author == Author.You }.text)
    }
}
