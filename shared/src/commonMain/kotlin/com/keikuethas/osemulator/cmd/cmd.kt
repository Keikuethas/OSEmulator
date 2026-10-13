package com.keikuethas.osemulator.cmd

import com.keikuethas.osemulator.cmd.commands.CD
import com.keikuethas.osemulator.cmd.commands.CP
import com.keikuethas.osemulator.cmd.commands.Exit
import com.keikuethas.osemulator.cmd.commands.LS
import com.keikuethas.osemulator.cmd.commands.RM
import com.keikuethas.osemulator.cmd.commands.Tree
import com.keikuethas.osemulator.cmd.commands.Uptime
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest

class CMD(vfsPath: String = "") {
    private val commands =
        listOf(CD, LS, Exit, Tree, Uptime, CP, RM).associateBy { it.name.lowercase() }
    private val _outputFlow = MutableSharedFlow<CMDResponse>(replay = 1, extraBufferCapacity = 128)
    val outputFlow = _outputFlow.asSharedFlow()
    private val vfsState = MutableStateFlow(VFS(vfsPath))

    @OptIn(ExperimentalCoroutinesApi::class)
    val vfsFlow = vfsState.flatMapLatest { it.curDir }

    suspend fun runScript(scriptPath: String) {
        val script = vfsState.value.getFileOnDisk(scriptPath)

        if (!script.exists() || !script.isFile) {
            _outputFlow.emit(
                CMDResult.InvalidArgument(
                    script.path,
                    "Файл не существует: ${script.path}"
                )
            )
            return
        } else if (!script.canRead()) {
            _outputFlow.emit(
                CMDResult.InvalidArgument(
                    script.path,
                    "Невозможно прочитать файл: ${script.path}"
                )
            )
            return
        }

        val lines = script.readLines()
        lines.forEach {
            if (checkMessage(it)) {
                _outputFlow.emit(CMDResult.Input(it))
                handleMessage(it)
            }
        }
    }

    suspend fun handleMessage(
        message: String
    ) {
        val argList = message.split(' ').toMutableList()

        val command = argList.removeFirst().lowercase()

        var i = 0
        while(i < argList.size) {
            if (argList[i].startsWith('"')) {
                argList[i] = argList[i].drop(1)
                while(i < argList.size-1 && !argList[i+1].endsWith('"')) {
                    argList[i] += " ${argList.removeAt(i + 1)}"
                }
                if (i < argList.size - 1)
                    argList[i] += " ${argList.removeAt(i + 1).dropLast(1)}"
            } else i++
        }

        commands[command]?.invoke(
            vfs = vfsState.value,
            outputFlow = _outputFlow,
            args = argList
        )
            ?: _outputFlow.emit(CMDResult.InvalidCommand(command))
    }

    fun checkMessage(message: String): Boolean {
        val argList = message.split(' ').toMutableList()
        val command = argList.removeFirst().lowercase()

        return commands[command]?.validate(vfsState.value, argList) ?: false
    }

}


