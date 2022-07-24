package com.mathroda.messengerclone.ui.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathroda.messengerclone.ui.profile.util.CustomUserAvatar
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun MessengerCloneProfileInfo(
    currentUser: User? = null,
) {
            val size = Modifier.size(100.dp)

            if (currentUser != null){
                CustomUserAvatar(
                    modifier = size,
                    user = currentUser,
                    showCameraIcon = true
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    modifier = Modifier
                        .wrapContentWidth(),
                    text = currentUser.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChatTheme.colors.textHighEmphasis
                )

            }
}