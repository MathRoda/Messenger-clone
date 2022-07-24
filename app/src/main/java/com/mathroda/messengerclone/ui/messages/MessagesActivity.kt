package com.mathroda.messengerclone.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.ui.BaseConnectedActivity
import com.mathroda.messengerclone.ui.messages.components.MessengerCloneMessagesComposer
import com.mathroda.messengerclone.ui.messages.components.MessengerCloneMessagesHeader
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.common.model.DeleteMessage
import io.getstream.chat.android.common.model.EditMessage
import io.getstream.chat.android.common.model.SendAnyway
import io.getstream.chat.android.common.state.*
import io.getstream.chat.android.compose.state.imagepreview.ImagePreviewResultType
import io.getstream.chat.android.compose.state.messageoptions.MessageOptionItemState
import io.getstream.chat.android.compose.state.messages.SelectedMessageFailedModerationState
import io.getstream.chat.android.compose.state.messages.SelectedMessageOptionsState
import io.getstream.chat.android.compose.state.messages.SelectedMessageReactionsPickerState
import io.getstream.chat.android.compose.state.messages.SelectedMessageReactionsState
import io.getstream.chat.android.compose.ui.components.SimpleDialog
import io.getstream.chat.android.compose.ui.components.messageoptions.defaultMessageOptionsState
import io.getstream.chat.android.compose.ui.components.moderatedmessage.ModeratedMessageDialog
import io.getstream.chat.android.compose.ui.components.reactionpicker.ReactionsPicker
import io.getstream.chat.android.compose.ui.components.selectedmessage.SelectedMessageMenu
import io.getstream.chat.android.compose.ui.components.selectedmessage.SelectedReactionsMenu
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.messages.attachments.AttachmentsPicker
import io.getstream.chat.android.compose.ui.messages.composer.MessageComposer
import io.getstream.chat.android.compose.ui.messages.header.MessageListHeader
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.rememberMessageListState
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class,
)
class MessagesActivity : BaseConnectedActivity() {

    private val factory by lazy {
        MessagesViewModelFactory(
            context = this,
            channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: "",
            deletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE
        )
    }

    private val listViewModel by viewModels<MessageListViewModel>(factoryProducer = {factory})
    private val attachmentsPickerViewModel by viewModels<AttachmentsPickerViewModel>(factoryProducer = { factory })
    private val composerViewModel by viewModels<MessageComposerViewModel>(factoryProducer = { factory })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: return

        setContent {
           ChatTheme {
               MessengerCloneMessagesScreen(
                   channelId = channelId,
                   messageLimit = 30,
                   onBackPressed = { finish() },
                   onHeaderActionClick = {}
               )
           }
        }
    }

    @Composable
    fun MessengerCloneMessagesScreen(
        channelId: String,
        messageLimit: Int = 30,
        showHeader: Boolean = true,
        enforceUniqueReactions: Boolean = true,
        showDateSeparators: Boolean = true,
        showSystemMessages: Boolean = true,
        deletedMessageVisibility: DeletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE,
        messageFooterVisibility: MessageFooterVisibility = MessageFooterVisibility.WithTimeDifference(),
        onBackPressed: () -> Unit = {},
        onHeaderActionClick: (channel: Channel) -> Unit = {},
    ) {

        val currentState = listViewModel.currentMessagesState
        val messageActions = listViewModel.messageActions

        val selectedMessageState = currentState.selectedMessageState
        val messageMode = listViewModel.messageMode
        val isShowingAttachments = attachmentsPickerViewModel.isShowingAttachments

        val connectionState by listViewModel.connectionState.collectAsState()
        val user by listViewModel.user.collectAsState()

        val backAction = {
            val isInThread = listViewModel.isInThread
            val isShowingOverlay = listViewModel.isShowingOverlay

            when {
                attachmentsPickerViewModel.isShowingAttachments -> attachmentsPickerViewModel.changeAttachmentState(false)
                isShowingOverlay -> listViewModel.selectMessage(null)
                isInThread -> {
                    listViewModel.leaveThread()
                    composerViewModel.leaveThread()
                }
                else -> onBackPressed()
            }
        }
        BackHandler(enabled = true, onBack = backAction)

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (showHeader) {
                        MessengerCloneMessagesHeader(
                            modifier = Modifier
                                .height(56.dp),
                            channel = listViewModel.channel,
                            currentUser = user,
                            typingUsers = listViewModel.typingUsers,
                            connectionState = connectionState,
                            messageMode = messageMode,
                            onBackPressed = backAction,
                            onHeaderActionClick = onHeaderActionClick
                        )
                    }
                },
                bottomBar = {
                    MessengerCloneMessagesComposer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .align(Alignment.Center),
                        viewModel = composerViewModel,
                        onAttachmentsClick = { attachmentsPickerViewModel.changeAttachmentState(true) },
                        onCommandsClick = { composerViewModel.toggleCommandsVisibility() },
                        onCancelAction = {
                            listViewModel.dismissAllMessageActions()
                            composerViewModel.dismissMessageActions()
                        }
                    )
                }
            ) {
                MessageList(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ChatTheme.colors.barsBackground)
                        .padding(it),
                    viewModel = listViewModel,
                    lazyListState = rememberMessageListState(parentMessageId = currentState.parentMessageId),
                    onThreadClick = { message ->
                        composerViewModel.setMessageMode(MessageMode.MessageThread(message))
                        listViewModel.openMessageThread(message)
                    },
                    onImagePreviewResult = { result ->
                        when (result?.resultType) {
                            ImagePreviewResultType.QUOTE -> {
                                val message = listViewModel.getMessageWithId(result.messageId)

                                if (message != null) {
                                    composerViewModel.performMessageAction(Reply(message))
                                }
                            }

                            ImagePreviewResultType.SHOW_IN_CHAT -> {
                                listViewModel.focusMessage(result.messageId)
                            }
                            null -> Unit
                        }
                    }
                )
            }

            val selectedMessage = selectedMessageState?.message ?: Message()
            val ownCapabilities = selectedMessageState?.ownCapabilities ?: setOf()

            val newMessageOptions = defaultMessageOptionsState(
                selectedMessage = selectedMessage,
                currentUser = user,
                isInThread = listViewModel.isInThread,
                ownCapabilities = ownCapabilities
            )

            var messageOptions by remember { mutableStateOf<List<MessageOptionItemState>>(emptyList()) }

            if (newMessageOptions.isNotEmpty()) {
                messageOptions = newMessageOptions
            }

            AnimatedVisibility(
                visible = selectedMessageState is SelectedMessageOptionsState && selectedMessage.id.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2))
            ) {
                SelectedMessageMenu(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .animateEnterExit(
                            enter = slideInVertically(
                                initialOffsetY = { height -> height },
                                animationSpec = tween()
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { height -> height },
                                animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)
                            )
                        ),
                    messageOptions = messageOptions,
                    message = selectedMessage,
                    ownCapabilities = ownCapabilities,
                    onMessageAction = { action ->
                        composerViewModel.performMessageAction(action)
                        listViewModel.performMessageAction(action)
                    },
                    onShowMoreReactionsSelected = {
                        listViewModel.selectExtendedReactions(selectedMessage)
                    },
                    onDismiss = { listViewModel.removeOverlay() }
                )
            }

            AnimatedVisibility(
                visible = selectedMessageState is SelectedMessageReactionsState && selectedMessage.id.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2))
            ) {
                SelectedReactionsMenu(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .animateEnterExit(
                            enter = slideInVertically(
                                initialOffsetY = { height -> height },
                                animationSpec = tween()
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { height -> height },
                                animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)
                            )
                        ),
                    currentUser = user,
                    message = selectedMessage,
                    onMessageAction = { action ->
                        composerViewModel.performMessageAction(action)
                        listViewModel.performMessageAction(action)
                    },
                    onShowMoreReactionsSelected = {
                        listViewModel.selectExtendedReactions(selectedMessage)
                    },
                    onDismiss = { listViewModel.removeOverlay() },
                    ownCapabilities = selectedMessageState?.ownCapabilities ?: setOf()
                )
            }

            AnimatedVisibility(
                visible = selectedMessageState is SelectedMessageReactionsPickerState && selectedMessage.id.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2))
            ) {
                ReactionsPicker(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .heightIn(max = 400.dp)
                        .wrapContentHeight()
                        .animateEnterExit(
                            enter = slideInVertically(
                                initialOffsetY = { height -> height },
                                animationSpec = tween()
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { height -> height },
                                animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2)
                            )
                        ),
                    message = selectedMessage,
                    onMessageAction = { action ->
                        composerViewModel.performMessageAction(action)
                        listViewModel.performMessageAction(action)
                    },
                    onDismiss = { listViewModel.removeOverlay() }
                )
            }

            AnimatedVisibility(
                visible = isShowingAttachments,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(delayMillis = AnimationConstants.DefaultDurationMillis / 2))
            ) {
                AttachmentsPicker(
                    attachmentsPickerViewModel = attachmentsPickerViewModel,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(350.dp)
                        .animateEnterExit(
                            enter = slideInVertically(
                                initialOffsetY = { height -> height },
                                animationSpec = tween()
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { height -> height },
                                animationSpec = tween(delayMillis = AnimationConstants.DefaultDurationMillis / 2)
                            )
                        ),
                    onAttachmentsSelected = { attachments ->
                        attachmentsPickerViewModel.changeAttachmentState(false)
                        composerViewModel.addSelectedAttachments(attachments)
                    },
                    onDismiss = {
                        attachmentsPickerViewModel.changeAttachmentState(false)
                        attachmentsPickerViewModel.dismissAttachments()
                    }
                )
            }

            val deleteAction = messageActions.firstOrNull { it is Delete }

            if (deleteAction != null) {
                SimpleDialog(
                    modifier = Modifier.padding(16.dp),
                    title = "Delete message",
                    message = "Are you sure you want to delete this message?",
                    onPositiveAction = { listViewModel.deleteMessage(deleteAction.message) },
                    onDismiss = { listViewModel.dismissMessageAction(deleteAction) }
                )
            }

            val flagAction = messageActions.firstOrNull { it is Flag }

            if (flagAction != null) {
                SimpleDialog(
                    modifier = Modifier.padding(16.dp),
                    title = "Flag Message",
                    message = "Do you want to send a copy of this message to a moderator for further investigation?",
                    onPositiveAction = { listViewModel.flagMessage(flagAction.message) },
                    onDismiss = { listViewModel.dismissMessageAction(flagAction) }
                )
            }

            if (selectedMessageState is SelectedMessageFailedModerationState) {
                ModeratedMessageDialog(
                    message = selectedMessage,
                    modifier = Modifier.background(
                        shape = MaterialTheme.shapes.medium,
                        color = ChatTheme.colors.inputBackground
                    ),
                    onDismissRequest = { listViewModel.removeOverlay() },
                    onDialogOptionInteraction = { message, action ->
                        when (action) {
                            DeleteMessage -> listViewModel.deleteMessage(message = message, true)
                            EditMessage -> composerViewModel.performMessageAction(Edit(message))
                            SendAnyway -> listViewModel.performMessageAction(Resend(message))
                            else -> {
                                // Custom events
                            }
                        }
                    }
                )
            }
        }
    }

    companion object{
      private const val KEY_CHANNEL_ID = "channelId"

        fun getIntent(context: Context, channelId: String): Intent {
            return Intent(context, MessagesActivity::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channelId)
            }
        }
    }
}