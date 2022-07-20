package com.mathroda.messengerclone.ui.channels.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.state.channels.list.ChannelItemState
import io.getstream.chat.android.compose.state.channels.list.ChannelsState
import io.getstream.chat.android.compose.ui.channels.list.*
import io.getstream.chat.android.compose.ui.components.EmptyContent
import io.getstream.chat.android.compose.ui.components.LoadingIndicator
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.channels.utils.CustomChannel
import com.mathroda.messengerclone.ui.channels.utils.CustomChannelItem
import com.mathroda.messengerclone.ui.channels.utils.CustomUserScrollerItem
import io.getstream.chat.android.compose.ui.components.LoadingFooter

@ExperimentalFoundationApi
@Composable
fun MessengerCloneChannelList(
    channelsState: ChannelsState,
    currentUser: User?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    lazyListState: LazyListState = rememberLazyListState(),
    onLastItemReached: () -> Unit = {},
    onChannelClick: (Channel) -> Unit = {},
    onChannelLongClick: (Channel) -> Unit = {},
    loadingContent: @Composable () -> Unit = { DefaultChannelListLoadingIndicator(modifier) },
    emptyContent: @Composable () -> Unit = { DefaultChannelListEmptyContent(modifier) },
    emptySearchContent: @Composable (String) -> Unit = { searchQuery ->
        DefaultChannelSearchEmptyContent(
            searchQuery = searchQuery,
            modifier = modifier
        )
    },
    helperContent: @Composable BoxScope.() -> Unit = {},
    loadingMoreContent: @Composable () -> Unit = { DefaultChannelsLoadingMoreIndicator() },
    itemContent: @Composable (ChannelItemState) -> Unit = { channelItem ->
        DefaultChannelItem(
            channelItem = channelItem,
            currentUser = currentUser,
            onChannelClick = onChannelClick,
            onChannelLongClick = onChannelLongClick
        )
    },
    divider: @Composable () -> Unit = { DefaultChannelItemDivider() },
) {
    val (isLoading, _, _, channels, searchQuery) = channelsState

    when {
        isLoading -> loadingContent()
        !isLoading && channels.isNotEmpty() -> CustomChannel(
            modifier = modifier,
            contentPadding = contentPadding,
            channelsState = channelsState,
            lazyListState = lazyListState,
            onLastItemReached = onLastItemReached,
            helperContent = helperContent,
            loadingMoreContent = loadingMoreContent,
            itemContent = itemContent,
            divider = divider,
        )
        searchQuery.isNotEmpty() -> emptySearchContent(searchQuery)
        else -> emptyContent()
    }
}

@ExperimentalFoundationApi
@Composable
internal fun DefaultChannelItem(
    channelItem: ChannelItemState,
    currentUser: User?,
    onChannelClick: (Channel) -> Unit,
    onChannelLongClick: (Channel) -> Unit,
) {
    CustomChannelItem(
        channelItem = channelItem,
        currentUser = currentUser,
        onChannelClick = onChannelClick,
        onChannelLongClick = onChannelLongClick
    )
}

/**
 * Default loading indicator.
 *
 * @param modifier Modifier for styling.
 */
@Composable
internal fun DefaultChannelListLoadingIndicator(modifier: Modifier) {
    LoadingIndicator(modifier)
}

/**
 * The default empty placeholder for the case when there are no channels available to the user.
 *
 * @param modifier Modifier for styling.
 */
@Composable
internal fun DefaultChannelListEmptyContent(modifier: Modifier = Modifier) {
    EmptyContent(
        modifier = modifier,
        painter = painterResource(id = R.drawable.stream_compose_empty_channels),
        text = stringResource(R.string.stream_compose_channel_list_empty_channels),
    )
}

/**
 * The default empty placeholder for the case when channel search returns no results.
 *
 * @param searchQuery The search query that returned no results.
 * @param modifier Modifier for styling.
 */
@Composable
internal fun DefaultChannelSearchEmptyContent(
    searchQuery: String,
    modifier: Modifier = Modifier,
) {
    EmptyContent(
        modifier = modifier,
        painter = painterResource(id = R.drawable.stream_compose_empty_search_results),
        text = stringResource(R.string.stream_compose_channel_list_empty_search_results, searchQuery),
    )
}

/**
 * Represents the default item divider in channel items.
 */
@Composable
 fun DefaultChannelItemDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.dp)
            .background(color = ChatTheme.colors.borders)
    )
}

@Composable
internal fun DefaultChannelsLoadingMoreIndicator() {
    LoadingFooter(modifier = Modifier.fillMaxWidth())
}