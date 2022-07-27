package com.mathroda.messengerclone.ui.messages.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.ui.messages.util.menu.CustomMessageOptions
import com.mathroda.messengerclone.ui.messages.util.menu.CustomReactionOptions
import io.getstream.chat.android.client.models.ChannelCapabilities
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.Reaction
import io.getstream.chat.android.common.state.MessageAction
import io.getstream.chat.android.common.state.React
import io.getstream.chat.android.compose.state.messageoptions.MessageOptionItemState
import io.getstream.chat.android.compose.ui.components.SimpleMenu
import io.getstream.chat.android.compose.ui.components.messageoptions.MessageOptions
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.ReactionIcon

@Composable
fun MessengerCloneSelectedMenu(
    message: Message,
    messageOptions: List<MessageOptionItemState>,
    ownCapabilities: Set<String>,
    onMessageAction: (MessageAction) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = ChatTheme.shapes.bottomSheet,
    overlayColor: Color = ChatTheme.colors.overlay,
    reactionTypes: Map<String, ReactionIcon> = ChatTheme.reactionIconFactory.createReactionIcons(),
    onDismiss: () -> Unit = {},
    headerContent: @Composable ColumnScope.() -> Unit = {
        val canLeaveReaction = ownCapabilities.contains(ChannelCapabilities.SEND_REACTION)

        if (canLeaveReaction) {
            CustomSelectedMessageReaction(
                message = message,
                reactionTypes = reactionTypes,
                onMessageAction = onMessageAction,
            )
        }
    },
    centerContent: @Composable ColumnScope.() -> Unit = {
        CustomSelectedMessageOptions(
            messageOptions = messageOptions,
            onMessageAction = onMessageAction
        )
    },
) {
    SimpleMenu(
        modifier = modifier,
        shape = shape,
        overlayColor = overlayColor,
        onDismiss = onDismiss,
        headerContent = headerContent,
        centerContent = centerContent
    )
}

@Composable
fun CustomSelectedMessageReaction(
    message: Message,
    reactionTypes: Map<String, ReactionIcon>,
    onMessageAction: (MessageAction) -> Unit,
) {
    CustomReactionOptions(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 20.dp),
        reactionTypes = reactionTypes,
        ownReactions = message.ownReactions,
        onReactionOptionSelected = {
            onMessageAction(
                React(
                    reaction = Reaction(messageId = message.id, type = it.type),
                    message = message
                )
            ) } ,
    )
}

@Composable
internal fun CustomSelectedMessageOptions(
    messageOptions: List<MessageOptionItemState>,
    onMessageAction: (MessageAction) -> Unit,
) {
    CustomMessageOptions(
        modifier = Modifier
            .height(80.dp),
        options = messageOptions,
        onMessageOptionSelected = {
            onMessageAction(it.action)
        }
    )
}