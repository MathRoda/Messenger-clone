package com.mathroda.messengerclone.ui.messages.util

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.BottomSelected

@Composable
fun ComposerIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    painter: Painter,
    onClick: () -> Unit
) {
    IconButton(
        enabled = enabled,
        modifier = modifier
            .size(32.dp)
            .padding(6.dp),
        content = {
            Icon(
                painter = painter,
                contentDescription = stringResource(id = R.string.stream_compose_attachments),
                tint = BottomSelected,
            )
        },
        onClick = onClick
    )
}