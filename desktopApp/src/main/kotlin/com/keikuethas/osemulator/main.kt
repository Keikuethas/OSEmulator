package com.keikuethas.osemulator

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.keikuethas.osemulator.mvi.CMDEvent

fun main(args: Array<String>) {
    application {
        val title = remember{ mutableStateOf("VFS?")}
        Window(
            onCloseRequest = ::exitApplication,
            title = title.value,
        ) {
            App(
                args = args,
                onEvent = {
                    when(it) {
                        CMDEvent.Exit -> exitApplication()
                        is CMDEvent.ChangeTitle -> {
                            title.value = it.title
                        }
                    }
                }
            )
        }
    }
}