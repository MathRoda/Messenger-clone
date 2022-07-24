package com.mathroda.messengerclone.ui.channels.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.ui.channels.utils.CustomUserScroller
import com.mathroda.messengerclone.ui.channels.utils.CustomUserScrollerItem
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.state.channels.list.ChannelItemState
import io.getstream.chat.android.compose.state.channels.list.ChannelsState

@ExperimentalFoundationApi
@Composable
fun MessengerCloneScrollerChannel(
    channelsState: ChannelsState,
    currentUser: User?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    lazyListState: LazyListState = rememberLazyListState(),
    onLastItemReached: () -> Unit = {},
    onChannelClick: (Channel) -> Unit = {},
    loadingContent: () -> Unit = {},
    helperContent: @Composable BoxScope.() -> Unit = {},
    loadingMoreContent: @Composable () -> Unit = { DefaultChannelsLoadingMoreIndicator() },
    itemContent: @Composable (ChannelItemState) -> Unit = { channelItem ->
        DefaultScrollerChannelItem(
            channelItem = channelItem,
            currentUser = currentUser,
            onChannelClick = onChannelClick,
        )
    },
) {
    val (isLoading, _, _, channels) = channelsState

    when {
         isLoading -> loadingContent()
        !isLoading && channels.isNotEmpty() -> CustomUserScroller(
            modifier = modifier,
            contentPadding = contentPadding,
            channelsState = channelsState,
            lazyListState = lazyListState,
            onLastItemReached = onLastItemReached,
            helperContent = helperContent,
            loadingMoreContent = loadingMoreContent,
            itemContent = itemContent,
        )

    }
}

@ExperimentalFoundationApi
@Composable
internal fun DefaultScrollerChannelItem(
    channelItem: ChannelItemState,
    currentUser: User?,
    onChannelClick: (Channel) -> Unit,
) {
    CustomUserScrollerItem(
        channelItem = channelItem,
        currentUser = currentUser,
        onChannelClick = onChannelClick,
    )
}