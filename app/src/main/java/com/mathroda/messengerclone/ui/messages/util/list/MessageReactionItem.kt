package com.mathroda.messengerclone.ui.messages.util.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.compose.state.reactionoptions.ReactionOptionItemState

@Composable
fun MessageReactionItem(
    option: ReactionOptionItemState,
    //score: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .wrapContentSize()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(20.dp)
                .padding(2.dp)
                .align(Alignment.CenterVertically),
            painter = option.painter,
            contentDescription = null
        )

        /*Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = score.toString(),
            color = ChatTheme.colors.textLowEmphasis,
            fontSize = 11.sp
        )*/
    }
}