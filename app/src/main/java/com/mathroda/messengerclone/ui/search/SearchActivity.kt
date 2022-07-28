package com.mathroda.messengerclone.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.mathroda.messengerclone.MessengerApp
import com.mathroda.messengerclone.ui.channels.ChannelsActivity
import com.mathroda.messengerclone.ui.channels.components.MessengerCloneChannelList
import com.mathroda.messengerclone.ui.messages.MessagesActivity
import com.mathroda.messengerclone.ui.search.components.MessengerCloneSearchInput
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory

@ExperimentalFoundationApi
@ExperimentalMaterialApi
class SearchActivity : ComponentActivity() {

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
                MessengerCloneSearchScreen()
            }
        }
    }

    @Composable
    fun MessengerCloneSearchScreen() {
        val selectedChannel by listViewModel.selectedChannel
        val user by listViewModel.user.collectAsState()
        val connectionState by listViewModel.connectionState.collectAsState()
        val channelState = listViewModel.channelsState


        var searchQuery by rememberSaveable { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    MessengerCloneSearchInput(
                        query = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            listViewModel.setSearchQuery(it)
                        },
                        onBackPressed = ::openChannels
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = ChatTheme.colors.barsBackground)
                ) {
                    MessengerCloneChannelList(
                        modifier = Modifier
                            .fillMaxSize(),
                        channelsState = channelState,
                        currentUser = user,
                        onSearchClick = { /*TODO*/ },
                        searchState = true,
                        onChannelClick = ::openMessages
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

    private fun openMessages(channel: Channel) {
        startActivity(MessagesActivity.getIntent(this, channel.cid))
    }

    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }
}

