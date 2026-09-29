package com.keikuethas.osemulator.cmd

import com.keikuethas.osemulator.mvi.CMDEvent
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

suspend fun exit(
    output: MutableSharedFlow<CMDResponse>,
    args: List<String>
) {
    if (args.isEmpty())
        output.emit(CMDEvent.Exit)
    else
        output.emit(
            CMDResult.InvalidArgument(args.first())
        )
}