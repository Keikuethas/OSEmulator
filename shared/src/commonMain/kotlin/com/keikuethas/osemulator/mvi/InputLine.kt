package com.keikuethas.osemulator.mvi

import com.keikuethas.osemulator.ui.StyledText
import com.keikuethas.osemulator.ui.TerminalColors

fun inputLine(
    pending: StyledText?,
    prompt: String,
    command: String,
): StyledText = StyledText(
    buildList {
        pending?.spans?.let(::addAll)
        add(StyledText.Span(TerminalColors.Prompt, "$prompt "))
        add(StyledText.Span(TerminalColors.Text, command))
    }
)