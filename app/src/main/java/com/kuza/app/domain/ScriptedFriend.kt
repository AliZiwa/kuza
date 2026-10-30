package com.kuza.app.domain

/**
 * A friend who has exactly one thing to say about anything. No model, no network, no cleverness.
 * It exists so the chat works end to end before any intelligence is added, and so later weeks
 * have something honest to measure against.
 */
class ScriptedFriend(
    private val lines: List<String> = DEFAULT_LINES,
) : Replier {

    override suspend fun reply(transcript: List<Message>): String? {
        val last = transcript.lastOrNull() ?: return null
        if (last.author != Author.You) return null
        val turn = transcript.count { it.author == Author.You } - 1
        return lines[turn % lines.size]
    }

    companion object {
        val DEFAULT_LINES = listOf(
            "Eh, tell me more.",
            "You are not serious.",
            "Okay, and then what happened?",
            "I said the same thing last week.",
            "Send me the details, I will look.",
        )
    }
}
