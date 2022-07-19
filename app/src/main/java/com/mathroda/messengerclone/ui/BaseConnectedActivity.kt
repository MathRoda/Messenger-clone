package com.mathroda.messengerclone.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseConnectedActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null && lastNonConfigurationInstance ==null) {
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
            finishAffinity()
        }
    }
}