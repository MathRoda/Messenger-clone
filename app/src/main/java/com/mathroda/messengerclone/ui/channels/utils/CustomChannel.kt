package com.mathroda.messengerclone.ui.channels.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.ui.channels.components.DefaultChannelsLoadingMoreIndicator
import com.mathroda.messengerclone.ui.channels.components.MessengerCloneScrollerChannel
import com.mathroda.messengerclone.ui.channels.components.MessengerCloneSearchInput
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.handlers.LoadMoreHandler
import io.getstream.chat.android.compose.state.channels.list.ChannelItemState
import io.getstream.chat.android.compose.state.channels.list.ChannelsState
import io.getstream.chat.android.compose.ui.components.LoadingFooter

@ExperimentalFoundationApi
@Composable
fun CustomChannel(
    channelsState: ChannelsState,
    lazyListState: LazyListState,
    onLastItemReached: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    helperContent: @Composable BoxScope.() -> Unit = {},
    loadingMoreContent: @Composable () -> Unit = { DefaultChannelsLoadingMoreIndicator() },
    itemContent: @Composable (ChannelItemState) -> Unit,
    divider: @Composable () -> Unit,
) {
    val (_, isLoadingMore, endOfChannels, channelItems) = channelsState

    Box(modifier = modifier) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = contentPadding
            ) {
                item {
                    DummyFirstChannelItem()
                }

                items(
                    items = channelItems,
                    key = { it.channel.cid }
                ) { item ->
                    itemContent(item)

                    divider()
                }

                if (isLoadingMore) {
                    item {
                        loadingMoreContent()
                    }
                }
            }

            if (!endOfChannels && channelItems.isNotEmpty()) {
                LoadMoreHandler(lazyListState) {
                    onLastItemReached()
                }
            }

        }
}

/**
 * The default loading more indicator.
 */
@Composable
internal fun DefaultChannelsLoadingMoreIndicator() {
    LoadingFooter(modifier = Modifier.fillMaxWidth())
}

/**
 * Represents an almost invisible dummy item to be added to the top of the list.
 *
 * If the list is scrolled to the top and a channel new item is added or moved
 * to the position above, then the list will automatically autoscroll to it.
 */
@Composable
private fun DummyFirstChannelItem() {
    Box(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
    )
}
