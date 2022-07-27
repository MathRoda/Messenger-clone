package com.mathroda.messengerclone.ui.messages.util.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.client.models.Reaction
import io.getstream.chat.android.compose.state.reactionoptions.ReactionOptionItemState
import io.getstream.chat.android.compose.ui.components.reactionoptions.ReactionOptionItem
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.ReactionIcon


@Composable
fun CustomReactionOptions(
    ownReactions: List<Reaction>,
    onReactionOptionSelected: (ReactionOptionItemState) -> Unit,
    modifier: Modifier = Modifier,
    numberOfReactionsShown: Int = DefaultNumberOfReactionsShown,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    reactionTypes: Map<String, ReactionIcon> = ChatTheme.reactionIconFactory.createReactionIcons(),
    itemContent: @Composable RowScope.(ReactionOptionItemState) -> Unit = { option ->
        CustomReactionOptionItem(
            option = option,
            onReactionOptionSelected = onReactionOptionSelected
        )
    },
) {
    val options = reactionTypes.entries.map { (type, reactionIcon) ->
        val isSelected = ownReactions.any { ownReaction -> ownReaction.type == type }
        val painter = reactionIcon.getPainter(isSelected)
        ReactionOptionItemState(
            painter = painter,
            type = type
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        options.take(numberOfReactionsShown).forEach { option ->
            key(option.type) {
                itemContent(option)
            }
        }

    }
}

@Composable
fun CustomReactionOptionItem(
    option: ReactionOptionItemState,
    onReactionOptionSelected: (ReactionOptionItemState) -> Unit
) {
    ReactionOptionItem(
        modifier = Modifier
            .size(32.dp)
            .size(ChatTheme.dimens.reactionOptionItemIconSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = { onReactionOptionSelected(option) }
            ),
        option = option

    )
}

private const val DefaultNumberOfReactionsShown = 6