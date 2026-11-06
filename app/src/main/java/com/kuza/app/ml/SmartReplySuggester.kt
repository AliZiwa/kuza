package com.kuza.app.ml

import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplyGenerator
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import com.kuza.app.domain.Author
import com.kuza.app.domain.Message
import com.kuza.app.domain.Suggester
import com.kuza.app.domain.SuggestionRules
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/**
 * Week 2: ML Kit Smart Reply behind the [Suggester] seam. Classic on-device ML: a bundled model
 * of about 5.7 MB (com.google.mlkit:smart-reply:17.0.4), no network, no language model, English
 * only. It proposes up to three short replies to the friend's last message.
 *
 * Smart Reply has one job and three answers: STATUS_SUCCESS with one to three suggestions,
 * STATUS_NOT_SUPPORTED_LANGUAGE, or STATUS_NO_REPLY (including when the conversation touches a
 * topic on its sensitive list). Anything other than success is rendered as no chips at all.
 */
class SmartReplySuggester(
    private val generator: SmartReplyGenerator = SmartReply.getClient(),
) : Suggester, AutoCloseable {

    override suspend fun suggest(transcript: List<Message>): List<String> {
        val window = SuggestionRules.window(transcript) ?: return emptyList()

        // Smart Reply wants oldest first and needs to know which side is "you".
        val conversation = window.map { message ->
            when (message.author) {
                Author.You -> TextMessage.createForLocalUser(message.text, message.sentAt)
                Author.Friend -> TextMessage.createForRemoteUser(message.text, message.sentAt, FRIEND_ID)
            }
        }

        val result = try {
            generator.suggestReplies(conversation).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Model not ready, or ML Kit failed. Silence is the correct chip.
            return emptyList()
        }

        if (result.status != SmartReplySuggestionResult.STATUS_SUCCESS) return emptyList()
        return SuggestionRules.clean(result.suggestions.map { it.text })
    }

    /** Releases the model. The ViewModel calls this from onCleared. */
    override fun close() = generator.close()

    private companion object {
        /** Any stable string identifies the remote sender within one conversation. */
        const val FRIEND_ID = "friend"
    }
}
