package com.mathroda.messengerclone.data

import io.getstream.chat.android.client.models.User

data class UserCredentials(
    val apiKey: String,
    val user: User,
    val token: String
)
