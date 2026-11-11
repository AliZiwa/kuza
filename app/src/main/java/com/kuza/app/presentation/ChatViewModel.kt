package com.kuza.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kuza.app.domain.Author
import com.kuza.app.domain.Message
import com.kuza.app.domain.Replier
import com.kuza.app.domain.ScriptedFriend
import com.kuza.app.domain.NoSuggestions
import com.kuza.app.domain.Suggester
import com.kuza.app.ml.SmartReplySuggester
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
    /** Week 2: short replies you could tap instead of typing. Empty means no chips. */
    val suggestions: List<String> = emptyList(),
)

sealed interface ChatIntent {
    data class DraftChanged(val text: String) : ChatIntent
    data object Send : ChatIntent
    /** Week 2: a chip was tapped. The text is sent as if you had typed it. */
    data class PickSuggestion(val text: String) : ChatIntent
}

/**
 * One state, one intent sink. The ViewModel knows a [Replier] exists and nothing about what is
 * behind it. That ignorance is the point: it is what lets week 4 swap in a language model with
 * a one-line change in [MainActivity].
 */
class ChatViewModel(
    private val replier: Replier = ScriptedFriend(),
    private val suggester: Suggester = NoSuggestions,
    private val typingBeatMs: Long = 900,
    private val clock: () -> Long = System::currentTimeMillis,
    private val io: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    companion object {
        val Factory = viewModelFactory {
            initializer {
                ChatViewModel(replier = ScriptedFriend(), suggester = SmartReplySuggester())
            }
        }
    }

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state

    private var replyJob: Job? = null
    private var suggestJob: Job? = null
    private var nextId = 1L

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.DraftChanged -> _state.update { it.copy(draft = intent.text) }
            ChatIntent.Send -> send(_state.value.draft)
            is ChatIntent.PickSuggestion -> send(intent.text)
        }
    }

    private fun send(raw: String) {
        val text = raw.trim()
        if (text.isEmpty()) return
        append(Author.You, text)
        // Chips answer the friend's last line. Once you have answered it, they are stale.
        suggestJob?.cancel()
        _state.update { it.copy(draft = "", suggestions = emptyList()) }

        // A reply composed before this message landed would be answering the wrong thing.
        replyJob?.cancel()
        replyJob = viewModelScope.launch(io) {
            _state.update { it.copy(friendTyping = true) }
            delay(typingBeatMs)
            val line = replier.reply(_state.value.messages)
            _state.update { it.copy(friendTyping = false) }
            if (line != null) {
                append(Author.Friend, line)
                suggest()
            }
        }
    }

    /** Week 2: after the friend speaks, ask the suggester what you might say back. */
    private fun suggest() {
        suggestJob?.cancel()
        suggestJob = viewModelScope.launch(io) {
            val chips = suggester.suggest(_state.value.messages)
            _state.update { it.copy(suggestions = chips) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        (suggester as? AutoCloseable)?.close()
    }

    private fun append(author: Author, text: String) {
        val message = Message(id = nextId++, author = author, text = text, sentAt = clock())
        _state.update { it.copy(messages = it.messages + message) }
    }
}
