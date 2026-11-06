# The First Bulb Lit the Wrong Side of the Table

### Silence is an answer. Give it a status code.

*AI in Mobile, a weekly learning journey across iOS and Android*

---

Last week, in "Build the Socket Before You Buy the Bulb," I built a chat app with one interface and no intelligence, and told you the socket was the expensive part. This week I screwed in the first bulb, and it lit my side of the table, not the friend's.

Sit down. Week two of Kuza adds smart reply chips, and the model doing it has not been touched in two years.

The bulb is ML Kit Smart Reply: `com.google.mlkit:smart-reply:17.0.4`, a bundled on-device model, about 5.7 MB added to the APK, no network, no language model, English only. The 17.0.4 build dates from August 2024, and the October 26, 2026 release notes still list it unchanged. Not deprecated. That is the most dependable thing about it.

The turn arrived before the first line of code. Last week's seam, `Replier`, speaks for the friend. Smart Reply refuses that job. Its whole API is built on two words, local user and remote user, and it only ever proposes what the local user could say next. The first intelligence to arrive did not want the friend's chair. It wanted mine. So the app gained a second seam, same shape, opposite direction:

```kotlin
fun interface Suggester {
    /** Up to a few short replies the user could send next. Empty means show nothing. */
    suspend fun suggest(transcript: List<Message>): List<String>
}
```

The mapping is the whole integration: oldest first, and which side of the table is you.

```kotlin
val conversation = window.map { message ->
    when (message.author) {
        Author.You -> TextMessage.createForLocalUser(message.text, message.sentAt)
        Author.Friend -> TextMessage.createForRemoteUser(message.text, message.sentAt, FRIEND_ID)
    }
}
```

Now the part I want you to carry home. The model has three answers, and two of them are silence. `STATUS_SUCCESS` with one to three suggestions. `STATUS_NOT_SUPPORTED_LANGUAGE` when the chat is not English. `STATUS_NO_REPLY` when it is not confident, or when the conversation touches its list of sensitive topics. It does not guess. It tells you, with a number, that it has nothing to say.

```kotlin
if (result.status != SmartReplySuggestionResult.STATUS_SUCCESS) return emptyList()
return SuggestionRules.clean(result.suggestions.map { it.text })
```

Last week the scripted friend returned null to stay quiet. This week a model Google shipped agreed, and went one better: it says why. Every model we add from here will be judged on whether it can shut up and explain itself.

Two pure, tested gates sit around the model. `window` decides what it may see: nothing on an empty chat, nothing when you spoke last, never more than the ten messages it reads anyway. `clean` decides what the screen may show: trimmed, under forty characters, no duplicates, three at most. The model never touches the screen.

The tradeoffs, told straight. This is a frozen, casual, English-only model with no idea what your product sounds like. No persona, no slang from your market. My mother will not tap a chip that does not speak Swahili. I chose the bundled model over the Play Services download (about 200 KB, still in beta) because it works the moment the app installs, offline, with no store service in the loop. The price is 5.7 MB. If that number offends you, you have not looked at your image assets.

The honest part: for a casual chat app, this is probably all the reply suggestion you need. Next week we start paying in gigabytes and device lists, and you should know what 5.7 MB already bought before you sign that cheque.

**The leadership lens.** Before you fund the language model, ask your team one question: whose voice is the feature? A model that speaks for the user and one that speaks to the user are different products with different risks, and this week the API decided which one we got. Then ask what classic on-device ML already covers: chips are a two-dependency change, measured by one number, tap rate.

Silence is an answer. Give it a status code.

*Code: github.com/AliZiwa/kuza, tag `week-02`, commit "Week 2: ML Kit Smart Reply chips behind a Suggester seam." Compile verification is on Ali's machine. Next week the friend's whole thread gets summarized by Gemini Nano, and we count what the first real language model costs in bytes, latency, and allowed phones.*

---

**Medium tags:** Artificial Intelligence, Mobile App Development, Android, Kotlin, Machine Learning

**LinkedIn opener (two lines before the fold):**

> Week two of building a chat app in public: I added the first AI feature, and the model doing it has not been updated in two years. That is the most dependable thing about it.
>
> It also refused the job I built the socket for, and I think it was right.

#AI #MobileDevelopment #Android #Kotlin #MachineLearning
