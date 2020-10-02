package id.train.widget1.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import id.train.widget1.utils.Constants.NOTIFICATION_KEY
import id.train.widget1.utils.Constants.VPN_STATE_CHANGE
import id.train.widget1.ui.MainActivity
import id.train.widget1.utils.service.DummyService
import id.train.widget1.widget.MyWidgetProvider
import java.lang.Exception

@SuppressLint("NewApi")
fun Context.sendIntentService(action: String) {
    Intent(this, DummyService::class.java).apply {
        this.action = action
        try {
            if (isOreoPlus()) {
                putExtra(NOTIFICATION_KEY, "Foreground Service Is Running")
                startForegroundService(this)
            } else {
                startService(this)
            }
        } catch (ignored: Exception) {
        }
    }
}

fun Context.broadcastWidgetVPNState(isConnected: Boolean, isConnecting: Boolean) {
    Intent(this, MyWidgetProvider::class.java).apply {
        putExtra(MainActivity.CONNECTED_KEY, isConnected)
        putExtra(MainActivity.CONNECTING_KEY, isConnecting)
        action = VPN_STATE_CHANGE
        sendBroadcast(this)
    }
}

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O