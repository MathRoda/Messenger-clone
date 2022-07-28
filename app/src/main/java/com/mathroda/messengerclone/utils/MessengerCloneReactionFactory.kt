package com.mathroda.messengerclone.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.mathroda.messengerclone.R
import io.getstream.chat.android.compose.ui.util.ReactionDrawable
import io.getstream.chat.android.compose.ui.util.ReactionIcon
import io.getstream.chat.android.compose.ui.util.ReactionIconFactory

class MessengerCloneReactionFactory(
    private val supportedReactions: Map<String, ReactionDrawable> = mapOf(
        "heart" to ReactionDrawable(
            iconResId = R.drawable.ic_heart,
            selectedIconResId = R.drawable.ic_heart
        ),
        "laugh" to ReactionDrawable(
            iconResId = R.drawable.ic_laugh,
            selectedIconResId = R.drawable.ic_laugh
        ),
        "wow" to ReactionDrawable(
            iconResId = R.drawable.ic_wow,
            selectedIconResId = R.drawable.ic_wow
        ),
        "cry" to ReactionDrawable(
            iconResId = R.drawable.ic_cry,
            selectedIconResId = R.drawable.ic_cry
        ),
        "angry" to ReactionDrawable(
            iconResId = R.drawable.ic_angry,
            selectedIconResId = R.drawable.ic_angry
        ),
        "like" to ReactionDrawable(
            iconResId = R.drawable.ic_like,
            selectedIconResId = R.drawable.ic_like
        ),
    )
): ReactionIconFactory {

    @Composable
    override fun createReactionIcon(type: String): ReactionIcon {
       val reactionDrawable = requireNotNull(supportedReactions[type])
        return ReactionIcon(
            painter = painterResource(id = reactionDrawable.iconResId),
            selectedPainter = painterResource(id = reactionDrawable.selectedIconResId)
        )
    }

    @Composable
    override fun createReactionIcons(): Map<String, ReactionIcon> {
        return supportedReactions.mapValues {
            createReactionIcon(type = it.key)
        }
    }

    override fun isReactionSupported(type: String): Boolean {
        return supportedReactions.containsKey(type)
    }

}

