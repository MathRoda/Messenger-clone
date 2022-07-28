package com.mathroda.messengerclone.ui.messages.util.list

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.utils.*
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomMessageText(
    message: Message,
    messageItemState: MessageItemState,
    modifier: Modifier = Modifier,
    onLongItemClick: (Message) -> Unit
) {
    val context = LocalContext.current

    val styledText = buildAnnotatedMessageText(message)
    val annotations = styledText.getStringAnnotations(0, styledText.lastIndex)

    // TODO: Fix emoji font padding once this is resolved and exposed: https://issuetracker.google.com/issues/171394808
    val style = when {
        message.isSingleEmoji() -> ChatTheme.typography.singleEmoji
        message.isFewEmoji() -> ChatTheme.typography.emojiOnly
        message.isMyMessage() -> ChatTheme.typography.body.copy(
            color = Color.White
        )
        else -> ChatTheme.typography.body
    }

    if (message.text.isNotEmpty()) {
        ClickableText(
            modifier = modifier
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            text = message.text,
            style = style,
            onLongPress = { onLongItemClick(message) },
        ) { position ->
            val targetUrl = annotations.firstOrNull {
                position in it.start..it.end
            }?.item

            if (targetUrl != null && targetUrl.isNotEmpty()) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(targetUrl)
                    )
                )
            }
        }
    } else {
        val horizontalPadding = if (message.isEmojiOnlyWithoutBubble()) 0.dp else 12.dp
        val verticalPadding = if (message.isEmojiOnlyWithoutBubble()) 0.dp else 8.dp
        Text(
            modifier = modifier
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                )
                .clipToBounds(),
            text = styledText,
            style = style
        )
    }
}

@Composable
private fun ClickableText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onLongPress: () -> Unit,
    onClick: (Int) -> Unit,
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick, onLongPress) {
        detectTapGestures(
            onLongPress = { onLongPress() },
            onTap = { pos ->
                layoutResult.value?.let { layoutResult ->
                    onClick(layoutResult.getOffsetForPosition(pos))
                }
            }
        )
    }

    Text(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        }
    )
}

@Composable
internal fun buildAnnotatedMessageText(message: Message): AnnotatedString {
    return BuildAnnotatedMessageText(message.text)
}