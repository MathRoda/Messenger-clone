package com.mathroda.messengerclone.ui.messages.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.utils.isDeleted
import com.mathroda.messengerclone.utils.isGiphyEphemeral
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResult
import io.getstream.chat.android.compose.state.messages.list.GiphyAction
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.attachments.content.MessageAttachmentsContent
import io.getstream.chat.android.compose.ui.components.messages.GiphyMessageContent
import io.getstream.chat.android.compose.ui.components.messages.QuotedMessage
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomMessageContent(
    message: Message,
    messageItemState: MessageItemState,
    modifier: Modifier = Modifier,
    onLongItemClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit = {},
    onQuotedMessageClick: (Message) -> Unit = {},
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {},
    giphyEphemeralContent: @Composable () -> Unit = {
        DefaultMessageGiphyContent(
            message = message,
            onGiphyActionClick = onGiphyActionClick
        )
    },
    deletedMessageContent: @Composable () -> Unit = {
        DefaultMessageDeletedContent(modifier = modifier)
    },
    regularMessageContent: @Composable () -> Unit = {
        DefaultMessageContent(
            message = message,
            onLongItemClick = onLongItemClick,
            onImagePreviewResult = onImagePreviewResult,
            onQuotedMessageClick = onQuotedMessageClick,
            messageItemState = messageItemState
        )
    },
) {
    when {
        message.isGiphyEphemeral() -> giphyEphemeralContent()
        message.isDeleted() -> deletedMessageContent()
        else -> regularMessageContent()
    }
}

@Composable
internal fun DefaultMessageGiphyContent(
    message: Message,
    onGiphyActionClick: (GiphyAction) -> Unit,
) {
    GiphyMessageContent(
        message = message,
        onGiphyActionClick = onGiphyActionClick
    )
}

@Composable
internal fun DefaultMessageDeletedContent(
    modifier: Modifier,
) {
    Text(
        modifier = modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 8.dp,
                bottom = 8.dp
            ),
        text = stringResource(id = R.string.stream_compose_message_deleted),
        color = ChatTheme.colors.textLowEmphasis,
        style = ChatTheme.typography.footnoteItalic
    )
}

@Composable
internal fun DefaultMessageContent(
    message: Message,
    messageItemState: MessageItemState,
    onLongItemClick: (Message) -> Unit,
    onImagePreviewResult: (ImagePreviewResult?) -> Unit,
    onQuotedMessageClick: (Message) -> Unit,
) {
    Column {
        MessageAttachmentsContent(
            message = message,
            onLongItemClick = onLongItemClick,
            onImagePreviewResult = onImagePreviewResult,
        )

        if (message.text.isNotEmpty()) {
            DefaultMessageTextContent(
                message = message,
                onLongItemClick = onLongItemClick,
                onQuotedMessageClick = onQuotedMessageClick,
                messageItemState = messageItemState
            )
        }
    }
}

@Composable
internal fun DefaultMessageTextContent(
    message: Message,
    messageItemState: MessageItemState,
    onLongItemClick: (Message) -> Unit,
    onQuotedMessageClick: (Message) -> Unit,
) {
    val quotedMessage = message.replyTo

    Column {
        if (quotedMessage != null) {
            QuotedMessage(
                modifier = Modifier.padding(2.dp),
                message = quotedMessage,
                onLongItemClick = { onLongItemClick(message) },
                onQuotedMessageClick = onQuotedMessageClick
            )
        }
        CustomMessageText(
            message = message,
            onLongItemClick = onLongItemClick,
            messageItemState = messageItemState
        )
    }
}



