package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.cmd.VFS
import com.keikuethas.osemulator.mvi.CMDEvent
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

object Exit : TerminalCommand("exit") {
    override suspend fun invoke(
        vfs: VFS,
        outputFlow: MutableSharedFlow<CMDResponse>,
        args: List<String>
    ) {
        if (args.isEmpty())
            outputFlow.emit(CMDEvent.Exit)
        else
            outputFlow.emit(
                CMDResult.InvalidArgument(
                    args.first(),
                    "Команда exit не поддерживает аргументы"
                )
            )
    }

    override fun validate(vfs: VFS, args: List<String>): Boolean = args.isEmpty()

}