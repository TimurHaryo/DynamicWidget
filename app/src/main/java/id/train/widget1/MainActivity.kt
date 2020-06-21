package id.train.widget1

import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import id.train.widget1.widget.AppWidget
import id.train.widget1.widget.IWidgetViewCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var isConnected = false
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            checkWidget()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
        checkWidget()
    }

    override fun onDestroy() {
        intent.removeExtra(WIDGET_KEY)
        widgetViewCallback?.onWidgetDestroy(this)
        super.onDestroy()
    }

    override fun onStart() {
        isMainAppsRunning = true
        registerReceiver(broadcastReceiver, IntentFilter(AppWidget.WIDGET_CLICKED))
        super.onStart()
    }

    override fun onStop() {
        isMainAppsRunning = null
        unregisterReceiver(broadcastReceiver)
        super.onStop()
    }

    private fun checkWidget() {
        var resultWidget = intent.getStringExtra(WIDGET_KEY)

        if (resultWidget == null) resultWidget = "nothing"

        Log.d("From Widget", resultWidget.toString())

        when (resultWidget) {
            AppWidget.WIDGET_CONNECT -> {
                connectingView()
                Handler().postDelayed({
                    connectedView()
                }, 3000)
            }
            AppWidget.WIDGET_DISCONNECT -> {
                notConnectedView()
            }
            AppWidget.WIDGET_CONNECTING -> {
                connectingView()
            }
            else -> {
                Log.e("From Widget", "Error widget")
            }
        }
    }

    private fun setupView() {
        notConnectedView()
        btn_start.setOnClickListener {
            if (!isConnected) {
                connectingView()
                Handler().postDelayed({
                    connectedView()
                }, 3000)
            } else {
                notConnectedView()
            }
        }
    }

    private fun connectedView() {
        progress_home.visibility = View.INVISIBLE
        btn_start.setBackgroundColor(Color.GREEN)
        isConnected = true
        widgetViewCallback?.onConnected(this)
    }

    private fun notConnectedView() {
        progress_home.visibility = View.INVISIBLE
        btn_start.setBackgroundColor(Color.RED)
        isConnected = false
        widgetViewCallback?.onDisconnected(this)
    }

    private fun connectingView() {
        progress_home.visibility = View.VISIBLE
        widgetViewCallback?.onConnecting(this)
    }

    companion object {
        const val CONNECTED_KEY = "CONNECTED"
        const val NOT_CONNECTED_KEY = "NOT CONNECTED"
        const val CONNECTING_KEY = "CONNECTING . . ."
        const val WIDGET_KEY = "MY WIDGET"

        var widgetViewCallback : IWidgetViewCallback? =null
        var isMainAppsRunning: Boolean? = null
    }
}