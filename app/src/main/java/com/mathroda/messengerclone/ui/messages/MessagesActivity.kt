package com.mathroda.messengerclone.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mathroda.messengerclone.ui.BaseConnectedActivity
import io.getstream.chat.android.common.state.DeletedMessageVisibility
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory

class MessagesActivity : BaseConnectedActivity() {

    private val factory by lazy {
        MessagesViewModelFactory(
            context = this,
            channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: "",
            deletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE
        )
    }

    private val listviewModel by viewModels<MessageListViewModel>(factoryProducer = {factory})
    private val attachmentsPickerViewModel by viewModels<AttachmentsPickerViewModel>(factoryProducer = { factory })
    private val composerViewModel by viewModels<MessageComposerViewModel>(factoryProducer = { factory })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: return

        setContent {
           ChatTheme {
               MessagesScreen(
                   channelId = channelId,
                   messageLimit = 30,
                   onBackPressed = { finish() },
                   onHeaderActionClick = {}
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