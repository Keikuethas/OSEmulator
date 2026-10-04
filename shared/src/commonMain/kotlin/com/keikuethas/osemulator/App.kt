package com.keikuethas.osemulator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keikuethas.osemulator.mvi.CMDEvent
import com.keikuethas.osemulator.mvi.CMDViewModel
import com.keikuethas.osemulator.ui.TerminalScreen

@Composable
@Preview
fun App(
    args: Array<String> = emptyArray(),
    viewModel: CMDViewModel = viewModel { CMDViewModel(args) },
    onEvent: (CMDEvent) -> Unit = {}
) {
    val inputText = remember { mutableStateOf("") }
    val state = viewModel.windowStateFlow.collectAsStateWithLifecycle().value
    val eventFlow = viewModel.eventFlow

    LaunchedEffect(Unit) {
        eventFlow.collect {
            onEvent(it)
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TerminalScreen(
                state = state,
                input = inputText.value,
                onInputChange = { inputText.value = it },
                onEnter = { cmd ->
                    if (cmd.isNotBlank()) {
                        viewModel.sendMessage(cmd.trim())
                        inputText.value = ""
                    }
                },
            )
        }
    }
}