package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.cmd.VFS
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

object RM : TerminalCommand("rm") {
    override suspend fun invoke(
        vfs: VFS,
        outputFlow: MutableSharedFlow<CMDResponse>,
        args: List<String>
    ) {
        val toRm = mutableListOf<VFS.VirtualContent>()
        if (args.isEmpty()) {
            if (vfs.curDir.value == vfs.root) outputFlow.emit(
                (
                        CMDResult.InvalidArgument(
                            vfs.curDir.value.fullPath,
                            "Нельзя удалить корень"
                        )
                        )
            )
            toRm.add(vfs.curDir.value)
        } else args.forEach {
            when (val local = vfs.getContent(it)) {
                null -> {
                    outputFlow.emit(
                        CMDResult.InvalidArgument(
                            it, "Путь не найден."
                        )
                    )
                }

                vfs.root -> outputFlow.emit(
                    (
                            CMDResult.InvalidArgument(it, "Нельзя удалить корень")
                            )
                )

                else -> toRm.add(local)
            }
        }

        for (what in toRm) {
            outputFlow.emit(
                CMDResult.Print(
                    "${what.fullPath} vs ${vfs.curDir.value.fullPath}"
                )
            )
            if (vfs.curDir.value.fullPath.contains(what.fullPath))
                outputFlow.emit(
                    CMDResult.Print(
                        vfs.goTo("${what.fullPath}/..".dropWhile { it != '/' }).toString()
                    )
                )
            what.remove()
        }
    }

    override fun validate(vfs: VFS, args: List<String>): Boolean {
        return super.validate(vfs, args)
    }
}