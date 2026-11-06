# Kuza

A chat app that learns one AI trick a week. Companion code for the **AI in Mobile** series by
[Ali Ziwa](https://aliziwa.com). The plan is in [ROADMAP.md](ROADMAP.md); the articles are in
[`articles/`](articles/); each week is a git tag.

## Week 1: the seam

```
app/src/main/java/com/kuza/app
├── MainActivity.kt                 builds the ViewModel with a Replier. The only line that changes in week 4.
├── domain/
│   ├── Models.kt                   Author, Message, and the Replier seam
│   └── ScriptedFriend.kt           a Replier with five lines and no brain
└── presentation/
    ├── ChatViewModel.kt            ChatState + ChatIntent, one StateFlow
    ├── ChatScreen.kt               bubbles, typing indicator, composer
    └── Theme.kt
```

```bash
./gradlew :app:installDebug
./gradlew :app:testDebugUnitTest
```

Minimum Android 8.0 (API 26). No network permission, no model, no accounts. That is the point of
week 1.

## Week 2: the chips

The phone suggests your side of the chat. After the friend speaks, up to three short replies
appear above the keyboard; tap one and it is sent. Classic on-device ML, no language model.

```
app/src/main/java/com/kuza/app
├── MainActivity.kt                 now also hands the ViewModel a Suggester
├── domain/
│   ├── Suggester.kt                the second seam, NoSuggestions, and SuggestionRules (the two gates)
├── ml/
│   └── SmartReplySuggester.kt      ML Kit Smart Reply behind the seam
└── presentation/
    ├── ChatViewModel.kt            suggestions in ChatState, PickSuggestion intent
    └── ChatScreen.kt               a SuggestionChip row between the thread and the composer
```

What changed in the build: `com.google.mlkit:smart-reply:17.0.4` (bundled model, about 5.7 MB
added to the APK) and `kotlinx-coroutines-play-services` so the Play Services `Task` can be
awaited. Nothing else.

Device requirements: any Android 8.0+ device or emulator. The model ships inside the APK, so no
Google Play Services download and no network. Smart Reply only answers in English, only considers
the last 10 messages, returns at most three suggestions, and stays silent on topics from its
sensitive list or when it is not confident. Silence is rendered as no chips.

Honest note: this week's code was written and reviewed without an Android toolchain on the
machine that wrote it. The unit tests in `app/src/test` cover the two gates and the ViewModel
plumbing; compile verification is on Ali's machine before the tag.

```bash
./gradlew :app:testDebugUnitTest     # SuggestionRulesTest, ChatViewModelTest, ScriptedFriendTest
```
