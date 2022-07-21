package com.mathroda.messengerclone.ui.channels.screen

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.compose.ui.graphics.painter.Painter
import com.mathroda.messengerclone.R

sealed class BottomScreens(
    //val route: String,
    val title: String? = null,
    @DrawableRes val icon: Int?= null
) {
    object Chats: BottomScreens(
        title = "Chats",
        icon = R.drawable.ic_chat
    )

    object Calls: BottomScreens(
        title = "Calls",
        icon = R.drawable.ic_calls
    )

    object People: BottomScreens(
        title = "People",
        icon = R.drawable.ic_people
    )
}
