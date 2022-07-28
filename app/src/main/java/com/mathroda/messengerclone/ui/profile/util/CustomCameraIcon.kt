package com.mathroda.messengerclone.ui.profile.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.BubbleGray
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomCameraIcon(
    modifier: Modifier,
    ) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(ChatTheme.colors.appBackground, CircleShape)
            .padding(7.dp)
            .background(BubbleGray, CircleShape),
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(20.dp),
            painter = painterResource(id = R.drawable.ic_new_camera),
            contentDescription = null )
    }
}