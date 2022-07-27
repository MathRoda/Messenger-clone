package com.mathroda.messengerclone.ui.messages.util.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.ui.theme.BubbleGray
import io.getstream.chat.android.compose.state.reactionoptions.ReactionOptionItemState
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomMessageReaction(
    options: List<ReactionOptionItemState>,
    modifier: Modifier = Modifier,
    itemContent: @Composable RowScope.(ReactionOptionItemState) -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(shape = RoundedCornerShape(16.dp), color = BubbleGray)
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        options.forEach { option ->
            itemContent(option)
        }
    }
}