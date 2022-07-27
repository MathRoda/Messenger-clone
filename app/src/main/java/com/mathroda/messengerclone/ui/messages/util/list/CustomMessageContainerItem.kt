package com.mathroda.messengerclone.ui.messages.util.list

import androidx.compose.runtime.Composable
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResult
import io.getstream.chat.android.compose.state.messages.list.GiphyAction
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.messages.list.MessageItem

@Composable
fun CustomMessageContainerItem(
    messageItem: MessageItemState,
    onLongItemClick: (Message) -> Unit,
    onReactionsClick: (Message) -> Unit = {},
    onThreadClick: (Message) -> Unit,
    onGiphyActionClick: (GiphyAction) -> Unit,
    onQuotedMessageClick: (Message) -> Unit,
    onImagePreviewResult: (ImagePreviewResult?) -> Unit,
) {
    CustomMessageItem(
        messageItem = messageItem,
        onLongItemClick = onLongItemClick,
        onReactionsClick = onReactionsClick,
        onThreadClick = onThreadClick,
        onGiphyActionClick = onGiphyActionClick,
        onQuotedMessageClick = onQuotedMessageClick,
        onImagePreviewResult = onImagePreviewResult,
    )
}

