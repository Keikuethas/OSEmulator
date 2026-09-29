package com.keikuethas.osemulator.cmd

import com.keikuethas.osemulator.mvi.CMDResponse
import com.keikuethas.osemulator.mvi.CMDResult
import kotlinx.coroutines.flow.MutableSharedFlow

suspend fun placeholder(
    outputFlow: MutableSharedFlow<CMDResponse>,
    name: String,
    args: List<String>
) {
    outputFlow.emit(CMDResult.Print("Вызвана команда $name"))
    if (args.isNotEmpty()) outputFlow.emit(CMDResult.Print("Аргументы:"))
    for (i in args.indices)
        outputFlow.emit(CMDResult.Print("[$i] ${args[i]}"))
}