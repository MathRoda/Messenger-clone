package com.mathroda.messengerclone.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mathroda.messengerclone.common.MessengerCloneCommonHeader
import com.mathroda.messengerclone.data.PredefinedUserCredentials
import com.mathroda.messengerclone.data.UserCredentials
import com.mathroda.messengerclone.ui.channels.ChannelsActivity
import com.mathroda.messengerclone.ui.login.components.CreateNewAccountBottom
import com.mathroda.messengerclone.ui.login.components.CustomAddAccount
import com.mathroda.messengerclone.ui.login.components.UserLoginItem
import com.mathroda.messengerclone.utils.MessengerHelper
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@ExperimentalMaterialApi
@ExperimentalFoundationApi
class UserLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatTheme {
                UserLoginScreen(
                    onUserItemClick = { userCredentials ->
                        if (ChatClient.instance().config.apiKey != userCredentials.apiKey) {
                            MessengerHelper.initializeSdk(applicationContext, userCredentials.apiKey)
                        }
                        MessengerHelper.connectUser(userCredentials)
                        openChannels()
                    }
                )
            }
        }
    }

    @Composable
    fun UserLoginScreen(
        onUserItemClick: (UserCredentials) -> Unit,
        onCustomLoginClick: () -> Unit =  {},
        isShowingTopBar: Boolean = true
    ) {

        Scaffold (
            topBar = {
                if (isShowingTopBar) {
                    MessengerCloneCommonHeader(
                        title = "Switch Account"
                    )
                }
            },
            bottomBar = {
                CreateNewAccountBottom()
            }
                ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 70.dp)
            ) {
                items(items = PredefinedUserCredentials.availableUsers) { userCredentials ->
                    UserLoginItem(
                        userCredentials = userCredentials,
                        onItemClick = onUserItemClick
                    )

                }

                item {
                    CustomAddAccount(onItemClick = onCustomLoginClick)
                }
            }
        }
    }




    private fun openChannels() {
        startActivity(ChannelsActivity.createIntent(this))
        finish()
    }


    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, UserLoginActivity::class.java)
        }
    }


}
