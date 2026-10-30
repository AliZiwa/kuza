# Build the Socket Before You Buy the Bulb

### A model is a tenant. Build the room before you advertise it.

*AI in Mobile, a weekly learning journey across iOS and Android*

---

Starting today, this series builds one app in public, one small step a week, and the first step has no AI in it at all. I want you to notice how much that bothers you. It bothered me. We called the app Kuza, Swahili for "grow," and week one is a chat screen where the other person is a script with five lines.

Sit down. The boring week is the one that decides whether the clever weeks are cheap.

Here is the whole plan: twelve weeks, one capability each. Smart replies, then an on-device summary, then the friend becomes a Gemma model, then structured output, memory, retrieval, a cloud fallback, AppFunctions, A2UI, and finally evals that prove week twelve beats week four. Every week the code is in the repo under a tag, and every article is three minutes, Kotlin included.

Week one's entire intelligence is this:

```kotlin
fun interface Replier {
    /** Returns the friend's next line, or null to stay quiet. */
    suspend fun reply(transcript: List<Message>): String?
}
```

One method. It is `suspend` because eleven weeks from now it will wait on a GPU. It returns a nullable because a friend who has nothing to say should be allowed to say nothing, and models that are forced to answer are the ones that invent. It takes the whole transcript, not the last message, because context is the one thing every later week will want more of.

Behind it today sits a friend with no brain:

```kotlin
class ScriptedFriend(private val lines: List<String>) : Replier {
    override suspend fun reply(transcript: List<Message>): String? {
        val last = transcript.lastOrNull() ?: return null
        if (last.author != Author.You) return null
        val turn = transcript.count { it.author == Author.You } - 1
        return lines[turn % lines.size]
    }
}
```

It will not answer itself. It will not speak first. Those are the two rules Banter's Director needed a hundred lines and a test suite to enforce after the model was in. Here they cost three lines before it is.

The ViewModel knows a `Replier` exists and nothing else. It shows a typing bubble, waits a beat, asks for a line, and if the user types again before the line arrives it cancels the job, because a reply composed against a transcript that is now stale is answering the wrong question. That cancel is the first thing people forget when the brain arrives and takes four seconds instead of one.

```kotlin
replyJob?.cancel()
replyJob = viewModelScope.launch(io) {
    _state.update { it.copy(friendTyping = true) }
    delay(typingBeatMs)
    val line = replier.reply(_state.value.messages)
    _state.update { it.copy(friendTyping = false) }
    if (line != null) append(Author.Friend, line)
}
```

Now the turn, and it is small on purpose. When I wrote the Banter group chat, the architecture came after the model: I had Gemma talking first and spent the following weeks building the Director, the SpeakerPicker, the ReplyCleaner around what it did wrong. Every one of those classes was a reaction. This time the seam exists before anything is behind it, and the test for the scripted friend is the same test the model will have to pass in week four. The script is not a placeholder. It is the baseline.

The honest part, because a father does not oversell week one. Nothing here is hard. A senior Android engineer writes this in an afternoon. The dependency list is Compose, lifecycle, coroutines, and JUnit, and there is no network permission in the manifest. If your team cannot build this in a day, the problem you have is not an AI problem.

**The leadership lens.** Before you fund the model, ask one question of the codebase: where does it plug in? If the answer is a diagram and not an interface with a test, you are about to pay for the brain and the surgery in the same quarter. Fund the socket this sprint. It is the cheapest line item you will ever approve.

A model is a tenant. Build the room before you advertise it.

*Code: github.com/AliZiwa/kuza, tag `week-01`. Next week the friend learns to suggest replies without a language model, and we see how far classic on-device ML gets you before the bill arrives.*

---

**Medium tags:** Artificial Intelligence, Mobile App Development, Android, Kotlin, Software Architecture

**LinkedIn opener (two lines before the fold):**

> This week I started building a chat app in public, one AI capability a week for twelve weeks. Week one has no AI in it at all.
>
> It has one interface with one method, and that method is the most expensive line in the project.

#AI #MobileDevelopment #Android #Kotlin #SoftwareArchitecture
