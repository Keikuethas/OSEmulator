package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.cmd.VFS
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

object CP : TerminalCommand("cp") {
    override suspend fun invoke(
        vfs: VFS, outputFlow: MutableSharedFlow<CMDResponse>, args: List<String>
    ) {
        if (args.size < 2) {
            outputFlow.emit(
                CMDResult.InvalidArgument(
                    args.firstOrNull() ?: "", "Команде CP требуется минимум 2 аргумента"
                )
            )
            return
        }

        val dest = vfs.getContent(args.last()) as? VFS.Directory
        if (dest == null) {
            outputFlow.emit(
                CMDResult.InvalidArgument(
                    args.last(), "Путь не существует или не является директорией."
                )
            )
            return
        }

        val toCp = mutableListOf<VFS.VirtualContent>()
        for (it in args.dropLast(1)) {
            val local = vfs.getContent(it)
            if (local == null) {
                outputFlow.emit(
                    CMDResult.InvalidArgument(
                        it, "Путь не найден."
                    )
                )
                continue
            }

            if ( local.fullPath.startsWith(dest.fullPath)) {
                outputFlow.emit(
                    CMDResult.InvalidArgument(
                        it, "Нельзя скопировать родительскую директорию в дочернюю."
                    )
                )
                continue
            }

            if (dest.children.any { child -> child.name == local.name }) {
                outputFlow.emit(
                    CMDResult.InvalidArgument(
                        it, "В папке назначения уже есть файл с таким именем."
                    )
                )
                continue
            }
            toCp.add(local)

        }

        if (toCp.size < args.size - 1) return

        for (what in toCp) {
            dest.addChild(what.copy())
        }
    }

    override fun validate(vfs: VFS, args: List<String>): Boolean {
        if (args.size < 2) return false

        val dest = vfs.getContent(args.last()) as? VFS.Directory ?: return false

        for (i in 0..args.size - 2) {
            val what = vfs.getContent(args[i]) ?: return false

            if (dest.fullPath in what.fullPath) return false
        }

        return true
    }
}