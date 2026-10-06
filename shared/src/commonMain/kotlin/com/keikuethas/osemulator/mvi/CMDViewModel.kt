package com.keikuethas.osemulator.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keikuethas.osemulator.cmd.CMD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CMDViewModel(args: Array<String>) : ViewModel() {
    private val _windowStateFlow = MutableStateFlow(CMDWindowState())
    private val _eventFlow = MutableSharedFlow<CMDEvent>(
        replay = 1,
        extraBufferCapacity = 16
    )
    private val _cmd = MutableStateFlow<CMD?>(null)

    val windowStateFlow = _windowStateFlow.asStateFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        observeCmd()
        handleArguments(args)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCmd() {
        viewModelScope.launch {
            _cmd.filterNotNull()
                .flatMapLatest { it.outputFlow }
                .collect { response ->
                    when (response) {
                        is CMDResult -> dispatch(response)
                        is CMDEvent  -> _eventFlow.emit(response)
                    }
                }
        }
        viewModelScope.launch {
            _cmd.filterNotNull()
                .flatMapLatest { it.vfsFlow }
                .collect { dir ->
                    _eventFlow.emit(CMDEvent.ChangeTitle(dir.fullPath))
                }
        }
    }

    private fun handleArguments(args: Array<String>) {
        if (args.isEmpty()) {
            dispatch(CMDResult.Print("Нет аргументов командной строки."))
            _cmd.value = CMD()
            return
        }

        dispatch(CMDResult.Print("Получены аргументы командной строки:"))
        args.forEach { dispatch(CMDResult.Print(it)) }

        val vfsArg = args.findLast { it.startsWith("vfs=", true) }
            ?.drop("vfs=".length)

        _cmd.value = if (vfsArg != null) {
            try {
                CMD(vfsArg)
            } catch (e: Exception) {
                dispatch(CMDResult.InvalidArgument(vfsArg, e.message))
                CMD()
            }
        } else CMD()

        args.findLast { it.startsWith("script=", true) }
            ?.drop("script=".length)
            ?.let { script ->
                viewModelScope.launch {
                    try {
                        _cmd.value!!.runScript(script)
                    } catch (e: Exception) {
                        dispatch(CMDResult.InvalidArgument(script, e.message))
                    }
                }
            }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            dispatch(CMDResult.Input(message))
            _cmd.value?.handleMessage(message)
        }
    }

    private fun dispatch(result: CMDResult) {
        _windowStateFlow.value = CMDReducer.reduce(_windowStateFlow.value, result)
    }
}