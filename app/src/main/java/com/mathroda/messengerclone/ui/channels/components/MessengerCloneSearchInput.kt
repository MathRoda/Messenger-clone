package com.mathroda.messengerclone.ui.channels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.channels.utils.CustomInputField
import com.mathroda.messengerclone.ui.theme.BubbleGray
import io.getstream.chat.android.compose.ui.components.composer.InputField
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun MessengerCloneSearchInput(
    query: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearchStarted: () -> Unit = {},
    leadingIcon: @Composable RowScope.() -> Unit = { DefaultSearchLeadingIcon() },
    label: @Composable () -> Unit = { DefaultSearchLabel() },
) {
    var isFocused by remember { mutableStateOf(false) }

    val trailingIcon: (@Composable RowScope.() -> Unit)? = if (isFocused && query.isNotEmpty()) {
        @Composable {
            IconButton(
                modifier = Modifier
                    .weight(1f)
                    .size(24.dp),
                onClick = { onValueChange("") },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clear),
                        contentDescription = null,
                        tint = ChatTheme.colors.textLowEmphasis,
                    )
                }
            )
        }
    } else null

    CustomInputField(
        modifier = modifier
            .onFocusEvent { newState ->
                val wasPreviouslyFocused = isFocused

                if (!wasPreviouslyFocused && newState.isFocused) {
                    onSearchStarted()
                }

                isFocused = newState.isFocused
            },
        value = query,
        onValueChange = onValueChange,
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon()

                Box(modifier = Modifier.weight(8f)) {
                    if (query.isEmpty()) {
                        label()
                    }

                    innerTextField()
                }

                trailingIcon?.invoke(this)
            }
        },
        maxLines = 1,
        innerPadding = PaddingValues(5.dp),

    )
}

@Composable
fun RowScope.DefaultSearchLeadingIcon() {
    Icon(
        modifier = Modifier
            .weight(1f)
            .size(15.dp),
        painter = painterResource(id = R.drawable.ic_search),
        contentDescription = null,
        tint = Color.LightGray,

    )
}

@Composable
internal fun DefaultSearchLabel() {
    Text(
        text = "Search",
        style = ChatTheme.typography.body,
        color = ChatTheme.colors.textLowEmphasis,
    )
}