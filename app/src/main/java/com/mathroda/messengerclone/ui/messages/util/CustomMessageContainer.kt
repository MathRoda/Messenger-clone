package com.mathroda.messengerclone.ui.messages.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.BottomSelected
import com.mathroda.messengerclone.ui.theme.BubbleGray
import com.mathroda.messengerclone.utils.isDeleted
import com.mathroda.messengerclone.utils.isEmojiOnlyWithoutBubble
import com.mathroda.messengerclone.utils.isFailed
import com.mathroda.messengerclone.utils.isGiphyEphemeral
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResult
import io.getstream.chat.android.compose.state.messages.list.GiphyAction
import io.getstream.chat.android.compose.state.messages.list.MessageItemGroupPosition
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.components.messages.MessageBubble
import io.getstream.chat.android.compose.ui.components.messages.MessageContent
import io.getstream.chat.android.compose.ui.messages.list.MessageItem
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomMessageItem(
    messageItem: MessageItemState,
    onLongItemClick: (Message) -> Unit,
    onReactionsClick: (Message) -> Unit = {},
    onThreadClick: (Message) -> Unit,
    onGiphyActionClick: (GiphyAction) -> Unit,
    onQuotedMessageClick: (Message) -> Unit,
    onImagePreviewResult: (ImagePreviewResult?) -> Unit,
) {
    MessageItem(
        messageItem = messageItem,
        onLongItemClick = onLongItemClick,
        onReactionsClick = onReactionsClick,
        onThreadClick = onThreadClick,
        onGiphyActionClick = onGiphyActionClick,
        onQuotedMessageClick = onQuotedMessageClick,
        onImagePreviewResult = onImagePreviewResult,
        centerContent = {
            DefaultMessageItemContent(
                messageItem = it,
                onLongItemClick = onLongItemClick,
                onImagePreviewResult = onImagePreviewResult,
                onGiphyActionClick = onGiphyActionClick,
                onQuotedMessageClick = onQuotedMessageClick,
            )
        }
    )
}

@Composable
internal fun DefaultMessageItemContent(
    messageItem: MessageItemState,
    onLongItemClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit = {},
    onQuotedMessageClick: (Message) -> Unit = {},
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {},
) {
    val modifier = Modifier.widthIn(max = ChatTheme.dimens.messageItemMaxWidth)
    if (messageItem.message.isEmojiOnlyWithoutBubble()) {
       EmojiMessageContent(
            modifier = modifier,
            messageItem = messageItem,
            onLongItemClick = onLongItemClick,
            onGiphyActionClick = onGiphyActionClick,
            onImagePreviewResult = onImagePreviewResult,
            onQuotedMessageClick = onQuotedMessageClick
        )
    } else {
        RegularMessageContent(
            modifier = modifier,
            messageItem = messageItem,
            onLongItemClick = onLongItemClick,
            onGiphyActionClick = onGiphyActionClick,
            onImagePreviewResult = onImagePreviewResult,
            onQuotedMessageClick = onQuotedMessageClick
        )
    }
    }

@Composable
internal fun EmojiMessageContent(
    messageItem: MessageItemState,
    modifier: Modifier = Modifier,
    onLongItemClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit = {},
    onQuotedMessageClick: (Message) -> Unit = {},
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {},
) {
    val message = messageItem.message

    if (!messageItem.isFailed()) {
        MessageContent(
            message = message,
            onLongItemClick = onLongItemClick,
            onGiphyActionClick = onGiphyActionClick,
            onImagePreviewResult = onImagePreviewResult,
            onQuotedMessageClick = onQuotedMessageClick
        )
    } else {
        Box(modifier = modifier) {
            MessageContent(
                message = message,
                onLongItemClick = onLongItemClick,
                onGiphyActionClick = onGiphyActionClick,
                onImagePreviewResult = onImagePreviewResult,
                onQuotedMessageClick = onQuotedMessageClick
            )

            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd),
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                tint = ChatTheme.colors.errorAccent
            )
        }
    }
}

@Composable
internal fun RegularMessageContent(
    messageItem: MessageItemState,
    modifier: Modifier = Modifier,
    onLongItemClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit = {},
    onQuotedMessageClick: (Message) -> Unit = {},
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {},
) {
    val (message, position, _, ownsMessage, _) = messageItem

    val messageBubbleShape = when (position) {
        MessageItemGroupPosition.Top, MessageItemGroupPosition.Middle -> RoundedCornerShape(16.dp)
        else -> {
            if (ownsMessage) ChatTheme.shapes.myMessageBubble else ChatTheme.shapes.otherMessageBubble
        }
    }

    val messageBubbleColor = when {
        message.isGiphyEphemeral() -> ChatTheme.colors.giphyMessageBackground
        message.isDeleted() -> ChatTheme.colors.barsBackground
        ownsMessage -> BottomSelected
        else -> BubbleGray
    }

     if (!messageItem.isFailed()) {
        MessageBubble(
            modifier = modifier,
            shape = messageBubbleShape,
            color = messageBubbleColor,
            border = if (message.isDeleted()) BorderStroke(1.dp, ChatTheme.colors.borders) else null,
            content = {
                CustomMessageContent(
                    message = message,
                    onLongItemClick = onLongItemClick,
                    onGiphyActionClick = onGiphyActionClick,
                    onImagePreviewResult = onImagePreviewResult,
                    onQuotedMessageClick = onQuotedMessageClick,
                    messageItemState = messageItem
                )
            }
        )
    } else {
        Box(modifier = modifier) {
            MessageBubble(
                modifier = Modifier.padding(end = 12.dp),
                shape = messageBubbleShape,
                color = messageBubbleColor,
                content = {
                    MessageContent(
                        message = message,
                        onLongItemClick = onLongItemClick,
                        onGiphyActionClick = onGiphyActionClick,
                        onImagePreviewResult = onImagePreviewResult,
                        onQuotedMessageClick = onQuotedMessageClick
                    )
                }
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd),
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                tint = ChatTheme.colors.errorAccent
            )
        }
    }
}
