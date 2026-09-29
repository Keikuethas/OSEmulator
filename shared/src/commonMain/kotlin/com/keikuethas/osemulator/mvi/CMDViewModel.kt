package com.keikuethas.osemulator.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keikuethas.osemulator.cmd.handleMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CMDViewModel : ViewModel() {
    private val _windowStateFlow = MutableStateFlow(CMDWindowState())
    private val _callbackFlow = MutableSharedFlow<CMDResponse>()
    private val _eventFlow = MutableSharedFlow<CMDEvent>()
    val windowStateFlow = _windowStateFlow.asStateFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        observeResponse()
    }

    private fun observeResponse() {
        viewModelScope.launch {
            _callbackFlow.collect {
                if (it is CMDResult) dispatch(it)
                else if (it is CMDEvent) _eventFlow.emit(it)
            }
        }
    }


    fun sendMessage(message: String) {
        viewModelScope.launch {
            dispatch(CMDResult.Input(message))
            handleMessage(_callbackFlow, message)
        }
    }

    private fun dispatch(result: CMDResult) {
        _windowStateFlow.value  = CMDReducer.reduce(
            _windowStateFlow.value,
            result
        )
    }
}