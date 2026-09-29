package com.keikuethas.osemulator.mvi.usecases

import com.keikuethas.osemulator.mvi.CMDResult
import com.keikuethas.osemulator.mvi.CMDWindowState
import com.keikuethas.osemulator.ui.StyledText
import com.keikuethas.osemulator.ui.TerminalColors

fun invalidArgument(
    state: CMDWindowState,
    result: CMDResult.InvalidArgument
) = state.copy(
    lines = state.lines + StyledText.of(
        "Неверный аргумент: ${result.value}",
        TerminalColors.Error
    )
)