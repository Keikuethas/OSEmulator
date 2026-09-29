package com.keikuethas.osemulator.cmd

import com.keikuethas.osemulator.mvi.CMDResponse
import kotlinx.coroutines.flow.MutableSharedFlow

suspend fun ls(outputFlow: MutableSharedFlow<CMDResponse>, args: List<String>) {
    placeholder(
        outputFlow,
        "ls",
        args
    )
}