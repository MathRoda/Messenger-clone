package com.mathroda.messengerclone.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.util.PatternsCompat
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
internal fun BuildAnnotatedMessageText(text: String): AnnotatedString {
    return buildAnnotatedString {
        // First we add the whole text to the [AnnotatedString] and style it as a regular text.
        append(text)
        addStyle(
            SpanStyle(
                fontStyle = ChatTheme.typography.body.fontStyle,
                color = ChatTheme.colors.textHighEmphasis
            ),
            start = 0,
            end = text.length
        )

        // Then for each available link in the text, we add a different style, to represent the links,
        // as well as add a String annotation to it. This gives us the ability to open the URL on click.
        @SuppressLint("RestrictedApi")
        val matcher = PatternsCompat.AUTOLINK_WEB_URL.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            addStyle(
                style = SpanStyle(
                    color = ChatTheme.colors.primaryAccent,
                    textDecoration = TextDecoration.Underline,
                ),
                start = start,
                end = end,
            )

            val linkText = requireNotNull(matcher.group(0)!!)

            // Add "http://" prefix if link has no scheme in it
            val url = if (URL_SCHEMES.none { scheme -> linkText.startsWith(scheme) }) {
                URL_SCHEMES[0] + linkText
            } else {
                linkText
            }

            addStringAnnotation(
                tag = "URL",
                annotation = url,
                start = start,
                end = end,
            )
        }
    }
}

private val URL_SCHEMES = listOf("http://", "https://")