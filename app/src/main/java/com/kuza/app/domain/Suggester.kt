package com.kuza.app.domain

/**
 * Week 2: the second seam. A [Replier] speaks for the friend. A [Suggester] offers words to you.
 * Same shape, opposite direction: it reads the transcript and proposes what the local user might
 * say next. The screen renders whatever comes back as chips and never learns where they came from.
 */
fun interface Suggester {
    /** Up to a few short replies the user could send next. Empty means show nothing. */
    suspend fun suggest(transcript: List<Message>): List<String>
}

/** Week 1 behaviour, kept as a value: no chips, ever. */
object NoSuggestions : Suggester {
    override suspend fun suggest(transcript: List<Message>): List<String> = emptyList()
}

/**
 * The rules around the model, kept pure so they can be tested without a device. Everything the
 * model is allowed to see goes through [window]; everything the screen is allowed to show goes
 * through [clean]. The model sits between those two gates and never touches the screen directly.
 */
object SuggestionRules {
    /** ML Kit Smart Reply only considers the last 10 messages, so we send no more than that. */
    const val WINDOW = 10

    /** Smart Reply returns at most three. We hold the line even if a later week's model does not. */
    const val MAX_CHIPS = 3

    /** Chips are for tapping, not reading. Anything longer is a sentence, not a reply. */
    const val MAX_CHARS = 40

    /**
     * The slice of the transcript the model may see, or null when there is nothing to answer:
     * an empty chat, or a chat where you spoke last. Suggestions are for answering the friend.
     */
    fun window(transcript: List<Message>): List<Message>? {
        val last = transcript.lastOrNull() ?: return null
        if (last.author != Author.Friend) return null
        return transcript.takeLast(WINDOW)
    }

    /** Trim, drop blanks and overlong lines, drop case-insensitive duplicates, keep the first few. */
    fun clean(raw: List<String>): List<String> =
        raw.asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.length <= MAX_CHARS }
            .distinctBy { it.lowercase() }
            .take(MAX_CHIPS)
            .toList()
}
