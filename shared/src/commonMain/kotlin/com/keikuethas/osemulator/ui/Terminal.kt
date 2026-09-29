package com.keikuethas.osemulator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun Terminal(
    lines: List<AnnotatedString>,
    pending: AnnotatedString?,
    prompt: String,
    input: String,
    onInputChange: (String) -> Unit,
    onEnter: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black,
    textColor: Color = Color.White,
    promptColor: Color = Color(0xFF00FF66),
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    textStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
    ),
) {
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(lines.size) {
        snapshotFlow { listState.layoutInfo.totalItemsCount }
            .distinctUntilChanged()
            .collect { listState.scrollToItem(lines.size) }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { focusRequester.requestFocus() },
    ) {
        items(
            count = lines.size,
            key = { i -> i },
        ) { i ->
            Text(
                text = lines[i],
                style = textStyle,
            )
        }

        item(key = "input") {
            BasicTextField(
                value = input,
                onValueChange = onInputChange,
                textStyle = textStyle.copy(color = textColor),
                cursorBrush = SolidColor(textColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { e ->
                        if (e.type == KeyEventType.KeyDown && e.key == Key.Enter) {
                            onEnter(input)
                            true
                        } else false
                    },
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        if (pending != null) {
                            Text(text = pending, style = textStyle)
                        }
                        Text(
                            text = "$prompt ",
                            style = textStyle,
                            color = promptColor,
                        )
                        Box(Modifier.weight(1f)) {
                            innerTextField()
                        }
                    }
                },
            )
        }
    }
}