package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.mvi.CMDEvent
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

object Exit: TerminalCommand() {
    override suspend fun invoke(
        outputFlow: MutableSharedFlow<CMDResponse>,
        args: List<String>
    ) {
        if (args.isEmpty())
            outputFlow.emit(CMDEvent.Exit)
        else
            outputFlow.emit(
                CMDResult.InvalidArgument(args.first())
            )
    }

    override fun validate(args: List<String>): Boolean = args.isEmpty()

}