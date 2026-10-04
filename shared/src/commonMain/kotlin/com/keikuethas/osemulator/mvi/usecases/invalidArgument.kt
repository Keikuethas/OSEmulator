package com.keikuethas.osemulator.mvi.usecases

import com.keikuethas.osemulator.mvi.CMDResult
import com.keikuethas.osemulator.mvi.CMDWindowState
import com.keikuethas.osemulator.ui.StyledText
import com.keikuethas.osemulator.ui.TerminalColors

fun invalidArgument(
    state: CMDWindowState,
    result: CMDResult.InvalidArgument
): CMDWindowState {
    val newLines = state.lines.toMutableList()
    newLines += StyledText.of(
        text = "Неверный аргумент: ${result.value}",
        rgb = TerminalColors.Error
    )
    result.description?.let { desc ->
        newLines += StyledText.of(
            text = desc,
            rgb = TerminalColors.Muted
        )
    }
    return state.copy(lines = newLines.toList())
}