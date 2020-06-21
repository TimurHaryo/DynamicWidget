package id.train.widget1.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import id.train.widget1.MainActivity
import id.train.widget1.R

class AppWidget : AppWidgetProvider(), IWidgetViewCallback {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        MainActivity.widgetViewCallback = this

        for (id in appWidgetIds) {
            updateAppWidget(
                context,
                id
            )
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        MainActivity.widgetViewCallback = this
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)

        Log.d("Widget Receive", "CLICKED!")

        if (WIDGET_CLICKED.equals(intent?.action)) {

            if (!isConnected) {
                onConnecting(context)
                Handler().postDelayed({
                    onConnected(context)
                }, 3000)
            } else {
                onDisconnected(context)
            }
        }
    }

    override fun onConnected(context: Context) {
        isConnected = true
        doUpdateAppWidget(context, MainActivity.CONNECTED_KEY)
    }

    override fun onConnecting(context: Context) {
        doUpdateAppWidget(context, MainActivity.CONNECTING_KEY)
    }

    override fun onDisconnected(context: Context) {
        isConnected = false
        doUpdateAppWidget(context, MainActivity.NOT_CONNECTED_KEY)
    }

    override fun onWidgetDestroy(context: Context) {
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        val thisWidget = ComponentName(context, AppWidget::class.java)
        val manager = AppWidgetManager.getInstance(context)

        views.setTextViewText(R.id.btn_widget_start, MainActivity.NOT_CONNECTED_KEY)
        views.setTextViewCompoundDrawablesRelative(
            R.id.btn_widget_start,
            0,
            0,
            R.drawable.ic_widget_on,
            0
        )
        isConnected = false
        MainActivity.isMainAppsRunning = null
        views.setViewVisibility(R.id.progress_widget, View.GONE)

        manager.updateAppWidget(thisWidget, views)
    }

    private fun doUpdateAppWidget(context: Context, action: String) {
        updateAppWidget(
            context,
            null,
            action
        )
    }

    private fun updateAppWidget(
        context: Context,
        id: Int?,
        action: String? = MainActivity.NOT_CONNECTED_KEY
    ) {
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        val thisWidget = ComponentName(context, AppWidget::class.java)
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(thisWidget)

        Log.d("Widget Update", isConnected.toString() +"\t"+ action.toString())

        var pendingWidgetIntent: PendingIntent
        var intentVpn: Intent

        try {
            isMainAppsRunning = MainActivity.isMainAppsRunning!!
            intentVpn = Intent(context, AppWidget::class.java)
            hasRun = true
        }catch (e: KotlinNullPointerException) {
            intentVpn = Intent(context, MainActivity::class.java)
            hasRun = false
        }

        intentVpn.action = WIDGET_CLICKED
        intentVpn.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        when (action) {
            MainActivity.NOT_CONNECTED_KEY -> {
                intentVpn.putExtra(MainActivity.WIDGET_KEY, WIDGET_CONNECT)
                views.setTextViewText(R.id.btn_widget_start, MainActivity.NOT_CONNECTED_KEY)
                views.setTextViewCompoundDrawablesRelative(
                    R.id.btn_widget_start,
                    0,
                    0,
                    R.drawable.ic_widget_on,
                    0
                )
                isConnected = false
                views.setViewVisibility(R.id.progress_widget, View.GONE)
            }
            MainActivity.CONNECTED_KEY -> {
                intentVpn.putExtra(MainActivity.WIDGET_KEY, WIDGET_DISCONNECT)
                views.setTextViewText(R.id.btn_widget_start, MainActivity.CONNECTED_KEY)
                views.setTextViewCompoundDrawablesRelative(
                    R.id.btn_widget_start,
                    0,
                    0,
                    R.drawable.ic_widget_off,
                    0
                )
                isConnected = true
                views.setViewVisibility(R.id.progress_widget, View.GONE)
            }
            MainActivity.CONNECTING_KEY -> {
                intentVpn.putExtra(MainActivity.WIDGET_KEY, WIDGET_CONNECTING)
                views.setTextViewText(R.id.btn_widget_start, MainActivity.CONNECTING_KEY)
                views.setTextViewCompoundDrawablesRelative(
                    R.id.btn_widget_start,
                    0,
                    0,
                    R.drawable.ic_widget_loading,
                    0
                )
                views.setViewVisibility(R.id.progress_widget, View.VISIBLE)
            }
            else -> {
                intentVpn.putExtra(MainActivity.WIDGET_KEY, "nothing")
            }
        }

        pendingWidgetIntent = if (hasRun) {
            PendingIntent.getBroadcast(context, 0, intentVpn, 0)
        } else {
            PendingIntent.getActivity(context, 0, intentVpn, 0)
        }

//        if (isMainAppsRunning) {
//            pendingWidgetIntent = PendingIntent.getBroadcast(context, 0, intentVpn, PendingIntent.FLAG_CANCEL_CURRENT)
//        } else {
//            intentVpn = Intent(context, MainActivity::class.java)
//
//            when (action) {
//                MainActivity.NOT_CONNECTED_KEY -> {
//                    intentVpn.putExtra(MainActivity.WIDGET_KEY, WIDGET_CONNECT)
//                    views.setTextViewText(R.id.btn_widget_start, MainActivity.NOT_CONNECTED_KEY)
//                    views.setTextViewCompoundDrawablesRelative(
//                        R.id.btn_widget_start,
//                        0,
//                        0,
//                        R.drawable.ic_widget_on,
//                        0
//                    )
//                    views.setViewVisibility(R.id.progress_widget, View.GONE)
//                }
//                MainActivity.CONNECTED_KEY -> {
//                    intentVpn.putExtra(MainActivity.WIDGET_KEY, WIDGET_DISCONNECT)
//                    views.setTextViewText(R.id.btn_widget_start, MainActivity.CONNECTED_KEY)
//                    views.setTextViewCompoundDrawablesRelative(
//                        R.id.btn_widget_start,
//                        0,
//                        0,
//                        R.drawable.ic_widget_off,
//                        0
//                    )
//                    views.setViewVisibility(R.id.progress_widget, View.GONE)
//                }
//                MainActivity.CONNECTING_KEY -> {
//                    intentVpn.putExtra(MainActivity.WIDGET_KEY, WIDGET_CONNECTING)
//                    views.setTextViewText(R.id.btn_widget_start, MainActivity.CONNECTING_KEY)
//                    views.setTextViewCompoundDrawablesRelative(
//                        R.id.btn_widget_start,
//                        0,
//                        0,
//                        R.drawable.ic_widget_loading,
//                        0
//                    )
//                    views.setViewVisibility(R.id.progress_widget, View.VISIBLE)
//                }
//                else -> {
//                    intentVpn.putExtra(MainActivity.WIDGET_KEY, "nothing")
//                }
//            }
//
//            pendingWidgetIntent = PendingIntent.getActivity(context, 0, intentVpn, PendingIntent.FLAG_CANCEL_CURRENT)
//        }

        views.setOnClickPendingIntent(R.id.btn_widget_start, pendingWidgetIntent)

        if (id != null) {
            manager.updateAppWidget(id, views)
        } else {
            manager.updateAppWidget(thisWidget, views)
        }

    }

    companion object {
        const val WIDGET_CLICKED = "onClickWidget"

        const val WIDGET_CONNECTING = "connecting"
        const val WIDGET_CONNECT = "connect"
        const val WIDGET_DISCONNECT = "disconnect"

        private var isConnected = false
        var isMainAppsRunning = false
        var hasRun = false
    }
}

