package com.keikuethas.osemulator

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.keikuethas.osemulator.mvi.CMDEvent

fun main() {
    application {
        val title = mutableStateOf("VFS")
        Window(
            onCloseRequest = ::exitApplication,
            title = title.value,
        ) {
            App(
                onEffect = {
                    when(it) {
                        CMDEvent.Exit -> exitApplication()
                        is CMDEvent.ChangeTitle -> title.value = it.title
                    }
                }
            )
        }
    }
}