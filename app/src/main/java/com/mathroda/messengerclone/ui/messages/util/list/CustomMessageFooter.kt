package com.mathroda.messengerclone.ui.messages.util.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import io.getstream.chat.android.compose.state.DateFormatType
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.components.Timestamp
import io.getstream.chat.android.compose.ui.components.channels.MessageReadStatusIcon
import io.getstream.chat.android.compose.ui.components.messages.MessageThreadFooter
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomMessageFooter(
    messageItem: MessageItemState,
) {
    val message = messageItem.message
    val hasThread = message.threadParticipants.isNotEmpty()
    val alignment = ChatTheme.messageAlignmentProvider.provideMessageAlignment(messageItem)

    if (hasThread && !messageItem.isInThread) {
        val replyCount = message.replyCount
        MessageThreadFooter(
            participants = message.threadParticipants,
            messageAlignment = alignment,
            text = LocalContext.current.resources.getQuantityString(
                R.plurals.stream_compose_message_list_thread_footnote,
                replyCount,
                replyCount
            )
        )
    }

    if (messageItem.shouldShowFooter) {
        Row(
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!messageItem.isMine) {
                Text(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f, fill = false),
                    text = message.user.name,
                    style = ChatTheme.typography.footnote,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = ChatTheme.colors.textLowEmphasis
                )
            }

            val date = message.updatedAt ?: message.createdAt ?: message.createdLocallyAt
            if (date != null) {
                Timestamp(date = date, formatType = DateFormatType.TIME)
            }
        }
    }
}