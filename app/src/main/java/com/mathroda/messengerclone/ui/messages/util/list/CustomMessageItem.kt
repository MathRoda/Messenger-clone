package com.mathroda.messengerclone.ui.messages.util.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.messages.util.CustomMessageContent
import com.mathroda.messengerclone.ui.theme.BottomSelected
import com.mathroda.messengerclone.ui.theme.BubbleGray
import com.mathroda.messengerclone.utils.*
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.common.state.DeletedMessageVisibility
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResult
import io.getstream.chat.android.compose.state.messages.list.GiphyAction
import io.getstream.chat.android.compose.state.messages.list.MessageFocused
import io.getstream.chat.android.compose.state.messages.list.MessageItemGroupPosition
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.state.reactionoptions.ReactionOptionItemState
import io.getstream.chat.android.compose.ui.components.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.components.channels.MessageReadStatusIcon
import io.getstream.chat.android.compose.ui.components.messages.*
import io.getstream.chat.android.compose.ui.messages.list.HighlightFadeOutDurationMillis
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomMessageItem(
    messageItem: MessageItemState,
    onLongItemClick: (Message) -> Unit,
    modifier: Modifier = Modifier,
    onReactionsClick: (Message) -> Unit = {},
    onThreadClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit = {},
    onQuotedMessageClick: (Message) -> Unit = {},
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {},
    leadingContent: @Composable RowScope.(MessageItemState) -> Unit = {
        DefaultMessageItemLeadingContent(messageItem = it)
    },
    headerContent: @Composable ColumnScope.(MessageItemState) -> Unit = {
        DefaultMessageItemFooterContent(messageItem = it)
    },
    centerContent: @Composable ColumnScope.(MessageItemState) -> Unit = {
        DefaultMessageItemCenterContent(
            messageItem = it,
            onLongItemClick = onLongItemClick,
            onImagePreviewResult = onImagePreviewResult,
            onGiphyActionClick = onGiphyActionClick,
            onQuotedMessageClick = onQuotedMessageClick,
        )
    },
    footerContent: @Composable ColumnScope.(MessageItemState) -> Unit = {
        DefaultMessageItemHeaderContent(
            messageItem = it,
            onReactionsClick = onReactionsClick
        )
    },
    trailingContent: @Composable RowScope.(MessageItemState) -> Unit = {
        DefaultMessageItemTrailingContent(messageItem = it)
    },
) {
    val (message, _, _, _, focusState) = messageItem

    val clickModifier = if (message.isDeleted()) {
        Modifier
    } else {
        Modifier.combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { if (message.hasThread()) onThreadClick(message) },
            onLongClick = { if (!message.isUploading()) onLongItemClick(message) }
        )
    }

    val backgroundColor =
        if (focusState is MessageFocused || message.pinned) ChatTheme.colors.highlight else Color.Transparent
    val shouldAnimateBackground = !message.pinned && focusState != null

    val color = if (shouldAnimateBackground) animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(
            durationMillis = if (focusState is MessageFocused) {
                AnimationConstants.DefaultDurationMillis
            } else {
                HighlightFadeOutDurationMillis
            }
        )
    ).value else backgroundColor

    val messageAlignment = ChatTheme.messageAlignmentProvider.provideMessageAlignment(messageItem)
    val description = stringResource(id = R.string.stream_compose_cd_message_item)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = color)
            .semantics { contentDescription = description },
        contentAlignment = messageAlignment.itemAlignment
    ) {
        Row(
            modifier
                .widthIn(max = 300.dp)
                .then(clickModifier)
        ) {

            leadingContent(messageItem)

            Column(horizontalAlignment = messageAlignment.contentAlignment) {
                headerContent(messageItem)

                centerContent(messageItem)

                footerContent(messageItem)
            }

            trailingContent(messageItem)
        }
    }
}

@Composable
internal fun RowScope.DefaultMessageItemLeadingContent(
    messageItem: MessageItemState,
) {
    val modifier = Modifier
        .padding(start = 8.dp, end = 12.dp)
        .size(28.dp)
        .align(Alignment.Top)

    if (!messageItem.isMine && (
                messageItem.shouldShowFooter ||
                        messageItem.groupPosition == MessageItemGroupPosition.Bottom ||
                        messageItem.groupPosition == MessageItemGroupPosition.None
                )
    ) {
        UserAvatar(
            modifier = modifier,
            user = messageItem.message.user,
            textStyle = ChatTheme.typography.captionBold,
            showOnlineIndicator = false
        )
    } else {
        Spacer(modifier = modifier)
    }
}

@Composable
internal fun DefaultMessageItemHeaderContent(
    messageItem: MessageItemState,
    onReactionsClick: (Message) -> Unit = {},
) {
    val message = messageItem.message
    val currentUser = messageItem.currentUser

    if (message.pinned) {
        val pinnedByUser = if (message.pinnedBy?.id == currentUser?.id) {
            stringResource(id = R.string.stream_compose_message_list_you)
        } else {
            message.pinnedBy?.name
        }

        val pinnedByText = if (pinnedByUser != null) {
            stringResource(id = R.string.stream_compose_pinned_to_channel_by, pinnedByUser)
        } else null

        MessageHeaderLabel(
            painter = painterResource(id = io.getstream.chat.android.compose.R.drawable.stream_compose_ic_message_pinned),
            text = pinnedByText
        )
    }

    if (message.showInChannel) {
        val alsoSendToChannelTextRes = if (messageItem.isInThread) {
            R.string.stream_compose_also_sent_to_channel
        } else {
            R.string.stream_compose_replied_to_thread
        }

        MessageHeaderLabel(
            painter = painterResource(id = R.drawable.ic_thread),
            text = stringResource(alsoSendToChannelTextRes)
        )
    }

    if (!message.isDeleted()) {
        val ownReactions = message.ownReactions
        val reactionCounts = message.reactionCounts.ifEmpty { return }
        val iconFactory = ChatTheme.reactionIconFactory
        reactionCounts
            .filter { iconFactory.isReactionSupported(it.key) }
            .takeIf { it.isNotEmpty() }
            ?.map { it.key }
            ?.map { type ->
                val isSelected = ownReactions.any { it.type == type }
                val reactionIcon = iconFactory.createReactionIcon(type)
                ReactionOptionItemState(
                    painter = reactionIcon.getPainter(isSelected),
                    type = type
                )
            }
            ?.let { options ->
                CustomMessageReaction(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false)
                        ) {
                            onReactionsClick(message)
                        }
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    options = options,
                    itemContent = { option ->
                        MessageReactionItem(
                            option = option,
                            //score = message.ownReactions.filter { it.type == option.type }.size
                        )
                    }
                )
            }
    }

}

@Composable
internal fun DefaultMessageItemTrailingContent(
    messageItem: MessageItemState,
) {
    if (messageItem.isMine) {
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
internal fun ColumnScope.DefaultMessageItemFooterContent(
    messageItem: MessageItemState,
) {
    val message = messageItem.message
    when {
        message.isUploading() -> {
            UploadingFooter(
                modifier = Modifier.align(Alignment.End),
                message = message
            )
        }
        message.isDeleted() &&
                messageItem.deletedMessageVisibility == DeletedMessageVisibility.VISIBLE_FOR_CURRENT_USER -> {
            OwnedMessageVisibilityContent(message = message)
        }
        else -> {
            CustomMessageFooter(messageItem = messageItem)
        }
    }

    val position = messageItem.groupPosition
    val spacerSize = if (position == MessageItemGroupPosition.None || position == MessageItemGroupPosition.Bottom) 4.dp else 2.dp

    Spacer(Modifier.size(spacerSize))
}

@Composable
internal fun DefaultMessageItemCenterContent(
    messageItem: MessageItemState,
    onLongItemClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit = {},
    onQuotedMessageClick: (Message) -> Unit = {},
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {},
) {
    val modifier = Modifier.widthIn(max = ChatTheme.dimens.messageItemMaxWidth)
    if (messageItem.message.isEmojiOnlyWithoutBubble()) {
        EmojiMessageContent(
            modifier = modifier,
            messageItem = messageItem,
            onLongItemClick = onLongItemClick,
            onGiphyActionClick = onGiphyActionClick,
            onImagePreviewResult = onImagePreviewResult,
            onQuotedMessageClick = onQuotedMessageClick
        )
    } else {
        RegularMessageContent(
            modifier = modifier,
            messageItem = messageItem,
            onLongItemClick = onLongItemClick,
            onGiphyActionClick = onGiphyActionClick,
            onImagePreviewResult = onImagePreviewResult,
            onQuotedMessageClick = onQuotedMessageClick
        )
    }

    if (messageItem.isMine) {
        MessageReadStatusIcon(
            modifier = Modifier.padding(end = 4.dp),
            message = messageItem.message,
            isMessageRead = messageItem.isMessageRead
        )
    }
}

@Composable
internal fun EmojiMessageContent(
    messageItem: MessageItemState,
    modifier: Modifier = Modifier,
    onLongItemClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit = {},
    onQuotedMessageClick: (Message) -> Unit = {},
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {},
) {
    val message = messageItem.message

    if (!messageItem.isFailed()) {
        MessageContent(
            message = message,
            onLongItemClick = onLongItemClick,
            onGiphyActionClick = onGiphyActionClick,
            onImagePreviewResult = onImagePreviewResult,
            onQuotedMessageClick = onQuotedMessageClick
        )
    } else {
        Box(modifier = modifier) {
            MessageContent(
                message = message,
                onLongItemClick = onLongItemClick,
                onGiphyActionClick = onGiphyActionClick,
                onImagePreviewResult = onImagePreviewResult,
                onQuotedMessageClick = onQuotedMessageClick
            )

            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd),
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                tint = ChatTheme.colors.errorAccent
            )
        }
    }
}

@Composable
internal fun RegularMessageContent(
    messageItem: MessageItemState,
    modifier: Modifier = Modifier,
    onLongItemClick: (Message) -> Unit = {},
    onGiphyActionClick: (GiphyAction) -> Unit = {},
    onQuotedMessageClick: (Message) -> Unit = {},
    onImagePreviewResult: (ImagePreviewResult?) -> Unit = {},
) {
    val (message, position, _, ownsMessage, _) = messageItem

    val messageBubbleShape = when (position) {
        MessageItemGroupPosition.Top, MessageItemGroupPosition.Middle -> RoundedCornerShape(16.dp)
        else -> {
            if (ownsMessage) ChatTheme.shapes.myMessageBubble else ChatTheme.shapes.otherMessageBubble
        }
    }

    val messageBubbleColor = when {
        message.isGiphyEphemeral() -> ChatTheme.colors.giphyMessageBackground
        message.isDeleted() -> ChatTheme.colors.barsBackground
        ownsMessage -> BottomSelected
        else -> BubbleGray
    }

    if (!messageItem.isFailed()) {
        MessageBubble(
            modifier = modifier,
            shape = messageBubbleShape,
            color = messageBubbleColor,
            border = if (message.isDeleted()) BorderStroke(1.dp, ChatTheme.colors.borders) else null,
            content = {
                CustomMessageContent(
                    message = message,
                    onLongItemClick = onLongItemClick,
                    onGiphyActionClick = onGiphyActionClick,
                    onImagePreviewResult = onImagePreviewResult,
                    onQuotedMessageClick = onQuotedMessageClick,
                    messageItemState = messageItem
                )
            }
        )
    } else {
        Box(modifier = modifier) {
            MessageBubble(
                modifier = Modifier.padding(end = 12.dp),
                shape = messageBubbleShape,
                color = messageBubbleColor,
                content = {
                    MessageContent(
                        message = message,
                        onLongItemClick = onLongItemClick,
                        onGiphyActionClick = onGiphyActionClick,
                        onImagePreviewResult = onImagePreviewResult,
                        onQuotedMessageClick = onQuotedMessageClick
                    )
                }
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd),
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                tint = ChatTheme.colors.errorAccent
            )
        }
    }
}
