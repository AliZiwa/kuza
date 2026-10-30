package com.kuza.app.domain

/** Who wrote a line. Week 1 has two people. Later weeks add more without touching the screen. */
enum class Author { You, Friend }

data class Message(
    val id: Long,
    val author: Author,
    val text: String,
    val sentAt: Long,
)

/**
 * The seam. Everything that will ever decide what the other side says lives behind this one
 * method. Week 1 fills it with a script. Later weeks fill it with a model, then an agent.
 * The screen never finds out.
 */
fun interface Replier {
    /** Returns the friend's next line, or null to stay quiet. */
    suspend fun reply(transcript: List<Message>): String?
}
