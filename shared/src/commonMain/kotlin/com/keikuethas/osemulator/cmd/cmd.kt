package com.keikuethas.osemulator.cmd

import com.keikuethas.osemulator.cmd.commands.CD
import com.keikuethas.osemulator.cmd.commands.Exit
import com.keikuethas.osemulator.cmd.commands.LS
import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest

object CMD {
    private val commands =
        listOf(CD, LS, Exit).associateBy { it.name.lowercase() }
    private val _outputFlow = MutableSharedFlow<CMDResponse>()
    val outputFlow = _outputFlow.asSharedFlow()
    private val vfsState = MutableStateFlow(VFS())
    @OptIn(ExperimentalCoroutinesApi::class)
    val vfsFlow = vfsState.flatMapLatest { it.curDir }

    fun setVFS(path: String) {
        vfsState.value = VFS(path)
    }
    suspend fun runScript(scriptPath: String) {
        val script = vfsState.value.getFile(scriptPath)

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

        commands[command]?.invoke(_outputFlow, argList)
            ?: _outputFlow.emit(CMDResult.InvalidCommand(command))
    }

    fun checkMessage(message: String): Boolean {
        val argList = message.split(' ').toMutableList()
        val command = argList.removeFirst().lowercase()

        return commands[command]?.validate(argList) ?: false
    }

}


