package com.mathroda.messengerclone.ui.channels.utils

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.theme.BubbleGray
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@ExperimentalMaterialApi
@Composable
fun CustomSearchInputButton(
    onClick: () -> Unit = {}
) {

    Surface(
        color = ChatTheme.colors.barsBackground,
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
                ){
            Surface(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .fillMaxSize(),
                color = BubbleGray,
                shape = RoundedCornerShape(20.dp),
                onClick = onClick
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                        ){
                    Icon(
                        modifier = Modifier
                            .size(16.dp),
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = Color.LightGray
                    )

                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = "Search",
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )

                }
            }
        }
    }
}