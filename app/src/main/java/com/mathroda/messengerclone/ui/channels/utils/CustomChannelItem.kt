package com.mathroda.messengerclone.ui.channels.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.state.channels.list.ChannelItemState
import io.getstream.chat.android.compose.ui.channels.list.ChannelItem
import io.getstream.chat.android.compose.ui.components.Timestamp
import io.getstream.chat.android.compose.ui.components.avatar.ChannelAvatar
import io.getstream.chat.android.compose.ui.components.channels.MessageReadStatusIcon
import io.getstream.chat.android.compose.ui.components.channels.UnreadCountIndicator
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.getLastMessage
import com.mathroda.messengerclone.R
import io.getstream.chat.android.compose.state.OnlineIndicatorAlignment
import io.getstream.chat.android.compose.ui.components.OnlineIndicator

@ExperimentalFoundationApi
@Composable
fun CustomChannelItem(
    channelItem: ChannelItemState,
    currentUser: User?,
    onChannelClick: (Channel) -> Unit,
    onChannelLongClick: (Channel) -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: @Composable RowScope.(ChannelItemState) -> Unit = {
        DefaultChannelItemLeadingContent(
            channelItem = it,
            currentUser = currentUser
        )
    },
    centerContent: @Composable RowScope.(ChannelItemState) -> Unit = {
        DefaultChannelItemCenterContent(
            channel = it.channel,
            isMuted = it.isMuted,
            currentUser = currentUser
        )
    },
    trailingContent: @Composable RowScope.(ChannelItemState) -> Unit = {
        DefaultChannelItemTrailingContent(
            channel = it.channel,
            currentUser = currentUser,
        )
    },
) {
    val channel = channelItem.channel
    val description = stringResource(id = R.string.stream_compose_cd_channel_item)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .semantics { contentDescription = description }
            .combinedClickable(
                onClick = { onChannelClick(channel) },
                onLongClick = { onChannelLongClick(channel) },
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingContent(channelItem)

            centerContent(channelItem)

            trailingContent(channelItem)
        }
    }
}

/**
 * Represents the default leading content of [ChannelItem], that shows the channel avatar.
 *
 * @param channelItem The channel to show the avatar of.
 * @param currentUser The currently logged in user.
 */
@Composable
internal fun DefaultChannelItemLeadingContent(
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
            .size(55.dp),
        channel = channelItem.channel,
        currentUser = currentUser,
        onlineIndicator = {
            DefaultOnlineIndicator(onlineIndicatorAlignment)
        }
    )
}

/**
 * Represents the center portion of [ChannelItem], that shows the channel display name
 * and the last message text preview.
 *
 * @param channel The channel to show the info for.
 * @param isMuted If the channel is muted for the current user.
 * @param currentUser The currently logged in user, used for data handling.
 */
@Composable
internal fun RowScope.DefaultChannelItemCenterContent(
    channel: Channel,
    isMuted: Boolean,
    currentUser: User?,
) {
    val unreadCount = channel.unreadCount
    Column(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
            .weight(1f)
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        val channelName: (@Composable (modifier: Modifier) -> Unit) = @Composable {
            Text(
                modifier = it,
                text = ChatTheme.channelNameFormatter.formatChannelName(channel, currentUser),
                style = if (unreadCount != null && unreadCount > 0) {ChatTheme.typography.bodyBold} else {ChatTheme.typography.body},
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (unreadCount != null && unreadCount > 0) {ChatTheme.colors.textHighEmphasis} else {ChatTheme.colors.textLowEmphasis},
            )
        }

        if (isMuted) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                channelName(Modifier.weight(weight = 1f, fill = false))

                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(16.dp),
                    painter = painterResource(id = R.drawable.stream_compose_ic_muted),
                    contentDescription = null,
                    tint = ChatTheme.colors.textLowEmphasis,
                )
            }
        } else {
            channelName(Modifier)
        }

        val lastMessageText = channel.getLastMessage(currentUser)?.let { lastMessage ->
            ChatTheme.messagePreviewFormatter.formatMessagePreview(lastMessage, currentUser)
        } ?: AnnotatedString("")

        if (lastMessageText.isNotEmpty()) {
            Text(
                text = lastMessageText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = if (unreadCount != null && unreadCount > 0) {ChatTheme.typography.bodyBold} else {ChatTheme.typography.body},
                color = if (unreadCount != null && unreadCount > 0) {ChatTheme.colors.textHighEmphasis} else {ChatTheme.colors.textLowEmphasis},
            )
        }
    }
}

/**
 * Represents the default trailing content for the channel item. By default it shows
 * the the information about the last message for the channel item, such as its read state,
 * timestamp and how many unread messages the user has.
 *
 * @param channel The channel to show the info for.
 * @param currentUser The currently logged in user, used for data handling.
 */
@Composable
internal fun RowScope.DefaultChannelItemTrailingContent(
    channel: Channel,
    currentUser: User?,
) {
    val lastMessage = channel.getLastMessage(currentUser)

    if (lastMessage != null) {
        Column(
            modifier = Modifier
                .padding(
                    start = 4.dp,
                    end = ChatTheme.dimens.channelItemHorizontalPadding,
                    top = ChatTheme.dimens.channelItemVerticalPadding,
                    bottom = ChatTheme.dimens.channelItemVerticalPadding
                )
                .wrapContentHeight()
                .align(Alignment.Bottom),
            horizontalAlignment = Alignment.End
        ) {
            val unreadCount = channel.unreadCount

            if (unreadCount != null && unreadCount > 0) {
                UnreadCountIndicator(
                    modifier = Modifier.padding(bottom = 4.dp),
                    unreadCount = unreadCount
                )
            }

            val isLastMessageFromCurrentUser = lastMessage.user.id == currentUser?.id

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLastMessageFromCurrentUser) {
                    MessageReadStatusIcon(
                        channel = channel,
                        message = lastMessage,
                        currentUser = currentUser,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(16.dp)
                    )
                }

                Timestamp(date = channel.lastUpdated)
            }
        }
    }
}
