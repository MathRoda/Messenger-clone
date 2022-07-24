package com.mathroda.messengerclone.ui.channels.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.compose.state.OnlineIndicatorAlignment
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
 fun BoxScope.DefaultOnlineIndicator(onlineIndicatorAlignment: OnlineIndicatorAlignment) {
    CustomOnlineIndicator(modifier = Modifier.align(onlineIndicatorAlignment.alignment))
}

@Composable
fun CustomOnlineIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(17.dp)
            .background(ChatTheme.colors.appBackground, CircleShape)
            .padding(2.dp)
            .background(ChatTheme.colors.infoAccent, CircleShape)
    )
}