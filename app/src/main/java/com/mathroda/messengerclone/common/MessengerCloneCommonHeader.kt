package com.mathroda.messengerclone.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathroda.messengerclone.R
import io.getstream.chat.android.compose.ui.components.BackButton
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.mirrorRtl

@Composable
fun MessengerCloneCommonHeader(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = ChatTheme.colors.barsBackground,
    shape: Shape = ChatTheme.shapes.header,
    onBackPressed: () -> Unit = {},
    leadingContent: @Composable RowScope.() -> Unit = {
        DefaultProfileHeaderLeadingContent(
            onBackPressed = onBackPressed,
            title = title
        )
    },
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = color,
        shape = shape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingContent()
        }
    }
}

@Composable
fun DefaultProfileHeaderLeadingContent(
    onBackPressed: () -> Unit,
    title: String
) {
    val layoutDirection = LocalLayoutDirection.current

    BackButton(
        modifier = Modifier.mirrorRtl(layoutDirection),
        painter = painterResource(id = R.drawable.ic_arrow_back),
        onBackPressed = { onBackPressed() }
    )

    Text(
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 16.dp),
        text = title,
        textAlign = TextAlign.Start,
        fontSize = 21.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        color = ChatTheme.colors.textHighEmphasis
    )
}