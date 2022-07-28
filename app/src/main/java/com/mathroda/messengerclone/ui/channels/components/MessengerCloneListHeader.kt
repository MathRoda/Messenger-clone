package com.mathroda.messengerclone.ui.channels.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.BubbleGray
import io.getstream.chat.android.client.models.ConnectionState
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.components.NetworkLoadingIndicator
import io.getstream.chat.android.compose.ui.components.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme


@Composable
 fun MessengerCloneListHeader(
    modifier: Modifier = Modifier,
    title: String = "Chats",
    currentUser: User? = null,
    connectionState: ConnectionState = ConnectionState.CONNECTED,
    color: Color = ChatTheme.colors.barsBackground,
    shape: Shape = ChatTheme.shapes.header,
    elevation: Dp = ChatTheme.dimens.headerElevation,
    onAvatarClick: (User?) -> Unit = {},
    onHeaderActionClick: () -> Unit = {},
    leadingContent: @Composable RowScope.() -> Unit = {
        DefaultChannelHeaderLeadingContent(
            currentUser = currentUser,
            onAvatarClick = onAvatarClick,
            connectionState = connectionState,
            title = title
        )
    },
    trailingContent: @Composable RowScope.() -> Unit = {
        DefaultChannelListHeaderTrailingContent(
            onHeaderActionClick = onHeaderActionClick
        )
    },
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        elevation = elevation,
        color = color,
        shape = shape
    ) {
        Row(
            Modifier
                .fillMaxWidth(0.7f)
                .padding(12.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            leadingContent()
        }

        Row (
            Modifier
                .fillMaxWidth(0.3f)
                .padding(12.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.End
                ){
            trailingContent()
        }
    }
}

@Composable
internal fun DefaultChannelHeaderLeadingContent(
    currentUser: User?,
    onAvatarClick: (User?) -> Unit,
    connectionState: ConnectionState,
    title: String
) {
    val size = Modifier.size(40.dp)

    if (currentUser != null) {
        UserAvatar(
            modifier = size,
            user = currentUser,
            contentDescription = currentUser.name,
            showOnlineIndicator = false,
            onClick = { onAvatarClick(currentUser) }
        )
    } else {
        Spacer(modifier = size)
    }

    when (connectionState) {
        ConnectionState.CONNECTED -> {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 16.dp),
                text = title,
                textAlign = TextAlign.Start,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = ChatTheme.colors.textHighEmphasis
            )
        }
        ConnectionState.CONNECTING -> NetworkLoadingIndicator()
        ConnectionState.OFFLINE -> {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 16.dp),
                text = "Disconnected",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                maxLines = 1,
                color = ChatTheme.colors.textHighEmphasis
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DefaultChannelListHeaderTrailingContent(
    onHeaderActionClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.size(35.dp),
        color = BubbleGray,
        shape = ChatTheme.shapes.avatar,
        onClick = {},
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
    ) {
        Icon(
            modifier = Modifier
                .wrapContentSize()
                .size(22.dp),
            painter = painterResource(id = R.drawable.ic_new_camera),
            contentDescription = null,
            tint = Color.Black,
        )
    }

    Spacer(Modifier.width(10.dp))

    Surface(
        modifier = Modifier
            .size(35.dp),
        onClick = onHeaderActionClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false),
        color = BubbleGray,
        shape = ChatTheme.shapes.avatar,
    ) {
        Icon(
            modifier = Modifier
                .wrapContentSize()
                .size(22.dp),
            painter = painterResource(id = R.drawable.ic_new_pen),
            contentDescription = null,
            tint = Color.Black,
        )
    }

}
