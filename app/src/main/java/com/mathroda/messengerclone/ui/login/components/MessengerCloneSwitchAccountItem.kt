package com.mathroda.messengerclone.ui.login.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.data.UserCredentials
import com.mathroda.messengerclone.ui.theme.BottomSelected
import com.mathroda.messengerclone.ui.theme.BubbleGray
import io.getstream.chat.android.compose.ui.components.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme

/**
 * Represents a user whose credentials will be used for login.
 */
@Composable
fun UserLoginItem(
    userCredentials: UserCredentials,
    onItemClick: (UserCredentials) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                onClick = { onItemClick(userCredentials) },
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserAvatar(
            modifier = Modifier.size(40.dp),
            user = userCredentials.user,
        )

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = userCredentials.user.name,
                fontSize = 14.sp,
                color = ChatTheme.colors.textHighEmphasis
            )

            Text(
                text = stringResource(id = R.string.user_login_user_subtitle),
                fontSize = 12.sp,
                color = ChatTheme.colors.textLowEmphasis
            )
        }
    }
}

@Composable
fun CustomAddAccount(
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                onClick = { onItemClick() },
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp)
                .background(BubbleGray)
                .padding(8.dp),
            imageVector = Icons.Default.Add,
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add account",
                fontSize = 14.sp,
                color = ChatTheme.colors.textHighEmphasis
            )

        }

    }
}

@ExperimentalMaterialApi
@Composable
fun CreateNewAccountBottom(
    modifier: Modifier = Modifier,
    onCreateAccount: () -> Unit = {}
) {

    Surface(
        color = ChatTheme.colors.barsBackground,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            verticalAlignment = CenterVertically,
        ) {

            Surface(
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 12.dp)
                    .fillMaxSize(),
                color = BottomSelected,
                shape = RoundedCornerShape(12.dp),
                onClick = onCreateAccount
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "CREATE NEW ACCOUNT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            }
        }
    }

}