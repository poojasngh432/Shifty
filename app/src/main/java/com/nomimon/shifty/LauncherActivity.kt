package com.nomimon.shifty

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle

class LauncherActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onResume() {
        super.onResume()
        if (BaseApp.isLoggedIn) {
            val intent =  Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intent =  Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}