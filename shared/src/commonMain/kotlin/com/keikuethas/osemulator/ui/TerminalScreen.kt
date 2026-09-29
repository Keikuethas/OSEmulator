package com.keikuethas.osemulator.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.keikuethas.osemulator.mvi.CMDWindowState

@Composable
fun TerminalScreen(
    state: CMDWindowState,
    input: String,
    onInputChange: (String) -> Unit,
    onEnter: (String) -> Unit,
) {
    Terminal(
        lines = state.lines.map { it.toAnnotatedString() },
        pending = state.pendingString?.toAnnotatedString(),
        prompt = state.prompt,
        input = input,
        onInputChange = onInputChange,
        onEnter = onEnter,
        promptColor = Color(0xFF000000.toInt() or TerminalColors.Prompt),
    )
}