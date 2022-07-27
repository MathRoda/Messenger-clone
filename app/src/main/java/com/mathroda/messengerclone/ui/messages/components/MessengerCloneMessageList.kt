package com.mathroda.messengerclone.ui.messages.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.messages.util.CustomMessageItem
import com.mathroda.messengerclone.ui.messages.util.CustomMessageSeparator
import com.mathroda.messengerclone.ui.messages.util.DefaultMessagesHelperContent
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResult
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResultType
import io.getstream.chat.android.compose.state.messages.list.GiphyAction
import io.getstream.chat.android.compose.state.messages.list.MessageListItemState
import io.getstream.chat.android.compose.ui.components.LoadingIndicator
import io.getstream.chat.android.compose.ui.messages.list.*
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.rememberMessageListState
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel

@Composable
fun MessengerCloneMessageList(
    viewModel: MessageListViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp),
    lazyListState: LazyListState =
        rememberMessageListState(parentMessageId = viewModel.currentMessagesState.parentMessageId),
    onThreadClick: (Message) -> Unit = { viewModel.openMessageThread(it) },
    onLongItemClick: (Message) -> Unit = { viewModel.selectMessage(it) },
    onReactionsClick: (Message) -> Unit = { viewModel.selectReactions(it) },
    onMessagesStartReached: () -> Unit = { viewModel.loadMore() },
    onLastVisibleMessageChanged: (Message) -> Unit = { viewModel.updateLastSeenMessage(it) },
    onScrollToBottom: () -> Unit = { viewModel.clearNewMessageState() },
    onGiphyActionClick: (GiphyAction) -> Unit = { viewModel.performGiphyAction(it) },
    onQuotedMessageClick: (Message) -> Unit = { viewModel.scrollToSelectedMessage(it) },
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {
        if (it?.resultType == ImagePreviewResultType.SHOW_IN_CHAT) {
            viewModel.focusMessage(it.messageId)
        }
    },
    loadingContent: @Composable () -> Unit = { DefaultMessageListLoadingIndicator(modifier) },
    emptyContent: @Composable () -> Unit = { DefaultMessageListEmptyContent(modifier) },
    helperContent: @Composable BoxScope.() -> Unit = {
        DefaultMessagesHelperContent(
            messagesState = viewModel.currentMessagesState,
            lazyListState = lazyListState,
        )
    },
    loadingMoreContent: @Composable () -> Unit = { DefaultMessagesLoadingMoreIndicator() },
    itemContent: @Composable (MessageListItemState) -> Unit = { messageListItem ->
        DefaultMessageContainer(
            messageListItem = messageListItem,
            onImagePreviewResult = onImagePreviewResult,
            onThreadClick = onThreadClick,
            onLongItemClick = onLongItemClick,
            onReactionsClick = onReactionsClick,
            onGiphyActionClick = onGiphyActionClick,
            onQuotedMessageClick = onQuotedMessageClick,
        )
    },
) {
    MessageList(
        modifier = modifier,
        contentPadding = contentPadding,
        currentState = viewModel.currentMessagesState,
        lazyListState = lazyListState,
        onMessagesStartReached = onMessagesStartReached,
        onLastVisibleMessageChanged = onLastVisibleMessageChanged,
        onLongItemClick = onLongItemClick,
        onReactionsClick = onReactionsClick,
        onScrolledToBottom = onScrollToBottom,
        onImagePreviewResult = onImagePreviewResult,
        itemContent = itemContent,
        helperContent = helperContent,
        loadingMoreContent = loadingMoreContent,
        loadingContent = loadingContent,
        emptyContent = emptyContent,
        onQuotedMessageClick = onQuotedMessageClick,
    )
}

@Composable
internal fun DefaultMessageContainer(
    messageListItem: MessageListItemState,
    onImagePreviewResult: (ImagePreviewResult?) -> Unit,
    onThreadClick: (Message) -> Unit,
    onLongItemClick: (Message) -> Unit,
    onReactionsClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit,
    onQuotedMessageClick: (Message) -> Unit,
) {
    MessageContainer(
        messageListItem = messageListItem,
        onLongItemClick = onLongItemClick,
        onReactionsClick = onReactionsClick,
        onThreadClick = onThreadClick,
        onGiphyActionClick = onGiphyActionClick,
        onImagePreviewResult = onImagePreviewResult,
        onQuotedMessageClick = onQuotedMessageClick,
        dateSeparatorContent = {
            CustomMessageSeparator(dateSeparator = it)
        },
        messageItemContent = {
            CustomMessageItem(
                messageItem = it,
                onLongItemClick = onLongItemClick,
                onThreadClick = onThreadClick,
                onReactionsClick = onReactionsClick,
                onGiphyActionClick = onGiphyActionClick,
                onQuotedMessageClick = onQuotedMessageClick,
                onImagePreviewResult = onImagePreviewResult
            )
        }
    )
}

@Composable
internal fun DefaultMessageListLoadingIndicator(modifier: Modifier) {
    LoadingIndicator(modifier)
}

@Composable
internal fun DefaultMessageListEmptyContent(modifier: Modifier) {
    Box(
        modifier = modifier.background(color = ChatTheme.colors.appBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.stream_compose_message_list_empty_messages),
            style = ChatTheme.typography.body,
            color = ChatTheme.colors.textLowEmphasis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun DefaultMessagesLoadingMoreIndicator() {
    LoadingIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    )
}