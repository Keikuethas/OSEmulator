package com.keikuethas.osemulator.mvi.usecases

import com.keikuethas.osemulator.mvi.CMDResult
import com.keikuethas.osemulator.mvi.CMDWindowState
import com.keikuethas.osemulator.ui.StyledText
import com.keikuethas.osemulator.ui.TerminalColors

fun invalidCommand(
    state: CMDWindowState,
    result: CMDResult.InvalidCommand
) = state.copy(
    lines = state.lines + StyledText.of(
        "Неизвестная команда: ${result.value}",
        TerminalColors.Error
    )
)