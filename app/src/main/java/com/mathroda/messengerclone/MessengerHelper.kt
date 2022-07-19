package com.mathroda.messengerclone

import android.content.Context
import com.mathroda.messengerclone.data.UserCredentials
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory

object MessengerHelper {

    fun initializeSdk(context: Context, apiKey: String) {

        val offlinePlugin = StreamOfflinePluginFactory(Config(
            userPresence = true,
            persistenceEnabled = true
        ), context )

        ChatClient.Builder(apiKey, context)
            .withPlugin(offlinePlugin)
            .build()
    }

    fun connectUser(
        userCredentials: UserCredentials,
        onSuccess: () -> Unit = {},
        onError: (ChatError) -> Unit = {}
    ) {
        ChatClient.instance().run {
            if (getCurrentUser() == null) {
                connectUser(userCredentials.user, userCredentials.token)
                    .enqueue() { result ->
                        if (result.isSuccess) {
                            MessengerApp.credentialsRepository.saveUserCredentials(userCredentials)
                            onSuccess()
                        } else {
                            onError(result.error())
                        }
                    }
            } else {
                onSuccess()
            }

        }
    }

    fun disconnectUser() {
        MessengerApp.credentialsRepository.clearCredentials()
        ChatClient.instance().disconnect()
    }
}