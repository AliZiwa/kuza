package com.kuza.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.kuza.app.presentation.ChatScreen
import com.kuza.app.presentation.ChatViewModel
import com.kuza.app.presentation.KuzaTheme

class MainActivity : ComponentActivity() {

    // Week 1: the friend reads from a script. This is the only line that changes in week 4.
    // Week 2: the phone suggests your side of the chat with ML Kit Smart Reply. The friend is unchanged.
    private val viewModel: ChatViewModel by viewModels {
        ChatViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { KuzaTheme { ChatScreen(viewModel) } }
    }
}
