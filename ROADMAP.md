# Kuza: a chat app that learns one thing a week

*Kuza* is Swahili for "grow." This repo is the companion to the weekly **AI in Mobile** series.
It starts as the plainest chat app that works and gains one AI capability per week. Each week is
one branch merged to `main`, one article in `articles/`, and one tag (`week-01`, `week-02`, ...).

Rules the series keeps:

- Every article is a 3 minute read or shorter, Kotlin snippets included.
- Every snippet is the real diff from that week, not pseudocode.
- Every API and version is verified against current docs the day the article is written.
- Each week ends with the app still working. No half-features.

| Week | Tag | What the app learns | The honest question |
|---|---|---|---|
| 1 | `week-01` | Nothing. Compose chat, MVI, a `Replier` seam, a scripted friend. | Where does intelligence plug in, and how do you know the plug fits? |
| 2 | `week-02` | ML Kit Smart Reply chips (classic on-device ML, no LLM). | How much value do you get before you touch a language model? |
| 3 | `week-03` | ML Kit GenAI Summarization of the thread (Gemini Nano). | What does the first LLM feature cost in bytes, latency, and device coverage? |
| 4 | `week-04` | The friend becomes a model: `Replier` backed by LiteRT-LM running Gemma 4 E2B. | One-line swap, but what did the UI assume about speed that is now false? |
| 5 | `week-05` | Structured output: the model returns typed JSON (mood, intent), validated against a closed enum. | How do you stop a model from inventing a field? |
| 6 | `week-06` | Reply hygiene: the `ReplyCleaner` lessons from Banter, with tests. | What do small models get wrong on the way out, every time? |
| 7 | `week-07` | A character with memory: Koog agent with a persona and short transcript window. | When does a prompt become an agent, and is the word earned? |
| 8 | `week-08` | Semantic memory: EmbeddingGemma 2 over past messages, retrieval before reply. | What is the smallest RAG that changes a conversation? |
| 9 | `week-09` | Hybrid routing: cloud Gemini fallback behind the same `Replier`, with the five routing signals. | Which messages should never leave the phone? |
| 10 | `week-10` | AppFunctions: expose "send message" and "summarize thread" to the Android intelligence system. | Which functions are public, private, or need a human tap? |
| 11 | `week-11` | A2UI: the agent answers with a Compose component (a poll card) instead of text. | What is the agent not allowed to draw? |
| 12 | `week-12` | Evals and guardrails: a replayable test set of conversations, pass/fail, run in CI. | How do you know week 12 is better than week 4? |

Weeks 9 to 12 may reorder based on what the platforms ship. The rule is that each week must build
on the one before it, and the series never repeats a lesson already published.

## Running it

```bash
./gradlew :app:installDebug          # device or emulator, API 26+
./gradlew :app:testDebugUnitTest     # the domain tests
```

Weeks 3 and later need a device that supports the on-device model in question; each week's article
and README section states the requirement.
