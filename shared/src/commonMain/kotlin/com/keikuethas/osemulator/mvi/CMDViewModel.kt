package com.keikuethas.osemulator.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keikuethas.osemulator.cmd.CMD
import com.keikuethas.osemulator.cmd.CMD.handleMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CMDViewModel(
    args: Array<String>
) : ViewModel() {
    private val _windowStateFlow = MutableStateFlow(CMDWindowState())
    private val _eventFlow = MutableSharedFlow<CMDEvent>(replay = 1)
    val windowStateFlow = _windowStateFlow.asStateFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        observeResponse()
        observeVFS()
        handleArguments(args)
    }

    private fun handleArguments(args: Array<String>) {

        if (args.isNotEmpty()) {
            dispatch(CMDResult.Print("Получены аргументы командной строки:"))
            args.forEach {
                dispatch(CMDResult.Print(it))
            }


            args.findLast { it.startsWith("vfs=", ignoreCase = true) }?.drop("vfs=".length)?.let {
                    try {
                        CMD.setVFS(it)
                    } catch (e: Exception) {
                        dispatch(CMDResult.InvalidArgument(it, e.message))
                    }
                }


            args.findLast { it.startsWith("script=", ignoreCase = true) }?.drop("script=".length)
                ?.let {
                    viewModelScope.launch {
                        try {
                            CMD.runScript(it)
                        } catch (e: Exception) {
                            dispatch(CMDResult.InvalidArgument(it, e.message))
                        }

                    }
                }
        } else dispatch(CMDResult.Print("Нет аргументов командной строки."))
    }

    private fun observeVFS() {
        viewModelScope.launch {
            CMD.vfsFlow.collect {dir ->
                _eventFlow.emit(CMDEvent.ChangeTitle(dir.fullPath))
            }
        }
    }

    private fun observeResponse() {
        viewModelScope.launch {
            CMD.outputFlow.collect {
                when (it) {
                    is CMDResult -> dispatch(it)
                    is CMDEvent -> _eventFlow.emit(it)
                }
            }
        }
    }


    fun sendMessage(message: String) {
        viewModelScope.launch {
            dispatch(CMDResult.Input(message))
            handleMessage(message)
        }
    }

    private fun dispatch(result: CMDResult) {
        _windowStateFlow.value = CMDReducer.reduce(
            _windowStateFlow.value, result
        )
    }
}