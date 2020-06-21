package id.train.widget1.widget

import android.content.Context

interface IWidgetViewCallback {
    fun onConnected(context: Context)
    fun onConnecting(context: Context)
    fun onDisconnected(context: Context)
    fun onWidgetDestroy(context: Context)
}