package com.mathroda.messengerclone.ui.channels

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.MessengerApp
import com.mathroda.messengerclone.R
import com.mathroda.messengerclone.ui.BaseConnectedActivity
import com.mathroda.messengerclone.ui.channels.components.*
import com.mathroda.messengerclone.ui.login.UserLoginActivity
import com.mathroda.messengerclone.ui.messages.MessagesActivity
import com.mathroda.messengerclone.ui.profile.ProfileActivity
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.compose.state.channels.list.*
import io.getstream.chat.android.compose.ui.channels.info.SelectedChannelMenu
import io.getstream.chat.android.compose.ui.components.SimpleDialog
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@ExperimentalFoundationApi
class ChannelsActivity: BaseConnectedActivity() {

    private val factory by lazy {
        ChannelViewModelFactory(
            chatClient = ChatClient.instance(),
            querySort = QuerySortByField.descByName("last_updated"),
            filters = null
        )
    }

    private val listViewModel: ChannelListViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChatTheme(dateFormatter = MessengerApp.dateFormatter) {

                    MessengerCloneChannelsScreen(
                        isShowingHeader = true,
                        onItemClick = ::openMessages,
                        onBackPressed = ::finish,
                        onHeaderAvatarClick = {
                            openProfile()
                        }
                    )

            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    @Suppress("LongMethod")
    fun MessengerCloneChannelsScreen(
        title: String = "Chats",
        isShowingHeader: Boolean = true,
        channelLimit: Int = 30,
        memberLimit: Int = 1,
        messageLimit: Int = 30,
        onHeaderActionClick: () -> Unit = {},
        onHeaderAvatarClick: () -> Unit = {},
        onItemClick: (Channel) -> Unit = {},
        onViewChannelInfoAction: (Channel) -> Unit = {},
        onBackPressed: () -> Unit = {},
    ) {
        val selectedChannel by listViewModel.selectedChannel
        val user by listViewModel.user.collectAsState()
        val connectionState by listViewModel.connectionState.collectAsState()
        val channelState = listViewModel.channelsState

        BackHandler(enabled = true) {
            if (selectedChannel != null) {
                listViewModel.selectChannel(null)
            } else {
                onBackPressed()
            }
        }

        var searchQuery by rememberSaveable { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {


            Scaffold(
                topBar = {
                    if (isShowingHeader) {
                        MessengerCloneListHeader(
                            onHeaderActionClick = onHeaderActionClick,
                            onAvatarClick = { onHeaderAvatarClick() },
                            currentUser = user,
                            title = title,
                            connectionState = connectionState,
                            elevation = 0.dp
                        )
                    }
                },

                bottomBar = {
                    MessengerCloneBottomBar()
                }

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = ChatTheme.colors.barsBackground)
                ) {

                    MessengerCloneChannelList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 56.dp),
                        channelsState = channelState,
                        currentUser = user,
                        onChannelClick = onItemClick,
                        onChannelLongClick = {
                            listViewModel.selectChannel(it)
                        },
                        query = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            listViewModel.setSearchQuery(it)
                        }
                    )
                }
            }
            val selectedChannel = selectedChannel ?: Channel()
            AnimatedVisibility(
                visible = selectedChannel.cid.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = AnimationConstants.DefaultDurationMillis / 2))
            ) {
                SelectedChannelMenu(
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
                    selectedChannel = selectedChannel,
                    currentUser = user,
                    isMuted = listViewModel.isChannelMuted(selectedChannel.cid),
                    onChannelOptionClick = { action ->
                        when (action) {
                            is ViewInfo -> onViewChannelInfoAction(action.channel)
                            is MuteChannel -> listViewModel.muteChannel(action.channel)
                            is UnmuteChannel -> listViewModel.unmuteChannel(action.channel)
                            else -> listViewModel.performChannelAction(action)
                        }
                    },
                    onDismiss = { listViewModel.dismissChannelAction() }
                )
            }

            val activeAction = listViewModel.activeChannelAction

            if (activeAction is LeaveGroup) {
                SimpleDialog(
                    modifier = Modifier.padding(16.dp),
                    title = stringResource(
                        id = R.string.stream_compose_selected_channel_menu_leave_group_confirmation_title
                    ),
                    message = stringResource(
                        id = R.string.stream_compose_selected_channel_menu_leave_group_confirmation_message,
                        ChatTheme.channelNameFormatter.formatChannelName(activeAction.channel, user)
                    ),
                    onPositiveAction = { listViewModel.leaveGroup(activeAction.channel) },
                    onDismiss = { listViewModel.dismissChannelAction() }
                )
            } else if (activeAction is DeleteConversation) {
                SimpleDialog(
                    modifier = Modifier.padding(16.dp),
                    title = stringResource(
                        id = R.string.stream_compose_selected_channel_menu_delete_conversation_confirmation_title
                    ),
                    message = stringResource(
                        id = R.string.stream_compose_selected_channel_menu_delete_conversation_confirmation_message,
                        ChatTheme.channelNameFormatter.formatChannelName(activeAction.channel, user)
                    ),
                    onPositiveAction = { listViewModel.deleteConversation(activeAction.channel) },
                    onDismiss = { listViewModel.dismissChannelAction() }
                )
            }
        }
    }


    private fun openMessages(channel: Channel) {
        startActivity(MessagesActivity.getIntent(this, channel.cid))
    }

    private fun openProfile() {
        finish()
        startActivity(ProfileActivity.getIntent(this))
        overridePendingTransition(0, 0)
    }


    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, ChannelsActivity::class.java)
        }
    }
}