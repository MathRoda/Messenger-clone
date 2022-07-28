package com.mathroda.messengerclone.ui.messages.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.getstream.sdk.chat.utils.MediaStringUtil
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.messages.util.ComposerIcon
import com.mathroda.messengerclone.ui.messages.util.CustomMessageInput
import com.mathroda.messengerclone.ui.theme.BottomSelected
import io.getstream.chat.android.client.models.*
import io.getstream.chat.android.common.composer.MessageComposerState
import io.getstream.chat.android.common.state.MessageMode
import io.getstream.chat.android.common.state.ValidationError
import io.getstream.chat.android.compose.ui.components.composer.CoolDownIndicator
import io.getstream.chat.android.compose.ui.components.composer.MessageInputOptions
import io.getstream.chat.android.compose.ui.components.suggestions.commands.CommandSuggestionList
import io.getstream.chat.android.compose.ui.components.suggestions.mentions.MentionSuggestionList
import io.getstream.chat.android.compose.ui.messages.composer.*
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.mirrorRtl
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel

@Composable
fun MessengerCloneMessagesComposer(
    viewModel: MessageComposerViewModel,
    modifier: Modifier = Modifier,
    onSendMessage: (Message) -> Unit = { viewModel.sendMessage(it) },
    onAttachmentsClick: () -> Unit = {},
    onValueChange: (String) -> Unit = { viewModel.setMessageInput(it) },
    onAttachmentRemoved: (Attachment) -> Unit = { viewModel.removeSelectedAttachment(it) },
    onCancelAction: () -> Unit = { viewModel.dismissMessageActions() },
    onMentionSelected: (User) -> Unit = { viewModel.selectMention(it) },
    onCommandSelected: (Command) -> Unit = { viewModel.selectCommand(it) },
    onAlsoSendToChannelSelected: (Boolean) -> Unit = { viewModel.setAlsoSendToChannel(it) },
    headerContent: @Composable ColumnScope.(MessageComposerState) -> Unit = {
        DefaultMessageComposerHeaderContent(
            messageComposerState = it,
            onCancelAction = onCancelAction
        )
    },
    footerContent: @Composable ColumnScope.(MessageComposerState) -> Unit = {
        DefaultMessageComposerFooterContent(
            messageComposerState = it,
            onAlsoSendToChannelSelected = onAlsoSendToChannelSelected
        )
    },
    mentionPopupContent: @Composable (List<User>) -> Unit = {
        DefaultMentionPopupContent(
            mentionSuggestions = it,
            onMentionSelected = onMentionSelected
        )
    },
    commandPopupContent: @Composable (List<Command>) -> Unit = {
        DefaultCommandPopupContent(
            commandSuggestions = it,
            onCommandSelected = onCommandSelected
        )
    },
   integrations: @Composable RowScope.(MessageComposerState) -> Unit = {
        DefaultComposerIntegration(
            messageInputState = it,
            onAttachmentsClick = onAttachmentsClick,
            ownCapabilities = it.ownCapabilities
        )
    },
    label: @Composable (MessageComposerState) -> Unit = { DefaultComposerLabel(it.ownCapabilities) },
    input: @Composable RowScope.(MessageComposerState) -> Unit = {
        DefaultComposerInputContent(
            messageComposerState = it,
            onValueChange = onValueChange,
            onAttachmentRemoved = onAttachmentRemoved,
            label = label,
        )
    },
    trailingContent: @Composable (MessageComposerState) -> Unit = {
        DefaultMessageComposerTrailingContent(
            value = it.inputValue,
            coolDownTime = it.coolDownTime,
            validationErrors = it.validationErrors,
            attachments = it.attachments,
            ownCapabilities = it.ownCapabilities,
            onSendMessage = { input, attachments ->
                val message = viewModel.buildNewMessage(input, attachments)

                onSendMessage(message)
            }
        )
    },
) {

    val messageComposerState by viewModel.messageComposerState.collectAsState()

    MessageComposer(
        modifier = modifier,
        onSendMessage = { text, attachments ->
            val messageWithData = viewModel.buildNewMessage(text, attachments)

            onSendMessage(messageWithData)
        },
        onMentionSelected = onMentionSelected,
        onCommandSelected = onCommandSelected,
        onAlsoSendToChannelSelected = onAlsoSendToChannelSelected,
        headerContent = headerContent,
        footerContent = footerContent,
        mentionPopupContent = mentionPopupContent,
        commandPopupContent = commandPopupContent,
        integrations = integrations,
        input = input,
        trailingContent = trailingContent,
        messageComposerState = messageComposerState,
        onCancelAction = onCancelAction
    )
}

/**
 * Represents the default content shown at the top of the message composer component.
 *
 * @param messageComposerState The state of the message composer.
 * @param onCancelAction Handler for the cancel button on Message actions, such as Edit and Reply.
 */
@Composable
 fun DefaultMessageComposerHeaderContent(
    messageComposerState: MessageComposerState,
    onCancelAction: () -> Unit,
) {
    val activeAction = messageComposerState.action

    if (activeAction != null) {
        MessageInputOptions(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
            activeAction = activeAction,
            onCancelAction = onCancelAction
        )
    }
}

/**
 * Represents the default content shown at the bottom of the message composer component.
 *
 * @param messageComposerState The state of the message composer.
 * @param onAlsoSendToChannelSelected Handler when the user checks the also send to channel checkbox.
 */
@Composable
 fun DefaultMessageComposerFooterContent(
    messageComposerState: MessageComposerState,
    onAlsoSendToChannelSelected: (Boolean) -> Unit,
) {
    if (messageComposerState.messageMode is MessageMode.MessageThread) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = messageComposerState.alsoSendToChannel,
                onCheckedChange = { onAlsoSendToChannelSelected(it) },
                colors = CheckboxDefaults.colors(ChatTheme.colors.primaryAccent)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.stream_compose_message_composer_show_in_channel),
                color = ChatTheme.colors.textLowEmphasis,
                textAlign = TextAlign.Center,
                style = ChatTheme.typography.body
            )
        }
    }
}

/**
 * Represents the default mention suggestion list popup shown above the message composer.
 *
 * @param mentionSuggestions The list of users that can be used to autocomplete the current mention input.
 * @param onMentionSelected Handler when the user taps on a mention suggestion item.
 */
@Composable
internal fun DefaultMentionPopupContent(
    mentionSuggestions: List<User>,
    onMentionSelected: (User) -> Unit,
) {
    MentionSuggestionList(
        users = mentionSuggestions,
        onMentionSelected = { onMentionSelected(it) }
    )
}

/**
 * Represents the default command suggestion list popup shown above the message composer.
 *
 * @param commandSuggestions The list of available commands in the channel.
 * @param onCommandSelected Handler when the user taps on a command suggestion item.
 */
@Composable
internal fun DefaultCommandPopupContent(
    commandSuggestions: List<Command>,
    onCommandSelected: (Command) -> Unit,
) {
    CommandSuggestionList(
        commands = commandSuggestions,
        onCommandSelected = { onCommandSelected(it) }
    )
}

/**
 * Composable that represents the message composer integrations (special actions).
 *
 * Currently just shows the Attachment picker action.
 *
 * @param messageInputState The state of the input.
 * @param onAttachmentsClick Handler when the user selects attachments.
 * @param onCommandsClick Handler when the user selects commands.
 * @param ownCapabilities Set of capabilities the user is given for the current channel.
 * For a full list @see [io.getstream.chat.android.client.models.ChannelCapabilities].
 */
@Composable
internal fun DefaultComposerIntegration(
    messageInputState: MessageComposerState,
    onAttachmentsClick: () -> Unit,
    ownCapabilities: Set<String>,
) {
    val hasTextInput = messageInputState.inputValue.isNotEmpty()
    val hasAttachments = messageInputState.attachments.isNotEmpty()
    val hasCommandInput = messageInputState.inputValue.startsWith("/")
    val hasCommandSuggestions = messageInputState.commandSuggestions.isNotEmpty()
    val hasMentionSuggestions = messageInputState.mentionSuggestions.isNotEmpty()

    val isAttachmentsButtonEnabled =
        !hasCommandInput && !hasCommandSuggestions && !hasMentionSuggestions
    val isCommandsButtonEnabled = !hasTextInput && !hasAttachments

    val canSendMessage = ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)
    val canSendAttachments = ownCapabilities.contains(ChannelCapabilities.UPLOAD_FILE)

    AnimatedVisibility(
        visible = !hasTextInput
    ) {

    if (canSendMessage) {
        Row(
            modifier = Modifier
                .height(44.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canSendAttachments) {

                ComposerIcon(
                    modifier = Modifier.size(30.dp),
                    enabled = false,
                    painter = painterResource(id = R.drawable.ic_four_points)
                ) {

                }

                ComposerIcon(
                    modifier = Modifier.size(38.dp),
                    enabled = false,
                    painter = painterResource(id = R.drawable.ic_new_camera)
                ) {

                }

                ComposerIcon(
                    enabled = isAttachmentsButtonEnabled,
                    painter = painterResource(id = R.drawable.ic_gallery)
                ) {
                    onAttachmentsClick()
                }

                ComposerIcon(
                    modifier = Modifier.size(38.dp),
                    enabled = false,
                    painter = painterResource(id = R.drawable.ic_mic)
                ) {

                }
            }
        }

    } else {
        Spacer(modifier = Modifier.width(12.dp))
    }
}
}

/**
 * Default input field label that the user can override in [MessageComposer].
 *
 * @param ownCapabilities Set of capabilities the user is given for the current channel.
 * For a full list @see [io.getstream.chat.android.client.models.ChannelCapabilities].
 */
@Composable
internal fun DefaultComposerLabel(ownCapabilities: Set<String>) {
    val text =
        if (ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)) {
           "Aa"
        } else {
            stringResource(id = R.string.stream_compose_cannot_send_messages_label)
        }

    Text(
        text = text,
        color = ChatTheme.colors.textLowEmphasis
    )
}

/**
 * Represents the default input content of the Composer.
 *
 * @param label Customizable composable that represents the input field label (hint).
 * @param messageComposerState The state of the message input.
 * @param onValueChange Handler when the input field value changes.
 * @param onAttachmentRemoved Handler when the user taps on the cancel/delete attachment action.
 */
@Composable
 fun RowScope.DefaultComposerInputContent(
    messageComposerState: MessageComposerState,
    onValueChange: (String) -> Unit,
    onAttachmentRemoved: (Attachment) -> Unit,
    label: @Composable (MessageComposerState) -> Unit,
) {
    CustomMessageInput(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .weight(1f),
        label = label,
        messageComposerState = messageComposerState,
        onValueChange = onValueChange,
        onAttachmentRemoved = onAttachmentRemoved,
        innerTrailingContent = {
            IconButton(
                modifier = Modifier
                    .size(18.dp),
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_emoji),
                        contentDescription = null,
                        tint = BottomSelected,
                    )
                },
                onClick = {

                }
            )
        }
    )
}

/**
 * Represents the default trailing content for the Composer, which represent a send button or a cooldown timer.
 *
 * @param value The input value.
 * @param coolDownTime The amount of time left in cool-down mode.
 * @param attachments The selected attachments.
 * @param validationErrors List of errors for message validation.
 * @param onSendMessage Handler when the user wants to send a message.
 * @param ownCapabilities Set of capabilities the user is given for the current channel.
 * For a full list @see [io.getstream.chat.android.client.models.ChannelCapabilities].
 */
@Composable
internal fun DefaultMessageComposerTrailingContent(
    value: String,
    coolDownTime: Int,
    attachments: List<Attachment>,
    validationErrors: List<ValidationError>,
    ownCapabilities: Set<String>,
    onSendMessage: (String, List<Attachment>) -> Unit,
) {
    val isSendButtonEnabled = ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)
    val isInputValid by lazy { (value.isNotBlank() || attachments.isNotEmpty()) && validationErrors.isEmpty() }
    val description = stringResource(id = R.string.stream_compose_cd_send_button)

    if (coolDownTime > 0) {
        CoolDownIndicator(coolDownTime = coolDownTime)
    } else {
        IconButton(
            modifier = Modifier.semantics { contentDescription = description },
            enabled = isSendButtonEnabled && isInputValid,
            content = {
                val layoutDirection = LocalLayoutDirection.current

                Icon(
                    modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection),
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = stringResource(id = R.string.stream_compose_send_message),
                    tint = BottomSelected
                )
            },
            onClick = {
                if (isInputValid) {
                    onSendMessage(value, attachments)
                }
            }
        )
    }
}

/**
 * Shows a [Toast] with an error if one of the following constraints are violated:
 *
 * - The message length exceeds the maximum allowed message length.
 * - The number of selected attachments is too big.
 * - At least one of the attachments is too big.
 *
 * @param validationErrors The list of validation errors for the current user input.
 */
@Composable
private fun MessageInputValidationError(validationErrors: List<ValidationError>, snackbarHostState: SnackbarHostState) {
    if (validationErrors.isNotEmpty()) {
        val firstValidationError = validationErrors.first()

        val errorMessage = when (firstValidationError) {
            is ValidationError.MessageLengthExceeded -> {
                stringResource(
                    R.string.stream_compose_message_composer_error_message_length,
                    firstValidationError.maxMessageLength
                )
            }
            is ValidationError.AttachmentCountExceeded -> {
                stringResource(
                    R.string.stream_compose_message_composer_error_attachment_count,
                    firstValidationError.maxAttachmentCount
                )
            }
            is ValidationError.AttachmentSizeExceeded -> {
                stringResource(
                    R.string.stream_compose_message_composer_error_file_size,
                    MediaStringUtil.convertFileSizeByteCount(firstValidationError.maxAttachmentSize)
                )
            }
            is ValidationError.ContainsLinksWhenNotAllowed -> {
                stringResource(
                    R.string.stream_compose_message_composer_error_sending_links_not_allowed,
                )
            }
        }

        val context = LocalContext.current
        LaunchedEffect(validationErrors.size) {
            if (firstValidationError is ValidationError.ContainsLinksWhenNotAllowed) {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    actionLabel = context.getString(R.string.stream_compose_ok),
                    duration = SnackbarDuration.Indefinite
                )
            } else {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


