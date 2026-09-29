package com.keikuethas.osemulator.mvi.usecases

import com.keikuethas.osemulator.mvi.CMDResult
import com.keikuethas.osemulator.mvi.CMDWindowState
import com.keikuethas.osemulator.ui.StyledText
import com.keikuethas.osemulator.ui.TerminalColors

fun print(
    state: CMDWindowState,
    result: CMDResult.Print
) = if (result.sendEOL) {
    state.copy(
        lines = state.lines + StyledText.of(
            result.message,
            TerminalColors.Text
        )
    )
} else {
    state.copy(
        pendingString = StyledText.of(
            result.message,
            TerminalColors.Text
        )
    )
}