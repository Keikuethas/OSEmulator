package com.keikuethas.osemulator.mvi.usecases

import com.keikuethas.osemulator.mvi.CMDResult
import com.keikuethas.osemulator.mvi.CMDWindowState
import com.keikuethas.osemulator.mvi.inputLine

fun input(
    state: CMDWindowState,
    result: CMDResult.Input
) = state.copy(
    pendingString = null,
    lines = state.lines + inputLine(
        state.pendingString,
        state.prompt,
        result.message
    ),
)