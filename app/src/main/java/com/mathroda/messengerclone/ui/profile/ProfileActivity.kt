package com.mathroda.messengerclone.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.common.MessengerCloneCommonHeader
import com.mathroda.messengerclone.ui.BaseConnectedActivity
import com.mathroda.messengerclone.ui.channels.ChannelsActivity
import com.mathroda.messengerclone.ui.login.UserLoginActivity
import com.mathroda.messengerclone.ui.profile.components.MessengerCloneProfileInfo
import com.mathroda.messengerclone.ui.profile.components.MessengerCloneProfileSettings
import com.mathroda.messengerclone.utils.MessengerHelper
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory

@ExperimentalFoundationApi
class ProfileActivity : BaseConnectedActivity() {

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
            ChatTheme {
                MessengerCloneProfileScreen(
                    onBackPressed = {
                        openChannels()
                    },
                    onSwitchAccount = {
                        MessengerHelper.disconnectUser()
                        openUserLogin()
                    }
                )
            }
        }
    }

    @Composable
    fun MessengerCloneProfileScreen(
        isShowingHeader: Boolean = true,
        onBackPressed: () -> Unit,
        onSwitchAccount: () -> Unit = {}
    ) {
        val user by listViewModel.user.collectAsState()

        BackHandler { openChannels() }
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    if (isShowingHeader) {
                        MessengerCloneCommonHeader(
                            modifier = Modifier
                                .height(56.dp),
                            onBackPressed = onBackPressed,
                            title = "Me"
                        )
                    }
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = ChatTheme.colors.barsBackground)
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.size(20.dp))
                    MessengerCloneProfileInfo(
                        currentUser = user
                    )

                    Spacer(modifier = Modifier.size(16.dp))
                    MessengerCloneProfileSettings(
                        onSwitchAccount = onSwitchAccount
                    )
                }
            }
        }
    }

    private fun openChannels() {
        finish()
        startActivity(ChannelsActivity.createIntent(this))
        overridePendingTransition(0, 0)
    }

    private fun openUserLogin() {
        finish()
        startActivity(UserLoginActivity.createIntent(this))
        overridePendingTransition(0, 0)
    }

    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}





