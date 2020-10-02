package id.train.widget1.utils.service

import android.app.*
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.NotificationCompat
import android.util.Log
import id.train.widget1.MyApplication.Companion.prefManager
import id.train.widget1.R
import id.train.widget1.utils.Constants.BROADCAST_STATUS
import id.train.widget1.utils.Constants.CONNECT_DISCONNECT
import id.train.widget1.utils.Constants.NOTIFICATION_KEY
import id.train.widget1.ui.MainActivity
import id.train.widget1.utils.broadcastWidgetVPNState
import id.train.widget1.utils.isOreoPlus

class DummyService: Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.v("My Service", "STARTING DUMMY SERVICE")
        val text = intent.getStringExtra(NOTIFICATION_KEY)

        when(intent.action) {
            CONNECT_DISCONNECT -> {
                createNotificationChannel()
                handleConnectDisconnect()
                Log.v("My Service", "CONNECT_DISCONNECT")
            }
            BROADCAST_STATUS -> {
                handleBroadcastStatus()
                Log.v("My Service", "BROADCAST_STATUS")
            }
        }

        startForeground(NOTIFICATION_ID, buildNotification(text ?: ""))
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        disconnectMe()
        prefManager.spCheckConnection =
            isConnected

        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (isOreoPlus()) {
            val notificationService = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(notificationService)
        }
    }

    private fun buildNotification(text: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this,
            CHANNEL_ID
        )
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_baseline_insert_chart_24)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun handleBroadcastStatus() {
        broadcastVPNStateChange(
            isConnected,
            isConnecting
        )
    }

    private fun broadcastVPNStateChange(isConnected: Boolean, isConnecting: Boolean) {
        broadcastWidgetVPNState(isConnected, isConnecting)
    }

    private fun handleConnectDisconnect() {
        if (getConnectedState()) {
            disconnectMe()
        } else {
            connectMe()
        }

        prefManager.spCheckConnection =
            isConnected
    }

    private fun connectMe() {
        isConnecting = true
        isConnected = true
        broadcastVPNStateChange(
            isConnected,
            isConnecting
        )
        Handler(Looper.getMainLooper()).postDelayed({
            isConnecting = false
            broadcastVPNStateChange(
                isConnected,
                isConnecting
            )
        }, 1500)
    }

    private fun disconnectMe() {
        isConnected = false
        isConnecting = false
        broadcastVPNStateChange(
            isConnected,
            isConnecting
        )
        stopForeground(true)
        stopSelf()
    }

    companion object {
        private const val CHANNEL_ID = "101"
        private const val CHANNEL_NAME = "Foreground Notification Channel"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_TITLE = "Dummy Service"

        private var isConnecting = false
        private var isConnected = false

        fun getConnectingState() =
            isConnecting
        fun getConnectedState() =
            isConnected
    }
}