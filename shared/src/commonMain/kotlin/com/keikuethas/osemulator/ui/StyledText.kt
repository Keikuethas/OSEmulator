package com.keikuethas.osemulator.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

/**
 * Стилизованный текст: список фрагментов, каждый со своим цветом.
 * Не зависит от Compose — это чистый data-слой.
 */
data class StyledText(
    val spans: List<Span>,
) {
    /**
     * @param rgb 0xRRGGBB. Альфа подразумевается непрозрачной.
     */
    data class Span(
        val rgb: Int,
        val text: String,
    )

    companion object {
        fun of(text: String, rgb: Int = 0xFFFFFF): StyledText =
            StyledText(listOf(Span(rgb, text)))

        fun of(vararg spans: Span): StyledText =
            StyledText(spans.toList())
    }
}


fun StyledText.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    spans.forEach { span ->
        withStyle(SpanStyle(color = Color(0xFF000000.toInt() or span.rgb))) {
            append(span.text)
        }
    }
}

object TerminalColors {
    const val Text: Int    = 0xFFFFFF
    const val Prompt: Int  = 0x00FF66
    const val Error: Int   = 0xFF5555
    const val Muted: Int   = 0x888888
    const val Success: Int = 0x55FF55
}