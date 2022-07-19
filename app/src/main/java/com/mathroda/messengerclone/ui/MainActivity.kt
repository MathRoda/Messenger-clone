package com.mathroda.messengerclone.ui

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mathroda.messengerclone.MessengerApp
import com.mathroda.messengerclone.MessengerHelper
import com.mathroda.messengerclone.ui.channels.ChannelsActivity
import com.mathroda.messengerclone.ui.login.UserLoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userCredentials = MessengerApp.credentialsRepository.loadUserCredentials()
        if (userCredentials != null) {
            MessengerHelper.connectUser(userCredentials)

            if (intent.hasExtra(KEY_CHANNEL_ID)) {

                val channelId = requireNotNull(intent.getStringExtra(KEY_CHANNEL_ID))
                TaskStackBuilder.create(this)
                    .addNextIntent(ChannelsActivity.createIntent(this))
                    .addNextIntent(MessagesActivity.getIntent(this, channelId))
                    .startActivities()
            } else {
                startActivity(ChannelsActivity.createIntent(this))
            }
        } else {
            startActivity(UserLoginActivity.createIntent(this))
        }
        finish()
    }

    companion object {
        private const val KEY_CHANNEL_ID = "channelId"

        fun createIntent(context: Context, channelId: String): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channelId)
            }
        }
    }
}
