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
