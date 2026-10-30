package com.kuza.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kuza.app.domain.ScriptedFriend
import com.kuza.app.presentation.ChatScreen
import com.kuza.app.presentation.ChatViewModel
import com.kuza.app.presentation.KuzaTheme

class MainActivity : ComponentActivity() {

    // Week 1: the friend reads from a script. This is the only line that changes in week 4.
    private val viewModel: ChatViewModel by viewModels {
        viewModelFactory { initializer { ChatViewModel(replier = ScriptedFriend()) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { KuzaTheme { ChatScreen(viewModel) } }
    }
}
