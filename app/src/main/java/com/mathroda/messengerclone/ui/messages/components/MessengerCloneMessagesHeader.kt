package com.mathroda.messengerclone.ui.messages.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.CustomBurble
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.common.state.MessageMode
import io.getstream.chat.android.compose.ui.components.BackButton
import io.getstream.chat.android.compose.ui.components.NetworkLoadingIndicator
import io.getstream.chat.android.compose.ui.components.TypingIndicator
import io.getstream.chat.android.compose.ui.components.avatar.ChannelAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.getMembersStatusText
import io.getstream.chat.android.compose.ui.util.mirrorRtl
import io.getstream.chat.android.offline.model.connection.ConnectionState

@Composable
fun MessengerCloneMessagesHeader(
    channel: Channel,
    currentUser: User?,
    modifier: Modifier = Modifier,
    typingUsers: List<User> = emptyList(),
    messageMode: MessageMode = MessageMode.Normal,
    connectionState: ConnectionState = ConnectionState.CONNECTED,
    color: Color = ChatTheme.colors.barsBackground,
    shape: Shape = ChatTheme.shapes.header,
    elevation: Dp = ChatTheme.dimens.headerElevation,
    onBackPressed: () -> Unit = {},
    onHeaderActionClick: (Channel) -> Unit = {},
    leadingContent: @Composable RowScope.() -> Unit = {
        DefaultHeaderLeadingContent(
            onBackPressed = onBackPressed,
            channel = channel,
            currentUser = currentUser
        )

        DefaultHeaderCenterContent(
            modifier = Modifier,
            channel = channel,
            currentUser = currentUser,
            typingUsers = typingUsers,
            messageMode = messageMode,
            onHeaderActionClick = onHeaderActionClick,
            connectionState = connectionState
        )
    },
    trailingContent: @Composable RowScope.() -> Unit = {
        DefaultMessageHeaderTrailingContent()
    }
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        elevation = elevation,
        color = color,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            leadingContent()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ){
            trailingContent()
        }
    }
}

@Composable
fun DefaultHeaderLeadingContent(
    onBackPressed: () -> Unit,
    channel: Channel,
    currentUser: User?
) {
    val layoutDirection = LocalLayoutDirection.current

    IconButton(
        modifier =  Modifier.mirrorRtl(layoutDirection = layoutDirection),
        onClick = onBackPressed
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
            tint = CustomBurble,
        )
    }

    ChannelAvatar(
        modifier = Modifier.size(40.dp),
        channel = channel,
        currentUser = currentUser,
        contentDescription = channel.name,
    )
}

@Composable
 fun DefaultHeaderCenterContent(
    channel: Channel,
    currentUser: User?,
    modifier: Modifier = Modifier,
    typingUsers: List<User> = emptyList(),
    messageMode: MessageMode = MessageMode.Normal,
    onHeaderActionClick: (Channel) -> Unit = {},
    connectionState: ConnectionState = ConnectionState.CONNECTED,
) {
    val title = when (messageMode) {
        MessageMode.Normal -> ChatTheme.channelNameFormatter.formatChannelName(channel, currentUser)
        is MessageMode.MessageThread -> stringResource(id = R.string.stream_compose_thread_title)
    }

    val subtitle = when (messageMode) {
        MessageMode.Normal -> channel.getMembersStatusText(LocalContext.current, currentUser)
        is MessageMode.MessageThread -> stringResource(
            R.string.stream_compose_thread_subtitle,
            ChatTheme.channelNameFormatter.formatChannelName(channel, currentUser)
        )
    }

    Column(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .padding(start = 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onHeaderActionClick(channel) }
            ),
    ) {
        Text(
            text = title,
            style = ChatTheme.typography.bodyBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = ChatTheme.colors.textHighEmphasis,
        )

        when (connectionState) {
            ConnectionState.CONNECTED -> {
                DefaultMessageHeaderSubtitle(
                    subtitle = subtitle,
                    typingUsers = typingUsers
                )
            }
            ConnectionState.CONNECTING -> {
                NetworkLoadingIndicator(
                    modifier = Modifier.wrapContentHeight(),
                    spinnerSize = 12.dp,
                    textColor = ChatTheme.colors.textLowEmphasis,
                    textStyle = ChatTheme.typography.footnote
                )
            }
            ConnectionState.OFFLINE -> {
                Text(
                    text = stringResource(id = R.string.stream_compose_disconnected),
                    color = ChatTheme.colors.textLowEmphasis,
                    style = ChatTheme.typography.footnote,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
 fun DefaultMessageHeaderTrailingContent() {

    Row(
        modifier = Modifier
            .fillMaxWidth(0.3f)
            .padding(end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HeaderIcon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(id = R.drawable.ic_call)
        )

        HeaderIcon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_calls)
        )

        HeaderIcon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(id = R.drawable.ic_info_button)
        )
    }
}

@Composable
internal fun DefaultMessageHeaderSubtitle(
    subtitle: String,
    typingUsers: List<User>,
) {
    val textColor = ChatTheme.colors.textLowEmphasis
    val textStyle = ChatTheme.typography.footnote

    if (typingUsers.isEmpty()) {
        Text(
            text = subtitle,
            color = textColor,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    } else {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val typingUsersText = LocalContext.current.resources.getQuantityString(
                R.plurals.stream_compose_message_list_header_typing_users,
                typingUsers.size,
                typingUsers.first().name,
                typingUsers.size - 1
            )

            TypingIndicator()

            Text(
                text = typingUsersText,
                color = textColor,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun HeaderIcon(
    modifier: Modifier,
    onClick: () -> Unit = {},
    painter: Painter
) {
    IconButton(
        modifier =  modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = CustomBurble,
        )
    }
}