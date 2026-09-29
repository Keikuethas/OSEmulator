package com.keikuethas.osemulator.mvi

import com.keikuethas.osemulator.ui.StyledText

data class CMDWindowState(
    val prompt: String = "$",
    val lines: List<StyledText> = emptyList(),
    val pendingString: StyledText? = null,
)