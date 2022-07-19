package com.mathroda.messengerclone.ui.channels.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.BubbleGray
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    border: BorderStroke = BorderStroke(0.dp, BubbleGray),
    innerPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit,
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }

    // Workaround to move cursor to the end after selecting a suggestion
    val selection = if (textFieldValueState.isCursorAtTheEnd()) {
        TextRange(value.length)
    } else {
        textFieldValueState.selection
    }

    val textFieldValue = textFieldValueState.copy(
        text = value,
        selection = selection
    )

    val description = stringResource(id = R.string.stream_compose_cd_message_input)

    BasicTextField(
        modifier = modifier
            .border(border = border, shape = ChatTheme.shapes.inputField)
            .clip(ChatTheme.shapes.inputField)
            .background(BubbleGray)
            .padding(innerPadding)
            .semantics { contentDescription = description },
        value = textFieldValue,
        onValueChange = {
            textFieldValueState = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        textStyle = ChatTheme.typography.body.copy(
            color = ChatTheme.colors.textHighEmphasis,
            textDirection = TextDirection.Content
        ),
        cursorBrush = SolidColor(ChatTheme.colors.primaryAccent),
        decorationBox = { innerTextField -> decorationBox(innerTextField) },
        maxLines = maxLines,
        singleLine = maxLines == 1,
        enabled = enabled
    )
}

/**
 * Check if the [TextFieldValue] state represents a UI with the cursor at the end of the input.
 *
 * @return True if the cursor is at the end of the input.
 */
private fun TextFieldValue.isCursorAtTheEnd(): Boolean {
    val textLength = text.length
    val selectionStart = selection.start
    val selectionEnd = selection.end

    return textLength == selectionStart && textLength == selectionEnd
}
