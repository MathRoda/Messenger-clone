package com.mathroda.messengerclone.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.*
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun MessengerCloneProfileSettings(
    title: String = "Account",
    onSwitchAccount: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ){
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .padding(12.dp),
            text = title,
            fontSize = 16.sp,
            color = Color.Gray
        )
    }

    LazyColumn {
        item {
            AccountItems(
                title = "Switch account",
                color = CustomBurble,
                painter = painterResource(id = R.drawable.ic_switch_account,)
            )

            AccountItems(
                title = "Account settings",
                color = BottomSelected,
                painter = painterResource(id = R.drawable.ic_settings),
                onClick = onSwitchAccount
            )

            AccountItems(
                title = "Report Technical Problems",
                color = CustomOrange,
                painter = painterResource(id = R.drawable.ic_alert)
            )

            AccountItems(
                title = "Help",
                color = CustomBlue,
                painter = painterResource(id = R.drawable.ic_question_mark)
            )

            AccountItems(
                title = "Legal & policies",
                color = CustomGray,
                painter = painterResource(id = R.drawable.ic_document_new)
            )
        }
    }

}

@Composable
fun AccountItems(
    title: String,
    color: Color,
    painter: Painter,
    onClick: () -> Unit = {}
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
            ){

        Box(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(36.dp)
                .background(color, CircleShape)
                .fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(20.dp),
                painter = painter,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            modifier = Modifier
                .wrapContentWidth(),
            text = title,
            fontSize = 16.sp,
            color = ChatTheme.colors.textHighEmphasis
        )
    }
}