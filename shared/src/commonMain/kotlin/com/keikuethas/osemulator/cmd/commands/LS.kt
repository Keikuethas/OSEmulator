package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.cmd.placeholder
import com.keikuethas.osemulator.mvi.CMDResponse
import kotlinx.coroutines.flow.MutableSharedFlow

object LS: TerminalCommand() {
    override suspend fun invoke(
        outputFlow: MutableSharedFlow<CMDResponse>,
        args: List<String>
    ) = placeholder(outputFlow, "ls", args)

    override fun validate(args: List<String>): Boolean = true

}