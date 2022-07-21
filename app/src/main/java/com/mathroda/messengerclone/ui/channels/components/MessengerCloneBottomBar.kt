package com.mathroda.messengerclone.ui.channels.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.ui.channels.screen.BottomScreens
import com.mathroda.messengerclone.ui.theme.BottomSelected
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun MessengerCloneBottomBar() {
    val screens = listOf(
        BottomScreens.Chats,
        BottomScreens.Calls,
        BottomScreens.People
    )

    BottomNavigation(backgroundColor = ChatTheme.colors.barsBackground) {
        screens.forEach { screen ->
            BottomNavigationItem(
                label = { Text(text = screen.title!!) },
                icon = {
                       Icon(
                           painter = painterResource(id = screen.icon!!),
                           contentDescription = null,
                           modifier = Modifier.size(25.dp)
                       )
                },
                selected = screen.icon == BottomScreens.Chats.icon ,
                onClick = { /*TODO*/ },
                selectedContentColor = BottomSelected,
                unselectedContentColor = Color.Gray
            )
        }
    }
}