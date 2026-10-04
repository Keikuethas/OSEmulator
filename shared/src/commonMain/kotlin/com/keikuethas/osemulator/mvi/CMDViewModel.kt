package com.keikuethas.osemulator.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keikuethas.osemulator.cmd.CMD
import com.keikuethas.osemulator.cmd.CMD.handleMessage
import com.keikuethas.osemulator.cmd.VFS
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
        with(args)
        {
            if (isNotEmpty()) {
                dispatch(CMDResult.Print("Получены аргументы командной строки:"))
                forEach {
                    dispatch(CMDResult.Print(it))
                }

                viewModelScope.launch {
                    forEach { arg ->
                        try {
                            when {
                                arg.startsWith("vfs=", ignoreCase = true) ->
                                    VFS.path = arg.drop("vfs=".length)

                                arg.startsWith("script=", ignoreCase = true) ->
                                    CMD.runScript(arg.drop("script=".length))

                                else -> dispatch(
                                    CMDResult.InvalidArgument(
                                        arg,
                                        "Неизвестное имя аргумента."
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            dispatch(CMDResult.InvalidArgument(arg, e.message))
                        }
                    }
                }
            } else dispatch(CMDResult.Print("Нет аргументов командной строки."))
        }
    }

    private fun observeVFS() {
        viewModelScope.launch {
            VFS.curDir.collect {
                _eventFlow.emit(CMDEvent.ChangeTitle(VFS.path))
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