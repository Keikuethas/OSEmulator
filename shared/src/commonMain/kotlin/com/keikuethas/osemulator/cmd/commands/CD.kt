package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.cmd.VFS
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

object CD : TerminalCommand("cd") {

    override suspend fun invoke(
        vfs: VFS,
        outputFlow: MutableSharedFlow<CMDResponse>,
        args: List<String>
    ) {
        if (args.size >= 2) {
            outputFlow.emit(
                CMDResult.InvalidArgument(
                    args[1],
                    "Команда cd не поддерживает больше 1 аргумента"
                )
            )
            return
        }

        if (args.isEmpty()) return

        if (!vfs.goTo(args.first())) {
            outputFlow.emit(
                CMDResult.InvalidArgument(
                    args.first(),
                    "Путь не существует или не является директорией"
                )
            )
            return
        }
    }

    override fun validate(vfs: VFS, args: List<String>): Boolean {
        if (args.size >= 2) return false

        val dir: VFS.Directory? = if (args.isEmpty()) vfs.curDir.value
        else vfs.getContent(args.first()) as? VFS.Directory

        return dir != null
    }

}