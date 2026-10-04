package com.keikuethas.osemulator.mvi

interface CMDResponse

sealed interface CMDResult: CMDResponse {
    data class Input(val message: String): CMDResult
    data class Print(val message: String, val sendEOL: Boolean = true): CMDResult
    data class InvalidCommand(
        val value: String,
        val description: String? = null
    ): CMDResult
    data class InvalidArgument(
        val value: String,
        val description: String? = null
    ): CMDResult
}

sealed interface CMDEvent: CMDResponse {
    data object Exit: CMDEvent
    data class ChangeTitle(val title: String): CMDEvent
}