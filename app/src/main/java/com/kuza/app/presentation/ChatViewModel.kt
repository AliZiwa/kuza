package com.kuza.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuza.app.domain.Author
import com.kuza.app.domain.Message
import com.kuza.app.domain.Replier
import com.kuza.app.domain.ScriptedFriend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatState(
    val messages: List<Message> = emptyList(),
    val draft: String = "",
    val friendTyping: Boolean = false,
)

sealed interface ChatIntent {
    data class DraftChanged(val text: String) : ChatIntent
    data object Send : ChatIntent
}

/**
 * One state, one intent sink. The ViewModel knows a [Replier] exists and nothing about what is
 * behind it. That ignorance is the point: it is what lets week 4 swap in a language model with
 * a one-line change in [MainActivity].
 */
class ChatViewModel(
    private val replier: Replier = ScriptedFriend(),
    private val typingBeatMs: Long = 900,
    private val clock: () -> Long = System::currentTimeMillis,
    private val io: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    private var replyJob: Job? = null
    private var nextId = 1L

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.DraftChanged -> _state.update { it.copy(draft = intent.text) }
            ChatIntent.Send -> send()
        }
    }

    private fun send() {
        val text = _state.value.draft.trim()
        if (text.isEmpty()) return
        append(Author.You, text)
        _state.update { it.copy(draft = "") }

        // A reply composed before this message landed would be answering the wrong thing.
        replyJob?.cancel()
        replyJob = viewModelScope.launch(io) {
            _state.update { it.copy(friendTyping = true) }
            delay(typingBeatMs)
            val line = replier.reply(_state.value.messages)
            _state.update { it.copy(friendTyping = false) }
            if (line != null) append(Author.Friend, line)
        }
    }

    private fun append(author: Author, text: String) {
        val message = Message(id = nextId++, author = author, text = text, sentAt = clock())
        _state.update { it.copy(messages = it.messages + message) }
    }
}
