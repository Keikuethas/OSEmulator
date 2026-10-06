package com.keikuethas.osemulator.cmd.commands

import com.keikuethas.osemulator.cmd.VFS
import com.keikuethas.osemulator.mvi.CMDResponse
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class TerminalCommand() {
    var name: String = javaClass.simpleName
        private set

    constructor(name: String) : this() {
        this.name = name
    }

    abstract suspend operator fun invoke(
        vfs: VFS,
        outputFlow: MutableSharedFlow<CMDResponse>,
        args: List<String>,
    )

    open fun validate(vfs: VFS, args: List<String>): Boolean = true

}