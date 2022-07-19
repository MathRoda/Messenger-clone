package com.mathroda.messengerclone

import android.app.Application
import com.getstream.sdk.chat.utils.DateFormatter
import com.mathroda.messengerclone.data.PredefinedUserCredentials
import com.mathroda.messengerclone.data.UserCredentialsRepository
import io.getstream.chat.android.client.utils.internal.toggle.ToggleService
import io.getstream.chat.android.compose.ui.theme.ChatTheme.dateFormatter
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import io.getstream.chat.android.state.BuildConfig
import java.util.*

class MessengerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        credentialsRepository = UserCredentialsRepository(this)
        dateFormatter = DateFormatter.from(this)

        initializeToggleService()
        MessengerHelper.initializeSdk(this, getApiKey())

    }

    private fun getApiKey(): String {
        return credentialsRepository.loadApiKey() ?: PredefinedUserCredentials.API_KEY
    }

    @OptIn(InternalStreamChatApi::class)
    private fun initializeToggleService() {
        ToggleService.init(applicationContext, mapOf(ToggleService.TOGGLE_KEY_SOCKET_REFACTOR to  BuildConfig.DEBUG))
    }
    companion object {
        lateinit var credentialsRepository: UserCredentialsRepository
           private set

        lateinit var dateFormatter: DateFormatter
           private set
    }
}