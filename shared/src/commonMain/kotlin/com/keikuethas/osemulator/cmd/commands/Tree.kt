package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.cmd.VFS
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

object Tree : TerminalCommand("tree") {
    override suspend fun invoke(
        vfs: VFS,
        outputFlow: MutableSharedFlow<CMDResponse>,
        args: List<String>
    ) {

        if (args.size >= 2) {
            outputFlow.emit(
                CMDResult.InvalidArgument(
                    args[1],
                    "Команда tree не поддерживает больше 1 аргумента"
                )
            )
            return
        }

        val dir: VFS.Directory? = if (args.isEmpty()) vfs.curDir.value
        else vfs.getContent(args.first()) as? VFS.Directory

        if (dir == null) {
            outputFlow.emit(
                CMDResult.InvalidArgument(
                    args.first(),
                    "Путь не существует или не является директорией"
                )
            )
            return
        }

        suspend fun buildTree(dir: VFS.Directory, level: Int = 0) {
            dir.children.sortedWith(
                compareBy<VFS.VirtualContent> { if (it is VFS.Directory) 1 else 0 }
                    .thenBy { it.name }
            ).forEach {
                outputFlow.emit(
                    CMDResult.Print(
                        "${"- ".repeat(level)}${it.name}"
                    )
                )

                if (it is VFS.Directory)
                    buildTree(it, level + 1)

            }
        }

        buildTree(dir)
    }

    override fun validate(vfs: VFS, args: List<String>): Boolean {
        if (args.size >= 2) return false

        val dir: VFS.Directory? = if (args.isEmpty()) vfs.curDir.value
        else vfs.getContent(args.first()) as? VFS.Directory

        return dir != null
    }
}