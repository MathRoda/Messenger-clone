package com.mathroda.messengerclone.ui.channels.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathroda.messengerclone.R
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.state.OnlineIndicatorAlignment
import io.getstream.chat.android.compose.state.channels.list.ChannelItemState
import io.getstream.chat.android.compose.ui.components.avatar.ChannelAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@ExperimentalFoundationApi
@Composable
fun CustomUserScrollerItem(
    channelItem: ChannelItemState,
    currentUser: User?,
    onChannelClick: (Channel) -> Unit,
    content: @Composable ColumnScope.(ChannelItemState) -> Unit = {
        DefaultChannelItemContent(
            channelItem = it,
            currentUser = currentUser,
        )
    },
) {
    val channel = channelItem.channel
    val description = stringResource(id = R.string.stream_compose_cd_channel_item)

    Column(
        modifier = Modifier
            .wrapContentSize()
            .semantics { contentDescription = description }
            .combinedClickable(
                onClick = { onChannelClick(channel) },
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        content(channelItem)
    }
}

@Composable
internal fun DefaultChannelItemContent(
    channelItem: ChannelItemState,
    currentUser: User?,
    onlineIndicatorAlignment: OnlineIndicatorAlignment = OnlineIndicatorAlignment.BottomEnd,
) {
    ChannelAvatar(
        modifier = Modifier
            .padding(
                start = ChatTheme.dimens.channelItemHorizontalPadding,
                end = 6.dp,
                top = ChatTheme.dimens.channelItemVerticalPadding,
                bottom = ChatTheme.dimens.channelItemVerticalPadding
            )
            .size(60.dp),
        channel = channelItem.channel,
        currentUser = currentUser,
        onlineIndicator = {
            DefaultOnlineIndicator(onlineIndicatorAlignment)
        }
    )

}
