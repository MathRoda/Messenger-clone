package com.mathroda.messengerclone.ui.channels.utils

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.compose.handlers.LoadMoreHandler
import io.getstream.chat.android.compose.state.channels.list.ChannelItemState
import io.getstream.chat.android.compose.state.channels.list.ChannelsState

@Composable
fun CustomUserScroller(
    channelsState: ChannelsState,
    lazyListState: LazyListState,
    onLastItemReached: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    helperContent: @Composable BoxScope.() -> Unit = {},
    loadingMoreContent: @Composable () -> Unit = { DefaultChannelsLoadingMoreIndicator() },
    itemContent: @Composable (ChannelItemState) -> Unit,
) {
    val (_, isLoadingMore, endOfChannels, channelItems) = channelsState

    Box(modifier = modifier) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = lazyListState,
            verticalAlignment = Alignment.CenterVertically,
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

        helperContent()
    }
}

@Composable
private fun DummyFirstChannelItem() {
    Box(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
    )
}