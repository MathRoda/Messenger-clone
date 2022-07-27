package com.mathroda.messengerclone.ui.messages.util.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.getstream.sdk.chat.model.ModelType
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.BottomSelected
import com.mathroda.messengerclone.utils.MessengerHelper
import com.mathroda.messengerclone.utils.hasLink
import com.mathroda.messengerclone.utils.isGiphy
import io.getstream.chat.android.client.models.ChannelCapabilities
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.utils.SyncStatus
import io.getstream.chat.android.common.state.*
import io.getstream.chat.android.compose.state.messageoptions.MessageOptionItemState
import io.getstream.chat.android.compose.ui.components.messageoptions.MessageOptionItem
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.core.internal.InternalStreamChatApi

@Composable
fun CustomMessageOptions(
    options: List<MessageOptionItemState>,
    onMessageOptionSelected: (MessageOptionItemState) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable RowScope.(MessageOptionItemState) -> Unit = { option ->
        CustomMessageOptionItem(
            option = option,
            onMessageOptionSelected = onMessageOptionSelected
        )
    },
) {
    Row(modifier = modifier
        .fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically) {
        options.forEach { option ->
            key(option.action) {
                itemContent(option)
            }
        }
    }
}

@Composable
internal fun CustomMessageOptionItem(
    option: MessageOptionItemState,
    onMessageOptionSelected: (MessageOptionItemState) -> Unit,
) {
    CustomMsgOptionItem(
        modifier = Modifier
            .height(90.dp)
            .padding(horizontal = 21.dp, vertical = 16.dp)
            .clickable(
                onClick = { onMessageOptionSelected(option) },
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ),
        option = option
    )
}

@Composable
fun CustomMsgOptionItem(
    option: MessageOptionItemState,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(id = option.title)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .size(28.dp),
            painter = option.iconPainter,
            tint = option.iconColor,
            contentDescription = title,
        )

        Text(
            text = title,
            style = ChatTheme.typography.body,
            color = option.titleColor
        )
    }
}

@Composable
 fun CustomMessageOptionsState(
    selectedMessage: Message,
    currentUser: User?,
    isInThread: Boolean,
    ownCapabilities: Set<String>,
): List<MessageOptionItemState> {
    if (selectedMessage.id.isEmpty()) {
        return emptyList()
    }

    val selectedMessageUserId = selectedMessage.user.id

    val isTextOnlyMessage = selectedMessage.text.isNotEmpty() && selectedMessage.attachments.isEmpty()
    val hasLinks = selectedMessage.attachments.any { it.hasLink() && it.type != MessengerHelper.attach_giphy }
    val isOwnMessage = selectedMessageUserId == currentUser?.id
    val isMessageSynced = selectedMessage.syncStatus == SyncStatus.COMPLETED
    val isMessageFailed = selectedMessage.syncStatus == SyncStatus.FAILED_PERMANENTLY

    // user capabilities
    val canReplyToMessage = ownCapabilities.contains(ChannelCapabilities.SEND_REPLY)
    val canDeleteOwnMessage = ownCapabilities.contains(ChannelCapabilities.DELETE_OWN_MESSAGE)
    val canDeleteAnyMessage = ownCapabilities.contains(ChannelCapabilities.DELETE_ANY_MESSAGE)

    return listOfNotNull(
        if (isOwnMessage && isMessageFailed) {
            MessageOptionItemState(
                title = R.string.stream_compose_resend_message,
                iconPainter = painterResource(R.drawable.ic_resend),
                action = Resend(selectedMessage),
                titleColor = ChatTheme.colors.textHighEmphasis,
                iconColor = BottomSelected,
            )
        } else null,
        if (isMessageSynced && canReplyToMessage) {
            MessageOptionItemState(
                title = R.string.stream_compose_reply,
                iconPainter = painterResource(R.drawable.ic_reply),
                action = Reply(selectedMessage),
                titleColor = ChatTheme.colors.textHighEmphasis,
                iconColor = BottomSelected,
            )
        } else null,
        if (!isInThread && isMessageSynced && canReplyToMessage) {
            MessageOptionItemState(
                title = R.string.thread_reply,
                iconPainter = painterResource(R.drawable.ic_thread),
                action = ThreadReply(selectedMessage),
                titleColor = ChatTheme.colors.textHighEmphasis,
                iconColor = BottomSelected,
            )
        } else null,
        if (isTextOnlyMessage || hasLinks) {
            MessageOptionItemState(
                title = R.string.copy_message,
                iconPainter = painterResource(R.drawable.ic_copy),
                action = Copy(selectedMessage),
                titleColor = ChatTheme.colors.textHighEmphasis,
                iconColor = BottomSelected,
            )
        } else null,
        if (!isOwnMessage) {
            MessageOptionItemState(
                title = R.string.flag_message,
                iconPainter = painterResource(R.drawable.ic_flag),
                action = Flag(selectedMessage),
                titleColor = ChatTheme.colors.textHighEmphasis,
                iconColor = BottomSelected,
            )
        } else null,
        if (canDeleteAnyMessage || (isOwnMessage && canDeleteOwnMessage)) {
            MessageOptionItemState(
                title = R.string.delete_message,
                iconPainter = painterResource(R.drawable.ic_delete),
                action = Delete(selectedMessage),
                iconColor = BottomSelected,
                titleColor = ChatTheme.colors.textHighEmphasis
            )
        } else null,
    )
}
