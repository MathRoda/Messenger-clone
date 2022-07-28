package com.mathroda.messengerclone.ui.messages.util

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.common.CustomInputField
import com.mathroda.messengerclone.ui.messages.components.DefaultComposerLabel
import com.mathroda.messengerclone.ui.theme.BubbleGray
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.ChannelCapabilities
import io.getstream.chat.android.common.composer.MessageComposerState
import io.getstream.chat.android.common.state.Edit
import io.getstream.chat.android.common.state.Reply
import io.getstream.chat.android.compose.ui.components.messages.QuotedMessage
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun CustomMessageInput(
    messageComposerState: MessageComposerState,
    onValueChange: (String) -> Unit,
    onAttachmentRemoved: (Attachment) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = DefaultMessageInputMaxLines,
    label: @Composable (MessageComposerState) -> Unit = {
        DefaultComposerLabel(ownCapabilities = messageComposerState.ownCapabilities)
    },
    innerLeadingContent: @Composable RowScope.() -> Unit = {},
    innerTrailingContent: @Composable RowScope.() -> Unit = {},
) {
    val (value, attachments, activeAction) = messageComposerState
    val canSendMessage = messageComposerState.ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)

    CustomInputField(
        modifier = modifier,
        value = value,
        color = BubbleGray,
        maxLines = maxLines,
        onValueChange = onValueChange,
        enabled = canSendMessage,
        innerPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        decorationBox = { innerTextField ->
            Column {
                if (activeAction is Reply) {
                    QuotedMessage(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        message = activeAction.message,
                        onLongItemClick = {},
                        onQuotedMessageClick = {}
                    )

                    Spacer(modifier = Modifier.size(16.dp))
                }

                if (attachments.isNotEmpty() && activeAction !is Edit) {
                    val previewFactory = ChatTheme.attachmentFactories.firstOrNull { it.canHandle(attachments) }

                    previewFactory?.previewContent?.invoke(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        attachments = attachments,
                        onAttachmentRemoved = onAttachmentRemoved
                    )

                    Spacer(modifier = Modifier.size(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    innerLeadingContent()

                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()

                        if (value.isEmpty()) {
                            label(messageComposerState)
                        }
                    }

                    innerTrailingContent()
                }
            }
        }
    )
}


/**
 * The default number of lines allowed in the input. The message input will become scrollable after
 * this threshold is exceeded.
 */
private const val DefaultMessageInputMaxLines = 6