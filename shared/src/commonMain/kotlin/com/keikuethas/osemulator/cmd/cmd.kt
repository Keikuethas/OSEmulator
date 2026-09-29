package com.keikuethas.osemulator.cmd

import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

val commandList: Map<
        String,
        suspend (
            MutableSharedFlow<CMDResponse>,
            List<String>
        ) -> Unit
        > = mapOf(
    "ls" to ::ls,
    "cd" to ::cd,
    "exit" to ::exit,
)

suspend fun handleMessage(
    outputFlow: MutableSharedFlow<CMDResponse>,
    message: String
) {
    val argList = message.split(' ').toMutableList()
    val command = argList.removeFirst()

    if (command in commandList)
        commandList[command]?.let { it(outputFlow, argList) }
    else outputFlow.emit(CMDResult.InvalidCommand(command))
}