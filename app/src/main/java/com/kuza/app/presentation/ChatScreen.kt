package com.kuza.app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kuza.app.domain.Author
import com.kuza.app.domain.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.friendTyping) {
        val last = state.messages.size + if (state.friendTyping) 1 else 0
        if (last > 0) listState.animateScrollToItem(last - 1)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kuza") }) },
        modifier = Modifier.imePadding(),
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(state.messages, key = { it.id }) { Bubble(it) }
                if (state.friendTyping) item(key = "typing") { TypingBubble() }
            }
            Composer(
                draft = state.draft,
                onDraft = { viewModel.onIntent(ChatIntent.DraftChanged(it)) },
                onSend = { viewModel.onIntent(ChatIntent.Send) },
            )
        }
    }
}

@Composable
private fun Bubble(message: Message) {
    val mine = message.author == Author.You
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            Modifier
                .widthIn(max = 280.dp)
                .background(
                    if (mine) colors.primary else colors.surfaceVariant,
                    RoundedCornerShape(18.dp),
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                message.text,
                color = if (mine) colors.onPrimary else colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun TypingBubble() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(18.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) { Text("...", style = MaterialTheme.typography.bodyLarge) }
    }
}

@Composable
private fun Composer(draft: String, onDraft: (String) -> Unit, onSend: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = draft,
            onValueChange = onDraft,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Say something") },
            singleLine = true,
        )
        TextButton(onClick = onSend, enabled = draft.isNotBlank()) { Text("Send") }
    }
}
