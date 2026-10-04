package com.keikuethas.osemulator.cmd

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

object VFS {

    private val _curDir = MutableStateFlow(
        File(System.getProperty("user.dir"))
    )

    val curDir = _curDir.asStateFlow()

    var path: String
        get() = _curDir.value.absolutePath
        set(value) {
            val candidate = File(value)
            val resolved = if (candidate.isAbsolute) candidate
            else File(_curDir.value, value)

            require(resolved.exists()) { "Не удалось найти путь: $value" }
            require(resolved.isDirectory) { "Путь не является директорией: $value" }
            _curDir.value = resolved.absoluteFile
        }
}