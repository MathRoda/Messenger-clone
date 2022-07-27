package com.mathroda.messengerclone.ui.profile.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.initials
import io.getstream.chat.android.compose.state.OnlineIndicatorAlignment
import io.getstream.chat.android.compose.ui.components.avatar.Avatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomUserAvatar(
    user: User,
    modifier: Modifier = Modifier,
    shape: Shape = ChatTheme.shapes.avatar,
    textStyle: TextStyle = ChatTheme.typography.title3Bold,
    showCameraIcon: Boolean = true,
    cameraIconAlignment: OnlineIndicatorAlignment = OnlineIndicatorAlignment.BottomEnd,
    initialsAvatarOffset: DpOffset = DpOffset(0.dp, 0.dp),
    cameraIcon: @Composable BoxScope.() -> Unit = {
        DefaultCameraIcon(cameraIconAlignment = cameraIconAlignment)
    }
) {
    Box(modifier = modifier) {
        Avatar(
            modifier = Modifier.fillMaxWidth(),
            imageUrl = user.image,
            initials = user.initials,
            textStyle = textStyle,
            shape = shape,
            initialsAvatarOffset = initialsAvatarOffset
        )

        if(showCameraIcon) {
            cameraIcon()
        }
    }
}

@Composable
fun BoxScope.DefaultCameraIcon(cameraIconAlignment: OnlineIndicatorAlignment) {
    CustomCameraIcon(modifier = Modifier.align(cameraIconAlignment.alignment))
}