package id.train.widget1.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import id.train.widget1.MyApplication.Companion.prefManager
import id.train.widget1.R
import id.train.widget1.utils.Constants.BROADCAST_STATUS
import id.train.widget1.utils.Constants.CONNECT_DISCONNECT
import id.train.widget1.utils.Constants.VPN_STATE_CHANGE
import id.train.widget1.utils.service.DummyService
import id.train.widget1.utils.sendIntentService
import id.train.widget1.ui.MainActivity

class MyWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        performUpdate(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        when(val action = intent.action) {
            VPN_STATE_CHANGE -> vpnStateChanged(context, intent)
            CONNECT_DISCONNECT -> context.sendIntentService(action)
            else -> super.onReceive(context, intent)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        Log.v("Widget Provider", "onAppWidgetOptionsChanged()")
        performUpdate(context)
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onEnabled(context: Context) {
        Log.v("Widget Provider", "onEnabled()")
        context.sendIntentService(BROADCAST_STATUS)
    }

    private fun performUpdate(context: Context) {
        val widgetManager = AppWidgetManager.getInstance(context)
        widgetManager.getAppWidgetIds(getComponentName(context)).forEach {
            val view = getRemoteView(widgetManager, context, it)
            setupButton(context, view)
            updateView(view, DummyService.getConnectingState(), DummyService.getConnectedState())
            widgetManager.updateAppWidget(it, view)
        }
    }

    private fun updateView(view: RemoteViews, isConnecting: Boolean, isConnected: Boolean) {
        Log.v("Widget Provider", "Connecting: $isConnecting || Connected: $isConnected")
        with(view) {
            if (!isConnecting) {
                setViewVisibility(R.id.progress_widget, View.GONE)
                setViewVisibility(R.id.btn_widget_start, View.VISIBLE)

                val drawableId = if (isConnected) R.drawable.ic_widget_on else R.drawable.ic_widget_off
                val widgetText = if (isConnected) MainActivity.CONNECTED_KEY else MainActivity.NOT_CONNECTED_KEY

                setTextViewCompoundDrawablesRelative(
                    R.id.btn_widget_start,
                    0,
                    0,
                    drawableId,
                    0
                )
                setTextViewText(
                    R.id.btn_widget_start,
                    widgetText
                )
            } else {
                setViewVisibility(R.id.btn_widget_start, View.GONE)
                setViewVisibility(R.id.progress_widget, View.VISIBLE)
            }
        }
    }

    private fun setupButton(context: Context, view: RemoteViews) {
        setupIntent(context, view)
    }

    private fun setupIntent(context: Context, view: RemoteViews, action: String = CONNECT_DISCONNECT) {
        val intent = Intent(context, MyWidgetProvider::class.java)
        intent.action = action
        val pendingIntent = PendingIntent.getBroadcast(context, 0 , intent, 0)
        view.setOnClickPendingIntent(R.id.btn_widget_start, pendingIntent)
    }

    private fun vpnStateChanged(context: Context, intent: Intent) {
        val isConnected = intent.getBooleanExtra(MainActivity.CONNECTED_KEY, false)
        val isConnecting = intent.getBooleanExtra(MainActivity.CONNECTING_KEY, false)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.getAppWidgetIds(getComponentName(context)).forEach {
            val view = getRemoteView(appWidgetManager, context, it)
            updateView(view, isConnecting, isConnected)
            appWidgetManager.updateAppWidget(it, view)
        }
    }

    private fun getRemoteView(appWidgetManager: AppWidgetManager, context: Context, widgetId: Int) : RemoteViews {
        val option = appWidgetManager.getAppWidgetOptions(widgetId)
        val minHeight = option.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

        if (widgetId == prefManager.spWidgetIdToMeasure && prefManager.spInitialSize == 0) {
            prefManager.spInitialSize = minHeight
        }

        val layoutId = R.layout.app_widget

        return RemoteViews(context.packageName, layoutId)
    }

    companion object {
        private fun getComponentName(context: Context) = ComponentName(context, MyWidgetProvider::class.java)
    }
}